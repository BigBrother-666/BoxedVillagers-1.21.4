package io.gitlab.arkdirfe.boxedvillagers.ui;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.data.TradeData;
import io.gitlab.arkdirfe.boxedvillagers.data.WitchdoctorGuiData;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import io.gitlab.arkdirfe.boxedvillagers.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;

public class WitchdoctorGuiManager implements Listener
{
    private final BoxedVillagers plugin;
    private Map<UUID, WitchdoctorGuiData> guiMap;
    private Map<Material, Integer> cureTier1Map;
    private Map<Material, Integer> cureTier2Map;
    private Map<Material, Integer> cureTier3Map;
    private Map<Material, Integer> purgeMap;

    private final int scrollSlot = getSlot(1, 4);
    private final int helpSlot = getSlot(0, 4);
    private final int cureSlot = getSlot(1, 1);
    private final int commitSlot = getSlot(1, 7);
    private final List<Integer> tradeSlots = Arrays.asList(getSlot(3, 2), getSlot(3, 3), getSlot(3, 4), getSlot(3, 5), getSlot(3, 6),
            getSlot(4, 2), getSlot(4, 3), getSlot(4, 4), getSlot(4, 5), getSlot(4, 6));

    public WitchdoctorGuiManager(BoxedVillagers plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        initializeMaps();
    }

    private void initializeMaps()
    {
        guiMap = new HashMap<>();
        cureTier1Map = new HashMap<>();
        cureTier2Map = new HashMap<>();
        cureTier3Map = new HashMap<>();
        purgeMap = new HashMap<>();

        initCostMap(Strings.CONFIG_COST_CURE1, cureTier1Map);
        initCostMap(Strings.CONFIG_COST_CURE2, cureTier2Map);
        initCostMap(Strings.CONFIG_COST_CURE3, cureTier3Map);
        initCostMap(Strings.CONFIG_COST_PURGE, purgeMap);

        plugin.getLogger().info("Registered costs for operations!");
    }

    private void initCostMap(String configSection, Map<Material, Integer> costMap)
    {
        if(!costMap.isEmpty())
        {
            costMap.clear();
        }

        for(String key : plugin.getConfig().getConfigurationSection(configSection).getKeys(false))
        {
            Material mat = Material.matchMaterial(key);
            if(mat != null)
            {
                costMap.put(mat, plugin.getConfig().getInt(configSection + "." + mat.toString()));
            }
        }
    }

    public void openGui(final HumanEntity player)
    {
        Inventory gui = Bukkit.createInventory(null, 54, Strings.UI_WD_TITLE);

        WitchdoctorGuiData data = new WitchdoctorGuiData(gui, player);
        initGui(data);

        guiMap.put(player.getUniqueId(), data);
    }

    private void initGui(WitchdoctorGuiData data)
    {
        ItemStack[] items = new ItemStack[54];

        for (int i = 0; i < 54; i++)
        {
            items[i] = getFillerItem(Material.LIME_STAINED_GLASS_PANE);
        }

        items[helpSlot] = getScrollIndicator();
        items[scrollSlot] = null;

        data.gui.setContents(items);
    }

    private void updateGui(WitchdoctorGuiData data)
    {
        if(data.scroll == null)
        {
            initGui(data);
        }
        else
        {
            ItemStack[] items = data.gui.getContents();

            items[helpSlot] = getHelpScroll();

            // Draw trades

            int index = 0;

            for(int i : tradeSlots)
            {
                if(index >= data.villagerData.trades.size())
                {
                    items[i] = null;
                    continue;
                }

                TradeData trade = data.villagerData.trades.get(index++);
                if(trade != null)
                {
                    items[i] = getTradeItem(trade);
                }
                else
                {
                    items[i] = null;
                }
            }

            data.gui.setContents(items);
            updateCureButton(data);
            updateCommitButton(data);
        }
    }

    private void updateCureButton(WitchdoctorGuiData data)
    {
        if(data.villagerData.cures == 7)
        {
            data.gui.setItem(cureSlot, getBlockedCureItem());
        }
        else
        {
            data.gui.setItem(cureSlot, getCureItem(data.villagerData.cures));
        }
    }

    private void updateCommitButton(WitchdoctorGuiData data)
    {
        data.gui.setItem(commitSlot, getCommitItem(data));
    }

