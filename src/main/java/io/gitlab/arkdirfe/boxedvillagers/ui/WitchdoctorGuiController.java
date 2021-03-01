package io.gitlab.arkdirfe.boxedvillagers.ui;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.data.TradeData;
import io.gitlab.arkdirfe.boxedvillagers.data.VillagerData;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiManager;
import io.gitlab.arkdirfe.boxedvillagers.util.GuiUtil;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import io.gitlab.arkdirfe.boxedvillagers.util.Util;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;

public class WitchdoctorGuiController
{
    private final Inventory gui;
    private final HumanEntity player;
    private ItemStack scroll = null;
    private VillagerData villagerData = null;

    private boolean tradesMoved;
    private int tradesPurged = 0;

    private final int scrollSlot;
    private final int helpSlot;
    private final int cureSlot;
    private final int commitSlot;
    private final List<Integer> tradeSlots;

    private final Map<Material, Integer> cureCost = new HashMap<>();
    private final Map<Material, Integer> commitCost = new HashMap<>();

    private final WitchdoctorGuiManager manager;

    public WitchdoctorGuiController(Inventory gui, HumanEntity player, int scrollSlot, int helpSlot, int cureSlot, int commitSlot, List<Integer> tradeSlots, WitchdoctorGuiManager manager)
    {
        this.player = player;
        this.gui = gui;
        this.scrollSlot = scrollSlot;
        this.helpSlot = helpSlot;
        this.cureSlot = cureSlot;
        this.commitSlot = commitSlot;
        this.tradeSlots = tradeSlots;
        this.manager = manager;

        player.openInventory(gui);
        init();
    }

    // --- Getters

    public Inventory getGui()
    {
        return gui;
    }

    public ItemStack getScroll()
    {
        return scroll;
    }

    public VillagerData getVillagerData()
    {
        return villagerData;
    }

    public HumanEntity getPlayer()
    {
        return player;
    }

    // --- Setters/Accessors

    public void tradeMoved()
    {
        tradesMoved = true;
    }

    public void tradePurged()
    {
        tradesPurged++;
    }

    public void setScroll(ItemStack scroll)
    {
        this.scroll = scroll;
        if(scroll != null)
        {
            villagerData = new VillagerData(new NBTItem(scroll));
        }
        else
        {
            villagerData = null;
        }
    }

    // --- General Methods

    public boolean canCommit()
    {
        return (tradesMoved || tradesPurged > 0);
    }

    public void resetTracking()
    {
        tradesMoved = false;
        tradesPurged = 0;
    }

    public void commitChanges()
    {
        if(playerCanPay(commitCost))
        {
            payCosts(commitCost);

            villagerData.setTrades(getModifiedTrades());
            Util.updateBoundScrollTooltip(scroll, villagerData);
            gui.setItem(scrollSlot, villagerData.writeToItem(new NBTItem(scroll)));
            resetTracking();
            updateCommitButton();
        }
    }

