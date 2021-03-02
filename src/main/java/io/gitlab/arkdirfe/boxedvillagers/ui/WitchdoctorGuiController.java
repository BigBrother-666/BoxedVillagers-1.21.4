package io.gitlab.arkdirfe.boxedvillagers.ui;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.data.CostData;
import io.gitlab.arkdirfe.boxedvillagers.data.TradeData;
import io.gitlab.arkdirfe.boxedvillagers.data.VillagerData;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class WitchdoctorGuiController
{
    private final Inventory gui;
    private final HumanEntity player;
    private ItemStack scroll = null;
    private VillagerData villagerData = null;

    private boolean tradesMoved;
    private int tradesPurged = 0;
    private int tradesExtracted = 0;

    private int tradeSlotEnd;

    private final boolean admin;

    private final WitchdoctorGuiManager manager;

    public WitchdoctorGuiController(Inventory gui, HumanEntity player,  WitchdoctorGuiManager manager, boolean admin)
    {
        this.player = player;
        this.gui = gui;
        this.manager = manager;
        this.admin = admin;
        tradeSlotEnd = manager.tradeSlotStart;

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

    public void setScroll(ItemStack scroll)
    {
        if(Util.isNotNullOrAir(scroll))
        {
            villagerData = new VillagerData(new NBTItem(scroll));
            this.scroll = scroll;
        }
        else
        {
            villagerData = null;
            this.scroll = null;
        }
    }

    // --- UI Update Methods

    private void init()
    {
        ItemStack[] items = new ItemStack[54];

        for (int i = 0; i < 54; i++)
        {
            items[i] = getFillerItem(Material.LIME_STAINED_GLASS_PANE);
        }

        items[manager.buyScrollSlot] = getBuyScrollItem();
        items[manager.helpSlot] = getNoScrollHelpItem();
        items[manager.scrollSlot] = null;

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

            items[manager.buyScrollSlot] = getBuyScrollItem();
            items[manager.extendTradeSlotsSlot] = getSlotExtensionItem();
            items[manager.helpSlot] = getScrollHelpItem();

            if(player.hasPermission(Strings.PERM_WITCHDOCTOR_ADVANCED))
            {
                // Draw trades
                int index = 0;

                tradeSlotEnd = manager.tradeSlotStart + villagerData.getTradeSlots();

                for(int i = manager.tradeSlotStart; i < tradeSlotEnd; i++)
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
            }

            gui.setContents(items);
            updateCureButton();
            updateCommitButton();
        }
    }

    public void updateCureButton()
    {
        gui.setItem(manager.cureSlot, getCureItem(villagerData.getCures()));
    }

    public void updateCommitButton()
    {
        if(player.hasPermission(Strings.PERM_WITCHDOCTOR_ADVANCED))
        {
            gui.setItem(manager.commitSlot, getCommitItem());
        }
    }

    // --- General Methods

    public boolean isTradeSlot(int slot)
    {
        return slot >= manager.tradeSlotStart && slot < tradeSlotEnd;
    }

    public boolean canCommit()
    {
        int free = getFreeTradeItems().size();
        return (tradesMoved || tradesPurged > 0 || tradesExtracted > 0 || free > 0);
    }

    public void resetTracking()
    {
        tradesMoved = false;
        tradesPurged = 0;
        tradesExtracted = 0;
    }

    public void commitChanges()
    {
        CostData commitCost = calculateCommitCost();

        if(playerCanPay(commitCost) || admin)
        {
            if(!admin)
            {
                payCosts(commitCost);
            }

            List<ItemStack> extracted = getExtractedTradeItems();

            for(ItemStack item : extracted)
            {
                gui.remove(item);

                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§aExtracted Trade");
                List<String> lore = new ArrayList<>();
                lore.add(meta.getLore().get(0));
                lore.add(meta.getLore().get(1));
                lore.add("§r§fActs like a regular trade in the Witch Doctor.");
                lore.add("§r§fGets added to scroll if committed.");
                meta.setLore(lore);
                item.setItemMeta(meta);

                player.getInventory().addItem(GuiUtil.setFree(item));
            }

            villagerData.setTrades(getModifiedTrades());
            Util.updateBoundScrollTooltip(scroll, villagerData);
            gui.setItem(manager.scrollSlot, villagerData.writeToItem(new NBTItem(scroll)));
            ((Player)player).playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            resetTracking();

            update();
        }
        else
        {
            ((Player)player).playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        }
    }

    public void cureVillager()
    {
        CostData cureCost = calculateCureCost();

        if(playerCanPay(cureCost) || admin)
        {
            if(!admin)
            {
                payCosts(cureCost);
            }

            villagerData.cure(new NBTItem(scroll), 1);
            Util.updateBoundScrollTooltip(scroll, villagerData);
            gui.setItem(manager.scrollSlot, villagerData.writeToItem(new NBTItem(scroll)));
            ((Player)player).playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.5f, 1);
            updateCureButton();
        }
        else
        {
            ((Player)player).playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1);
        }
    }

    public void purgeTrade(int slot)
    {
        gui.setItem(slot, null);

        ((Player)player).playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        tradesPurged++;
        updateCommitButton();
    }

    public void buyScroll()
    {
        CostData scrollCost = calculateScrollCost();

        if(playerCanPay(scrollCost) && player.getInventory().firstEmpty() != -1)
        {
            payCosts(scrollCost);
            player.getInventory().addItem(Util.getUnboundScroll(false));
            ((Player)player).playSound(player.getLocation(), Sound.ENTITY_VILLAGER_WORK_LIBRARIAN, 1f, 1);
        }
        else
        {
            ((Player)player).playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1);
        }
    }

    public void extendSlots()
    {
        if(villagerData.getTradeSlots() == VillagerData.maxTradeSlots)
        {
            return;
        }

        CostData slotCost = calculateSlotExtensionCost();

        if(playerCanPay(slotCost))
        {
            payCosts(slotCost);

            villagerData.addTradeSlots(new NBTItem(scroll), 1);
            Util.updateBoundScrollTooltip(scroll, villagerData);
            gui.setItem(manager.scrollSlot, villagerData.writeToItem(new NBTItem(scroll)));
            ((Player)player).playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1);
            update();
        }
        else
        {
            ((Player)player).playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1);
        }
    }

    public void extractTrade(int slot) // Already null checked by manager
    {
        ItemStack item = gui.getItem(slot);

        if(GuiUtil.isFree(item) || GuiUtil.isExtracted(item))
        {
            return;
        }

        item.setType(Material.SUGAR_CANE);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aExtracted Trade");
        List<String> lore = new ArrayList<>();
        lore.add(meta.getLore().get(0));
        lore.add(meta.getLore().get(1));
        lore.add("§r§fCommit to receive item.");
        meta.setLore(lore);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        gui.setItem(slot, GuiUtil.setExtracted(item));

        tradesExtracted++;

        updateCommitButton();
    }

    // --- UI Element Generator Methods

    private ItemStack getFillerItem(Material material)
    {
        ItemStack item = new ItemStack(material);

        Util.setItemTitleLoreAndFlags(item, " ", null, null);

        return GuiUtil.setUninteractable(item);
    }

    private ItemStack getNoScrollHelpItem()
    {
        ItemStack item = new ItemStack(Material.PAPER);

        Util.setItemTitleLoreAndFlags(item, "§2Help",
                Arrays.asList("§r§fPlace your bound scroll below to begin the process.",
                        "§r§fYou can purchase scrolls at the right."),
                Arrays.asList(ItemFlag.HIDE_ENCHANTS));

        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        return GuiUtil.setUninteractable(item);
    }

    private ItemStack getScrollHelpItem()
    {
        ItemStack item = new ItemStack(Material.PAPER);

        List<String> lore = new ArrayList<>();

        lore.add("§r§fEdit trades below.");
        lore.add("§r§fYou can purchase scrolls at the right.");
        lore.add("§r§fUse the buttons on the left to upgrade your villager.");

        if(player.hasPermission(Strings.PERM_WITCHDOCTOR_ADVANCED))
        {
            lore.add("§r§fUse the button on the right to commit your changes.");
            lore.add("§r§fNote: Prices shown below ignore cures.");
        }

        Util.setItemTitleLoreAndFlags(item, "§2Help",
                lore,
                Arrays.asList(ItemFlag.HIDE_ENCHANTS));

        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        return GuiUtil.setUninteractable(item);
    }

    private ItemStack getSlotExtensionItem()
    {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);

        List<String> lore = new ArrayList<>();

        if(villagerData.getTradeSlots() < VillagerData.maxTradeSlots)
        {
            lore.add(String.format("§r§fYour villager hold up to §6%d§f trades.", VillagerData.maxTradeSlots));
            lore.add(String.format("§r§fIt can currently hold §6%d§f.", villagerData.getTradeSlots()));
            lore.add("§r§4Applies instantly, irreversible.");

            CostData slotCost = calculateSlotExtensionCost();

            if(slotCost.hasCost())
            {
                lore.addAll(costToString(slotCost));
            }
        }
        else
        {
            lore.add("§r§fYour villager has full trade slots.");
            item.setType(Material.BOOK);
        }

        Util.setItemTitleLoreAndFlags(item, "§2Extend Trade Slots",
                lore,
                null);

        return GuiUtil.setUninteractable(item);
    }

    private ItemStack getBuyScrollItem()
    {
        ItemStack item = new ItemStack(Material.PAPER);

        List<String> lore = new ArrayList<>();
        lore.add("§r§fUse it to capture villagers.");

        CostData scrollCost = calculateScrollCost();

        if(scrollCost.hasCost())
        {
            lore.addAll(costToString(scrollCost));
        }

        Util.setItemTitleLoreAndFlags(item, "§2Buy Villager Scroll",
                lore,
                null);

        return GuiUtil.setUninteractable(item);
    }

    private ItemStack getCureItem(int cures)
    {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);

        List<String> lore = new ArrayList<>();

        if(villagerData.getCures() != 7)
        {
            lore.add("§r§fReduces all prices but never below 1.");
            lore.add("§r§4Applies instantly, irreversible.");

            CostData cureCost = calculateCureCost();

            if(cureCost.hasCost())
            {
                lore.addAll(costToString(cureCost));
            }
        }
        else
        {
            lore.add("§r§fVillager is at max cures!");
            item.setType(Material.APPLE);
        }

        Util.setItemTitleLoreAndFlags(item, "§2Cure Villager",
                lore,
                null);

        return GuiUtil.setUninteractable(item);
    }

    private ItemStack getCommitItem()
    {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);

        List<String> lore = new ArrayList<>();

        int free = getFreeTradeItems().size();

        if(!tradesMoved && tradesPurged == 0 && tradesExtracted == 0 && free == 0)
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

        if(tradesExtracted > 0)
        {
            lore.add(String.format("§r§6%d§f trades were extracted.", tradesExtracted));
        }

        if(free > 0)
        {
            lore.add(String.format("§r§6%d§f new trades were added.", free));
        }

        CostData commitCost = calculateCommitCost();

        if(commitCost.hasCost())
        {
            lore.addAll(costToString(commitCost));
        }

        Util.setItemTitleLoreAndFlags(item, "§2Commit Changes",
                lore,
                Arrays.asList(ItemFlag.HIDE_ENCHANTS));

        return GuiUtil.setUninteractable(item);
    }

    private ItemStack getTradeItem(TradeData trade)
    {
        ItemStack item = new ItemStack(Material.PAPER);

        List<String> lore = new ArrayList<>();

        lore.add(tradeToString(trade.getRecipe(), trade.getBaseAmount()));
        lore.add("§r§fPrice reduced by §6" + trade.getReduction() + "§f for each cure.");
        lore.add("§r§fShift Left Click to purge this trade.");

        if(player.hasPermission(Strings.PERM_WITCHDOCTOR_EXTRACT))
        {
            lore.add("§r§fShift Right Click to extract this trade.");
        }

        Util.setItemTitleLoreAndFlags(item, "§aStored Trade",
                lore,
                Arrays.asList(ItemFlag.HIDE_ENCHANTS));

        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.addCompound(Strings.TAG_SERIALIZED_TRADE_DATA);
        trade.serializeToNBT(compound);

        return GuiUtil.setMovable(nbtItem.getItem());
    }

    public List<TradeData> getModifiedTrades()
    {
        List<TradeData> trades = new ArrayList<>();

        for(int i = manager.tradeSlotStart; i < tradeSlotEnd; i++)
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

    // --- Utility Methods

    public List<ItemStack> getFreeTradeItems()
    {
        List<ItemStack> stacks = new ArrayList<>();

        for (int i = manager.tradeSlotStart; i < tradeSlotEnd; i++)
        {
            ItemStack item = gui.getItem(i);

            if(Util.isNotNullOrAir(item) && GuiUtil.isFree(item))
            {
                stacks.add(item);
            }
        }

        return stacks;
    }

    public List<ItemStack> getExtractedTradeItems() // Gets only freshly extracted trades, not ones that were inserted later
    {
        List<ItemStack> stacks = new ArrayList<>();

        for (int i = manager.tradeSlotStart; i < tradeSlotEnd; i++)
        {
            ItemStack item = gui.getItem(i);

            if(Util.isNotNullOrAir(item) && GuiUtil.isExtracted(item) && !GuiUtil.isFree(item))
            {
                stacks.add(item);
            }
        }

        return stacks;
    }

    private CostData calculateCureCost()
    {
        return manager.cureCosts.get(villagerData.getCures() + 1);
    }

    private CostData calculateCommitCost()
    {
        int free = getFreeTradeItems().size();
        return CostData.sum(manager.purgeCost.getMultiplied(tradesPurged), manager.extractCost.getMultiplied(tradesExtracted), manager.addCost.getMultiplied(free));
    }

    private CostData calculateScrollCost()
    {
        return manager.scrollCost;
    }

    private CostData calculateSlotExtensionCost()
    {
        return manager.slotExtensionCosts.get(villagerData.getTradeSlots() - VillagerData.minTradeSlots);
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

    private List<String> costToString(CostData cost)
    {
        List<String> strings = new ArrayList<>();

        strings.add("§r§fCosts:");
        if(cost.getMoney() > 0)
        {
            strings.add(String.format("§r§f   -§6%d §eMoney", cost.getMoney()));
        }

        if(cost.getCrystals() > 0)
        {
            strings.add(String.format("§r§f   -§6%d §bCrystals", cost.getCrystals()));
        }

        for (Map.Entry<Material, Integer> entry : cost.getResources().entrySet())
        {
            strings.add(String.format("§r§f   -§6%d §a%s", entry.getValue(), Strings.capitalize(entry.getKey().toString(), "_")));
        }

        return strings;
    }

    private boolean playerCanPay(CostData cost)
    {
        for (Map.Entry<Material, Integer> entry : cost.getResources().entrySet())
        {
            if(!player.getInventory().containsAtLeast(new ItemStack(entry.getKey()), entry.getValue()))
            {
                return false;
            }
        }

        // Check for currencies here!

        return true;
    }

    private void payCosts(CostData cost)
    {
        for (Map.Entry<Material, Integer> entry : cost.getResources().entrySet())
        {
            ItemStack item = new ItemStack(entry.getKey());
            item.setAmount(entry.getValue());
            player.getInventory().removeItem(item);
        }

        // Pay currencies here!
    }
}