    private int getSlot(int row, int col)
    {
        return 9 * row + col;
    }

    private ItemStack setUninteractable(ItemStack item)
    {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean(Strings.TAG_UNINTERACTABLE, true);
        return nbtItem.getItem();
    }

    private boolean isUninteractable(ItemStack item)
    {
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasKey(Strings.TAG_UNINTERACTABLE);
    }

    private ItemStack setMovable(ItemStack item)
    {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean(Strings.TAG_MOVABLE, true);
        return nbtItem.getItem();
    }

    private boolean isMovable(ItemStack item)
    {
        if(!Util.isNotNullOrAir(item))
        {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasKey(Strings.TAG_MOVABLE);
    }

    private ItemStack getFillerItem(Material material)
    {
        ItemStack item = new ItemStack(material);

        Util.setItemTitleLoreAndFlags(item, " ", null, null);

        return setUninteractable(item);
    }

    private ItemStack getScrollIndicator()
    {
        ItemStack item = new ItemStack(Material.PAPER);

        Util.setItemTitleLoreAndFlags(item, "§2Villager Scroll",
                Arrays.asList("§r§fPlace your bound scroll below to begin the process."),
                Arrays.asList(ItemFlag.HIDE_ENCHANTS));

        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        return setUninteractable(item);
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

        return setUninteractable(item);
    }

    private ItemStack getCureItem(int cures)
    {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);

        List<String> lore = new ArrayList<>();
        lore.add("§r§fReduces all prices but never below 1.");
        lore.add("§r§4Applies instantly, irreversible.");

        Map<Material, Integer> cost = calculateCureCost(cures);

        if(cost.size() > 0)
        {
            lore.add("§r§fCosts:");
            for(Map.Entry<Material, Integer> entry : cost.entrySet())
            {
                lore.add(String.format("§r§f   -§6%d §a%s", entry.getValue(), Strings.capitalize(entry.getKey().toString(), "_")));
            }
        }

        Util.setItemTitleLoreAndFlags(item, "§2Cure Villager",
                lore,
                null);

        return setUninteractable(item);
    }

    private Map<Material, Integer> calculateCureCost(int cures)
    {
        Map<Material, Integer> costs = new HashMap<>();

        int multiplier = cures + 1;

        for (Map.Entry<Material, Integer> entry : cureTier1Map.entrySet())
        {
            Material key = entry.getKey();
            costs.put(key, costs.getOrDefault(key, 0) + entry.getValue() * multiplier);
        }
        if (cures >= 3)
        {
            for (Map.Entry<Material, Integer> entry : cureTier2Map.entrySet())
            {
                Material key = entry.getKey();
                costs.put(key, costs.getOrDefault(key, 0) + entry.getValue() * (multiplier - 3));
            }
        }
        if (cures >= 5)
        {
            for (Map.Entry<Material, Integer> entry : cureTier3Map.entrySet())
            {
                Material key = entry.getKey();
                costs.put(key, costs.getOrDefault(key, 0) + entry.getValue() * (multiplier - 5));
            }
        }


        return costs;
    }

    private ItemStack getBlockedCureItem()
    {
        ItemStack item = new ItemStack(Material.APPLE);

        Util.setItemTitleLoreAndFlags(item, "§2Cure Villager",
                Arrays.asList("§r§fVillager is at max cures!"),
                null);

        return setUninteractable(item);
    }

    private ItemStack getCommitItem(WitchdoctorGuiData data)
    {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);

        List<String> lore = new ArrayList<>();

        if(!data.tradesMoved && data.tradesPurged == 0)
        {
            lore.add("§r§fNo changes to commit!");
        }
        else
        {
            lore.add("§r§fUncommitted changes!");
            item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        }

        if(data.tradesMoved)
        {
            lore.add("§r§fTrades were moved.");
        }
        if(data.tradesPurged > 0)
        {
            lore.add(String.format("§r§6%d§f trades were purged.", data.tradesPurged));
        }

        if(data.tradesPurged > 0) // Same condition for now, might chance if I add trade extracting
        {
            lore.add("§r§fTotal Costs:");

            for (Map.Entry<Material, Integer> entry : purgeMap.entrySet())
            {
                lore.add(String.format("§r§f   -§6%d §a%s", entry.getValue() * data.tradesPurged, Strings.capitalize(entry.getKey().toString(), "_")));
            }
        }