    public void cureVillager()
    {
        if(playerCanPay(cureCost))
        {
            payCosts(cureCost);

            villagerData.cure(new NBTItem(scroll), 1);
            Util.updateBoundScrollTooltip(scroll, villagerData);
            gui.setItem(scrollSlot, villagerData.writeToItem(new NBTItem(scroll)));
            ((Player)player).playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.5f, 1);
            updateCureButton();
        }
    }

    public void purgeTrade(int slot)
    {
        gui.setItem(slot, null);

        // Sound maybe
        tradesPurged++;
        updateCommitButton();
    }

    // --- UI Update Methods

    private void init()
    {
        ItemStack[] items = new ItemStack[54];

        for (int i = 0; i < 54; i++)
        {
            items[i] = getFillerItem(Material.LIME_STAINED_GLASS_PANE);
        }

        items[helpSlot] = getScrollIndicator();
        items[scrollSlot] = null;

        gui.setContents(items);
    }

    public void update()
    {
        if(scroll == null)
        {
            init();
        }
        else
        {
            ItemStack[] items = gui.getContents();

            items[helpSlot] = getHelpScroll();

            // Draw trades

            int index = 0;

            for(int i : tradeSlots)
            {
                if(index >= villagerData.getTrades().size())
                {
                    items[i] = null;
                    continue;
                }

                TradeData trade = villagerData.getTrades().get(index++);
                if(trade != null)
                {
                    items[i] = getTradeItem(trade);
                }
                else
                {
                    items[i] = null;
                }
            }

            gui.setContents(items);
            updateCureButton();
            updateCommitButton();
        }
    }

    public void updateCureButton()
    {
        if(villagerData.getCures() == 7)
        {
            gui.setItem(cureSlot, getBlockedCureItem());
        }
        else
        {
            gui.setItem(cureSlot, getCureItem(villagerData.getCures()));
        }
    }

    public void updateCommitButton()
    {
        gui.setItem(commitSlot, getCommitItem());
    }

    // --- UI Element Generator Methods

    private ItemStack getFillerItem(Material material)
    {
        ItemStack item = new ItemStack(material);

        Util.setItemTitleLoreAndFlags(item, " ", null, null);

        return GuiUtil.setUninteractable(item);
    }

    private ItemStack getScrollIndicator()
    {
        ItemStack item = new ItemStack(Material.PAPER);

        Util.setItemTitleLoreAndFlags(item, "§2Villager Scroll",
                Arrays.asList("§r§fPlace your bound scroll below to begin the process."),
                Arrays.asList(ItemFlag.HIDE_ENCHANTS));

        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        return GuiUtil.setUninteractable(item);
    }

    private ItemStack getHelpScroll()
    {
        ItemStack item = new ItemStack(Material.PAPER);

        Util.setItemTitleLoreAndFlags(item, "§2Villager Scroll",
                Arrays.asList("§r§fEdit trades below.",
                        "§r§fUse the button on the right to commit your changes.",
                        "§r§fNote: Prices shown ignore cures."),
                Arrays.asList(ItemFlag.HIDE_ENCHANTS));

        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        return GuiUtil.setUninteractable(item);
    }

    private ItemStack getCureItem(int cures)
    {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);

        List<String> lore = new ArrayList<>();
        lore.add("§r§fReduces all prices but never below 1.");
        lore.add("§r§4Applies instantly, irreversible.");

        calculateCureCost();

        if(cureCost.size() > 0)
        {
            lore.add("§r§fCosts:");
            for(Map.Entry<Material, Integer> entry : cureCost.entrySet())
            {
                lore.add(String.format("§r§f   -§6%d §a%s", entry.getValue(), Strings.capitalize(entry.getKey().toString(), "_")));
            }
        }

        Util.setItemTitleLoreAndFlags(item, "§2Cure Villager",
                lore,
                null);

        return GuiUtil.setUninteractable(item);
    }

    private ItemStack getBlockedCureItem()
    {
        ItemStack item = new ItemStack(Material.APPLE);

        Util.setItemTitleLoreAndFlags(item, "§2Cure Villager",
                Arrays.asList("§r§fVillager is at max cures!"),
                null);

        return GuiUtil.setUninteractable(item);
    }

    private ItemStack getCommitItem()
    {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);

        List<String> lore = new ArrayList<>();

        if(!tradesMoved && tradesPurged == 0)
        {
            lore.add("§r§fNo changes to commit!");
        }
        else
        {
            lore.add("§r§fUncommitted changes!");
            item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        }

        if(tradesMoved)
        {
            lore.add("§r§fTrades were moved.");
        }
        if(tradesPurged > 0)
        {
            lore.add(String.format("§r§6%d§f trades were purged.", tradesPurged));
        }

        calculateCommitCost();

        if(tradesPurged > 0 && commitCost.size() > 0)
        {
            lore.add("§r§fTotal Costs:");
            for (Map.Entry<Material, Integer> entry : commitCost.entrySet())
            {
                lore.add(String.format("§r§f   -§6%d §a%s", entry.getValue(), Strings.capitalize(entry.getKey().toString(), "_")));
            }
        }

        Util.setItemTitleLoreAndFlags(item, "§2Commit Changes",
                lore,
                Arrays.asList(ItemFlag.HIDE_ENCHANTS));

        return GuiUtil.setUninteractable(item);
    }

    private ItemStack getTradeItem(TradeData trade)
    {
        ItemStack item = new ItemStack(Material.PAPER);

        Util.setItemTitleLoreAndFlags(item, "§aStored Trade",
                Arrays.asList(tradeToString(trade.getRecipe(), trade.getBaseAmount()),
                        "§r§fPrice reduced by §6" + trade.getReduction() + "§f for each cure.",
                        "§r§fShift Left Click to purge this trade."),
                null);

        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.addCompound(Strings.TAG_SERIALIZED_TRADE_DATA);
        trade.serializeToNBT(compound);

        return GuiUtil.setMovable(nbtItem.getItem());
    }

    public List<TradeData> getModifiedTrades()
    {
        List<TradeData> trades = new ArrayList<>();

        for(int i : tradeSlots)
        {
            ItemStack item = gui.getItem(i);

            if(Util.isNotNullOrAir(item))
            {
                NBTItem nbtItem = new NBTItem(item);
                TradeData tradeData = new TradeData(nbtItem.getCompound(Strings.TAG_SERIALIZED_TRADE_DATA), villagerData.getCures());
                trades.add(tradeData);
            }
        }

        return trades;
    }

    // --- UI Utility Methods

    private void calculateCureCost()
    {
        cureCost.clear();

        int cures = villagerData.getCures();
        int multiplier = cures + 1;

        for (Map.Entry<Material, Integer> entry : manager.cureTier1CostMap.entrySet())
        {
            Material key = entry.getKey();
            cureCost.put(key, cureCost.getOrDefault(key, 0) + entry.getValue() * multiplier);
        }
        if (cures >= 3)
        {
            for (Map.Entry<Material, Integer> entry : manager.cureTier2CostMap.entrySet())
            {
                Material key = entry.getKey();
                cureCost.put(key, cureCost.getOrDefault(key, 0) + entry.getValue() * (multiplier - 3));
            }
        }
        if (cures >= 5)
        {
            for (Map.Entry<Material, Integer> entry : manager.cureTier3CostMap.entrySet())
            {
                Material key = entry.getKey();
                cureCost.put(key, cureCost.getOrDefault(key, 0) + entry.getValue() * (multiplier - 5));
            }
        }
    }

    private void calculateCommitCost()
    {
        commitCost.clear();

        for (Map.Entry<Material, Integer> entry : manager.purgeCostMap.entrySet())
        {
            commitCost.put(entry.getKey(), entry.getValue() * tradesPurged);
        }
    }

    private String tradeToString(MerchantRecipe recipe, int baseAmount)
    {
        ItemStack i1 = recipe.getIngredients().get(0);
        ItemStack i2 = recipe.getIngredients().get(1);
        ItemStack output = recipe.getResult();

        StringBuilder result = new StringBuilder();
        result.append("§r§f");
        result.append(String.format("§6%d §a%s§f", baseAmount, Strings.capitalize(i1.getType().getKey().getKey(), "_")));
        if(i2.getType() != Material.AIR)
        {
            result.append(String.format(" + §6%d§f §a%s§f", i2.getAmount(), Strings.capitalize(i2.getType().getKey().getKey(), "_")));
        }
        result.append(String.format(" = §6%d§f §a%s§f", output.getAmount(), Strings.capitalize(output.getType().getKey().getKey(), "_")));
        if(output.getType() == Material.ENCHANTED_BOOK)
        {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta)output.getItemMeta();
            for(Map.Entry<Enchantment, Integer> ench : meta.getStoredEnchants().entrySet())
            {
                result.append(String.format(" §5(%s %s)§f", Strings.capitalize(ench.getKey().getKey().getKey(), "_"), Strings.numberToRoman(ench.getValue())));
            }
        }

        return result.toString();
    }

    private boolean playerCanPay(Map<Material, Integer> costs)
    {
        for (Map.Entry<Material, Integer> entry : costs.entrySet())
        {
            if(!player.getInventory().containsAtLeast(new ItemStack(entry.getKey()), entry.getValue()))
            {
                return false;
            }
        }

        return true;
    }

    private void payCosts(Map<Material, Integer> costs)
    {
        for (Map.Entry<Material, Integer> entry : costs.entrySet())
        {
            ItemStack item = new ItemStack(entry.getKey());
            item.setAmount(entry.getValue());
            player.getInventory().removeItem(item);
        }
    }
}