        Util.setItemTitleLoreAndFlags(item, "§2Commit Changes",
                lore,
                Arrays.asList(ItemFlag.HIDE_ENCHANTS));

        return setUninteractable(item);
    }

    private ItemStack getTradeItem(TradeData trade)
    {
        ItemStack item = new ItemStack(Material.PAPER);

        Util.setItemTitleLoreAndFlags(item, "§aStored Trade",
                Arrays.asList(tradeToString(trade.recipe, trade.baseAmount),
                        "§r§fPrice reduced by §6" + trade.reduction + "§f for each cure.",
                        "§r§fShift Left Click to purge this trade."),
                null);

        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.addCompound(Strings.TAG_SERIALIZED_TRADE_DATA);
        trade.serializeToNBT(compound);

        return setMovable(nbtItem.getItem());
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

    private List<TradeData> getModifiedTrades(WitchdoctorGuiData data)
    {
        List<TradeData> trades = new ArrayList<>();

        for(int i : tradeSlots)
        {
            ItemStack item = data.gui.getItem(i);

            if(Util.isNotNullOrAir(item))
            {
                NBTItem nbtItem = new NBTItem(item);
                TradeData tradeData = new TradeData(nbtItem.getCompound(Strings.TAG_SERIALIZED_TRADE_DATA), data.villagerData.cures);
                trades.add(tradeData);
            }
        }

        return trades;
    }

    private boolean playerCanPay(HumanEntity player, Map<Material, Integer> costs)
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

    private void payCosts(HumanEntity player, Map<Material, Integer> costs)
    {
        for (Map.Entry<Material, Integer> entry : costs.entrySet())
        {
            ItemStack item = new ItemStack(entry.getKey());
            item.setAmount(entry.getValue());
            player.getInventory().removeItem(item);
        }
    }

    // Cleanup methods that are called on disable or when ui is closed, makes sure that the player gets their stuff back

    public void cleanupOpenGuis()
    {
        for(WitchdoctorGuiData data : guiMap.values())
        {
            HumanEntity player = data.player;
            returnScrollAndRemoveFromMap(data, player);
        }
    }

    private void returnScrollAndRemoveFromMap(WitchdoctorGuiData data, HumanEntity player)
    {
        ItemStack scroll = data.scroll;

        if(scroll != null)
        {
            player.getInventory().addItem(scroll);
        }

        guiMap.remove(player.getUniqueId());
    }

    // Event Handlers. At this point I know only players can open this UI so I can skip some checks.

    @EventHandler
    public void onCloseInventory(final InventoryCloseEvent event)
    {
        System.out.println(event.getView().getTitle());
        if(!event.getView().getTitle().equals(Strings.UI_WD_TITLE))
        {
            return;
        }

        if(guiMap.size() == 0)
        {
            return;
        }

        HumanEntity player = event.getInventory().getViewers().get(0);
        WitchdoctorGuiData data = guiMap.get(player.getUniqueId());

        if(data == null)
        {
            return;
        }

        returnScrollAndRemoveFromMap(data, player);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        UUID uuid = event.getPlayer().getUniqueId();
        if(guiMap.containsKey(uuid))
        {
            returnScrollAndRemoveFromMap(guiMap.get(uuid), event.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        InventoryView view = event.getView();
        if(!view.getTitle().equalsIgnoreCase(Strings.UI_WD_TITLE))
        {
            return;
        }

        HumanEntity player =  event.getInventory().getViewers().get(0);
        WitchdoctorGuiData data = guiMap.get(player.getUniqueId());

        if(data == null)
        {
            return;
        }

        if(event.isShiftClick())
        {
            event.setCancelled(true);
        }

        ItemStack item = event.getCurrentItem();

        if(Util.isNotNullOrAir(item))
        {
            if(isUninteractable(item))
            {
                event.setCancelled(true);
            }
        }

        if(event.getRawSlot() == scrollSlot)
        {
            ItemStack slot = data.gui.getItem(scrollSlot);
            ItemStack cursor = view.getCursor();
            boolean slotEmpty = slot == null;
            boolean cursorEmpty = cursor == null || cursor.getType() == Material.AIR;
            boolean slotScroll = Util.validateBoundItem(slot) != null;
            boolean cursorScroll = Util.validateBoundItem(cursor) != null;

            event.setCancelled(true);

            if((slotEmpty && cursorScroll))
            {
                data.gui.setItem(scrollSlot, cursor);
                view.setCursor(new ItemStack(Material.AIR));
            }
            else if((slotScroll && cursorScroll))
            {
                data.gui.setItem(scrollSlot, cursor);
                view.setCursor(slot);
            }
            else if((slotScroll && cursorEmpty))
            {
                data.gui.setItem(scrollSlot, null);
                view.setCursor(slot);
            }

            data.resetTracking();

            data.setScroll(data.gui.getItem(scrollSlot));
            updateGui(data);
            return;
        }

        if(event.getRawSlot() == cureSlot && data.scroll != null)
        {
            if(data.villagerData.cures != 7)
            {
                Map<Material, Integer> costs = calculateCureCost(data.villagerData.cures);
                if(playerCanPay(data.player, costs))
                {
                    payCosts(data.player, costs);

                    data.villagerData.cure(new NBTItem(data.scroll), 1);
                    Util.updateBoundScrollTooltip(data.scroll, data.villagerData);
                    data.gui.setItem(scrollSlot, data.villagerData.writeToItem(new NBTItem(data.scroll)));
                    ((Player)player).playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.5f, 1);
                    updateCureButton(data);
                }
            }

            return;
        }

        if(event.getRawSlot() == commitSlot && (data.tradesMoved || data.tradesPurged > 0))
        {
            Map<Material, Integer> costs = new HashMap<>();

            for (Map.Entry<Material, Integer> entry : purgeMap.entrySet())
            {
                costs.put(entry.getKey(), entry.getValue() * data.tradesPurged);
            }

            if(playerCanPay(data.player, costs))
            {
                payCosts(data.player, costs);
                data.villagerData.trades = getModifiedTrades(data);
                Util.updateBoundScrollTooltip(data.scroll, data.villagerData);
                data.gui.setItem(scrollSlot, data.villagerData.writeToItem(new NBTItem(data.scroll)));
                data.resetTracking();
                updateCommitButton(data);
            }

            return;
        }

        // Handling for movable items (trade recipes), can be moved around inside the UI but not taken out

        int slotIndex = event.getRawSlot();
        ItemStack slot = event.getCurrentItem();
        ItemStack cursor = view.getCursor();
        boolean slotEmpty = slot == null;
        boolean cursorEmpty = cursor == null || cursor.getType() == Material.AIR;
        boolean slotMovable = isMovable(slot);
        boolean cursorMovable = isMovable(cursor);

        if(tradeSlots.contains(slotIndex))
        {
            event.setCancelled(true);
            if((slotEmpty && cursorMovable))
            {
                data.gui.setItem(slotIndex, cursor);
                view.setCursor(new ItemStack(Material.AIR));
                data.tradesMoved = true;
            }
            else if((slotMovable && cursorMovable))
            {
                data.gui.setItem(slotIndex, cursor);
                view.setCursor(slot);
                data.tradesMoved = true;
            }
            else if((slotMovable && cursorEmpty))
            {
                data.gui.setItem(slotIndex, null);
                view.setCursor(slot);
            }

            updateCommitButton(data);
        }
        else if(cursorMovable)
        {
            event.setCancelled(true);
        }

        if(slotMovable && cursorEmpty && event.isShiftClick()) // Purge trade
        {
            event.setCancelled(true);

            data.gui.setItem(slotIndex, null);
            view.setCursor(new ItemStack(Material.AIR));

            // Sound maybe
            data.tradesPurged++;
            updateCommitButton(data);
        }
    }

    @EventHandler
    public void onInventoryDragged(InventoryDragEvent event)
    {
        InventoryView view = event.getView();
        if(!view.getTitle().equalsIgnoreCase(Strings.UI_WD_TITLE))
        {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDropped(PlayerDropItemEvent event)
    {
        if(isMovable(event.getItemDrop().getItemStack()))
        {
            event.getItemDrop().remove();
        }
    }
}
