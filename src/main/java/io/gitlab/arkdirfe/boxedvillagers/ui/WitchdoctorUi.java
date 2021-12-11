package io.gitlab.arkdirfe.boxedvillagers.ui;

import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.data.CostData;
import io.gitlab.arkdirfe.boxedvillagers.data.TradeData;
import io.gitlab.arkdirfe.boxedvillagers.data.VillagerData;
import io.gitlab.arkdirfe.boxedvillagers.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class WitchdoctorUi implements Listener
{
    private static class WitchdoctorUiInstance
    {
        protected final Inventory gui;
        protected final boolean admin;
        protected boolean tradesMoved;
        protected int tradesPurged = 0;
        protected int tradesExtracted = 0;
        protected VillagerData villagerData = null;
        protected HumanEntity player;
        protected int tradeSlotEnd = tradeSlotStart;
        
        protected boolean buyPerms;
        protected boolean curePerms;
        protected boolean extendPerms;
        protected boolean purgePerms;
        protected boolean extractPerms;
        protected boolean shouldSeeTrades;
        
        public WitchdoctorUiInstance(@NotNull Inventory inventory, @NotNull final HumanEntity player, final boolean admin)
        {
            this.admin = admin;
            this.gui = inventory;
            this.player = player;
            
            buyPerms = player.hasPermission(Strings.PERM_WITCHDOCTOR_BUY) || admin;
            curePerms = player.hasPermission(Strings.PERM_WITCHDOCTOR_CURE) || admin;
            extendPerms = player.hasPermission(Strings.PERM_WITCHDOCTOR_EXTEND) || admin;
            purgePerms = player.hasPermission(Strings.PERM_WITCHDOCTOR_PURGE) || admin;
            extractPerms = player.hasPermission(Strings.PERM_WITCHDOCTOR_EXTRACT) || admin;
            shouldSeeTrades = purgePerms || extractPerms;
        }
        
        @Nullable
        public ItemStack getScroll()
        {
            ItemStack scroll = gui.getItem(scrollSlot);
            
            if(!ItemUtil.isNullOrAir(scroll))
            {
                villagerData = new VillagerData(new NBTItem(scroll));
                tradeSlotEnd = tradeSlotStart + villagerData.getTradeSlots();
            }
            else
            {
                villagerData = null;
            }
            
            return scroll;
        }
        
        public void resetTracking()
        {
            tradesMoved = false;
            tradesPurged = 0;
            tradesExtracted = 0;
        }
    }
    
    public WitchdoctorUi(final BoxedVillagers plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    private static final int scrollSlot = GuiUtil.getGuiSlot(1, 4);
    private static final int helpSlot = GuiUtil.getGuiSlot(0, 4);
    private static final int cureSlot = GuiUtil.getGuiSlot(1, 2);
    private static final int commitSlot = GuiUtil.getGuiSlot(1, 6);
    private static final int extendTradeSlotsSlot = GuiUtil.getGuiSlot(1, 0);
    private static final int buyScrollSlot = GuiUtil.getGuiSlot(1, 8);
    private static final int tradeSlotStart = GuiUtil.getGuiSlot(3, 0);
    
    private static final HashMap<UUID, WitchdoctorUiInstance> instances = new HashMap<>();
    
    public static void openForPlayer(@NotNull final HumanEntity player, final boolean admin)
    {
        Inventory gui = Bukkit.createInventory(null, 54, (admin ? StringFormatter.formatLine(Strings.get(StringRef.UI_WD_TITLE_ADMIN)) : StringFormatter.formatLine(Strings.get(StringRef.UI_WD_TITLE))));
        WitchdoctorUiInstance instance = new WitchdoctorUiInstance(gui, player, admin);
        instances.put(player.getUniqueId(), instance);
        player.openInventory(gui);
        updateInstance(instance);
    }
    
    public static void cleanupOpenGuis()
    {
        for(WitchdoctorUiInstance instance : instances.values())
        {
            HumanEntity player = instance.player;
            returnItemsAndRemoveFromMap(instance, player);
        }
    }
    
    private static void returnItemsAndRemoveFromMap(@NotNull final WitchdoctorUiInstance instance, @NotNull final HumanEntity player)
    {
        if(instance.getScroll() != null)
        {
            player.getInventory().addItem(instance.getScroll());
        }
        
        for(ItemStack item : getFreeTradeItems(instance))
        {
            player.getInventory().addItem(item);
        }
        
        ItemStack cursorItem = player.getItemOnCursor();
        if(!ItemUtil.isNullOrAir(cursorItem) && !GuiUtil.isExtracted(cursorItem))
        {
            NBTItem nbt = new NBTItem(cursorItem);
            if(nbt.hasKey(Strings.TAG_SERIALIZED_TRADE_DATA))
            {
                player.setItemOnCursor(null);
            }
        }
        
        instances.remove(player.getUniqueId());
    }
    
    private static void updateNextTick(@NotNull WitchdoctorUiInstance instance, @NotNull HumanEntity player)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                // Safety in case the inventory closed in the tick that passed
                if(instances.containsKey(player.getUniqueId()))
                {
                    updateInstance(instance);
                }
            }
        }.runTaskLater(BoxedVillagers.getPlugin(), 1);
    }
    
    private static void updateInstance(@NotNull WitchdoctorUiInstance instance)
    {
        ItemStack[] items = new ItemStack[54];
        
        for(int i = 0; i < 54; i++)
        {
            items[i] = ItemUtil.getUIFillerItem(Material.LIME_STAINED_GLASS_PANE);
        }
        
        if(instance.buyPerms)
        {
            items[buyScrollSlot] = ItemUtil.getBuyScrollItem(BoxedVillagers.getScrollCost());
        }
        
        ItemStack scroll = instance.getScroll(); // This also sets villager data and trade slot end
        items[scrollSlot] = scroll;
        if(!ItemUtil.isNullOrAir(scroll))
        {
            if(instance.curePerms)
            {
                items[cureSlot] = ItemUtil.getCureItem(calculateCureCost(instance.villagerData), instance.villagerData.getCures());
            }
            if(instance.extendPerms)
            {
                items[extendTradeSlotsSlot] = ItemUtil.getSlotExtensionItem(instance.villagerData, calculateSlotExtensionCost(instance.villagerData));
            }
            if(instance.purgePerms || instance.extractPerms)
            {
                items[commitSlot] = ItemUtil.getCommitItem(getFreeTradeItems(instance).size(), instance.tradesMoved, instance.tradesPurged, instance.tradesExtracted, calculateCommitCost(instance));
            }
            if(instance.shouldSeeTrades)
            {
                int index = 0;
                
                for(int i = tradeSlotStart; i < instance.tradeSlotEnd; i++)
                {
                    if(index >= instance.villagerData.getTrades().size())
                    {
                        items[i] = null;
                        continue;
                    }
                    
                    TradeData trade = instance.villagerData.getTrades().get(index++);
                    if(trade != null)
                    {
                        items[i] = ItemUtil.getTradeItem(trade, instance.purgePerms, instance.extractPerms);
                    }
                    else
                    {
                        items[i] = null;
                    }
                }
            }
        }
        
        items[helpSlot] = ItemUtil.getHelpItem(!ItemUtil.isNullOrAir(scroll), instance.buyPerms, instance.curePerms, instance.extendPerms, instance.purgePerms, instance.extractPerms);
        
        instance.gui.setContents(items);
    }
    
    private static void updateCureButton(@NotNull WitchdoctorUiInstance instance)
    {
        if(ItemUtil.isNullOrAir(instance.getScroll()))
        {
            return;
        }
        
        instance.gui.setItem(cureSlot, ItemUtil.getCureItem(calculateCureCost(instance.villagerData), instance.villagerData.getCures()));
    }
    
    private static void updateExtendButton(@NotNull WitchdoctorUiInstance instance)
    {
        if(ItemUtil.isNullOrAir(instance.getScroll()))
        {
            return;
        }
        
        instance.gui.setItem(extendTradeSlotsSlot, ItemUtil.getSlotExtensionItem(instance.villagerData, calculateSlotExtensionCost(instance.villagerData)));
    }
    
    private static void updateCommitButton(@NotNull WitchdoctorUiInstance instance)
    {
        if(ItemUtil.isNullOrAir(instance.getScroll()))
        {
            return;
        }
        
        instance.gui.setItem(commitSlot, ItemUtil.getCommitItem(getFreeTradeItems(instance).size(), instance.tradesMoved, instance.tradesPurged, instance.tradesExtracted, calculateCommitCost(instance)));
    }
    
    private static void updateCommitButtonNextTick(@NotNull WitchdoctorUiInstance instance, @NotNull HumanEntity player)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                // Safety in case the inventory closed in the tick that passed
                if(instances.containsKey(player.getUniqueId()))
                {
                    updateCommitButton(instance);
                }
            }
        }.runTaskLater(BoxedVillagers.getPlugin(), 1);
    }
    
    @Nullable
    private static CostData calculateCureCost(@NotNull VillagerData villagerData)
    {
        if(villagerData.getCures() == 7)
        {
            return null;
        }
        return BoxedVillagers.getCureCosts().get(villagerData.getCures());
    }
    
    @Nullable
    private static CostData calculateSlotExtensionCost(@NotNull VillagerData villagerData)
    {
        if(villagerData.getTradeSlots() == BoxedVillagers.getMaxTradeSlots())
        {
            return null;
        }
        return BoxedVillagers.getSlotExtensionCosts().get(villagerData.getTradeSlots());
    }
    
    @NotNull
    private static CostData calculateCommitCost(@NotNull WitchdoctorUiInstance instance)
    {
        int free = getFreeTradeItems(instance).size();
        return CostData.sum(BoxedVillagers.getPurgeCost().getMultiplied(instance.tradesPurged), BoxedVillagers.getExtractCost().getMultiplied(instance.tradesExtracted), BoxedVillagers.getAddCost().getMultiplied(free));
    }
    
    @NotNull
    private static List<ItemStack> getFreeTradeItems(@NotNull WitchdoctorUiInstance instance)
    {
        List<ItemStack> stacks = new ArrayList<>();
        
        for(int i = tradeSlotStart; i < instance.tradeSlotEnd; i++)
        {
            ItemStack item = instance.gui.getItem(i);
            
            if(!ItemUtil.isNullOrAir(item) && GuiUtil.isFree(item))
            {
                stacks.add(item);
            }
        }
        
        return stacks;
    }
    
    @NotNull
    private static List<ItemStack> getExtractedTradeItems(@NotNull WitchdoctorUiInstance instance)
    {
        List<ItemStack> stacks = new ArrayList<>();
        
        for(int i = tradeSlotStart; i < instance.tradeSlotEnd; i++)
        {
            ItemStack item = instance.gui.getItem(i);
            
            if(!ItemUtil.isNullOrAir(item) && GuiUtil.isExtracted(item) && !GuiUtil.isFree(item))
            {
                stacks.add(item);
            }
        }
        
        return stacks;
    }
    
    @NotNull
    private static List<TradeData> getModifiedTrades(@NotNull WitchdoctorUiInstance instance)
    {
        List<TradeData> trades = new ArrayList<>();
        
        for(int i = tradeSlotStart; i < instance.tradeSlotEnd; i++)
        {
            ItemStack item = instance.gui.getItem(i);
            
            if(!ItemUtil.isNullOrAir(item))
            {
                NBTItem nbtItem = new NBTItem(item);
                TradeData tradeData = new TradeData(nbtItem.getCompound(Strings.TAG_SERIALIZED_TRADE_DATA), instance.villagerData.getCures());
                trades.add(tradeData);
            }
        }
        
        return trades;
    }
    
    private static void buyScroll(@NotNull HumanEntity player)
    {
        CostData scrollCost = BoxedVillagers.getScrollCost();
        
        if(playerCanPay(scrollCost, player) && player.getInventory().firstEmpty() != -1)
        {
            payCosts(scrollCost, player);
            player.getInventory().addItem(ItemUtil.getUnboundScroll(false));
            ((Player) player).playSound(player.getLocation(), Sound.ENTITY_VILLAGER_WORK_LIBRARIAN, 1f, 1);
        }
        else
        {
            ((Player) player).playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1);
        }
    }
    
    public void cureVillager(@NotNull WitchdoctorUiInstance instance, @NotNull HumanEntity player)
    {
        if(ItemUtil.isNullOrAir(instance.getScroll()))
        {
            return;
        }
        
        if(instance.villagerData.getCures() >= BoxedVillagers.getMaxCures())
        {
            return;
        }
        
        CostData cureCost = calculateCureCost(instance.villagerData);
        
        if(instance.admin || playerCanPay(cureCost, player))
        {
            if(!instance.admin)
            {
                payCosts(cureCost, player);
            }
            
            instance.villagerData.cure(1);
            instance.gui.setItem(scrollSlot, instance.villagerData.getItem());
            ((Player) player).playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.5f, 1);
            
            updateCureButton(instance);
        }
        else
        {
            ((Player) player).playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1);
        }
    }
    
    private static void extendSlots(@NotNull WitchdoctorUiInstance instance, @NotNull HumanEntity player)
    {
        if(ItemUtil.isNullOrAir(instance.getScroll()))
        {
            return;
        }
        
        if(instance.villagerData.getTradeSlots() == BoxedVillagers.getMaxTradeSlots())
        {
            return;
        }
        
        CostData slotCost = calculateSlotExtensionCost(instance.villagerData);
        
        if(instance.admin || playerCanPay(slotCost, player))
        {
            if(!instance.admin)
            {
                payCosts(slotCost, player);
            }
            
            instance.villagerData.addTradeSlots(1);
            instance.gui.setItem(scrollSlot, instance.villagerData.getItem());
            ((Player) player).playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1);
            
            updateExtendButton(instance);
            instance.gui.setItem(instance.tradeSlotEnd - 1, null);
        }
        else
        {
            ((Player) player).playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1);
        }
    }
    
    private static void commitChanges(@NotNull WitchdoctorUiInstance instance, @NotNull HumanEntity player)
    {
        if(ItemUtil.isNullOrAir(instance.getScroll()))
        {
            return;
        }
        
        CostData commitCost = calculateCommitCost(instance);
        
        if(instance.admin || playerCanPay(commitCost, player))
        {
            if(!instance.admin)
            {
                payCosts(commitCost, player);
            }
            
            List<ItemStack> extracted = getExtractedTradeItems(instance);
            
            for(ItemStack item : extracted)
            {
                instance.gui.remove(item);
                player.getInventory().addItem(ItemUtil.convertExtractedToFree(item));
            }
            
            instance.villagerData.setTrades(getModifiedTrades(instance));
            instance.gui.setItem(scrollSlot, instance.villagerData.getItem());
            ((Player) player).playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            
            instance.resetTracking();
            updateInstance(instance);
        }
        else
        {
            ((Player) player).playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        }
    }
    
    private static void purgeTrade(@NotNull WitchdoctorUiInstance instance, @NotNull HumanEntity player, final int slot)
    {
        if(ItemUtil.isNullOrAir(instance.getScroll()))
        {
            return;
        }
        
        instance.gui.setItem(slot, null);
        
        ((Player) player).playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        instance.tradesPurged++;
        
        updateCommitButton(instance);
    }
    
    private static void extractTrade(@NotNull WitchdoctorUiInstance instance, @NotNull HumanEntity player, final int slot) // Already null checked by manager
    {
        if(ItemUtil.isNullOrAir(instance.getScroll()))
        {
            return;
        }
        
        ItemStack item = instance.gui.getItem(slot);
        
        if(ItemUtil.isNullOrAir(item) || GuiUtil.isFree(item) || GuiUtil.isExtracted(item))
        {
            return;
        }
        
        ((Player) player).playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 2);
        
        instance.gui.setItem(slot, ItemUtil.convertTradeToExtracted(item));
        instance.tradesExtracted++;
        
        updateCommitButton(instance);
    }
    
    private static boolean playerCanPay(@Nullable final CostData cost, @NotNull final HumanEntity player)
    {
        if(cost == null)
        {
            return true;
        }
        
        for(Map.Entry<Material, Integer> entry : cost.getResources().entrySet())
        {
            if(!player.getInventory().containsAtLeast(new ItemStack(entry.getKey()), entry.getValue()))
            {
                return false;
            }
        }
        
        if(BoxedVillagers.getEconomy() != null)
        {
            return BoxedVillagers.getEconomy().has(Bukkit.getServer().getOfflinePlayer(player.getUniqueId()), cost.getMoney());
        }
        
        return true;
    }
    
    private static void payCosts(@Nullable final CostData cost, @NotNull final HumanEntity player)
    {
        if(cost == null)
        {
            return;
        }
        
        for(Map.Entry<Material, Integer> entry : cost.getResources().entrySet())
        {
            ItemStack item = new ItemStack(entry.getKey());
            item.setAmount(entry.getValue());
            player.getInventory().removeItem(item);
        }
        
        if(BoxedVillagers.getEconomy() != null)
        {
            BoxedVillagers.getEconomy().withdrawPlayer(Bukkit.getServer().getOfflinePlayer(player.getUniqueId()), cost.getMoney());
        }
    }
    
    //////////////////
    // Event Handlers
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        if(!instances.containsKey(event.getWhoClicked().getUniqueId()))
        {
            return;
        }
        
        if(event.getAction() == InventoryAction.DROP_ALL_SLOT || event.getAction() == InventoryAction.DROP_ONE_SLOT || event.getAction() == InventoryAction.DROP_ALL_CURSOR || event.getAction() == InventoryAction.DROP_ONE_CURSOR)
        {
            event.setCancelled(true);
            return;
        }
        
        WitchdoctorUiInstance instance = instances.get(event.getWhoClicked().getUniqueId());
        HumanEntity player = event.getWhoClicked();
        
        if(event.getRawSlot() == scrollSlot)
        {
            if(!ItemUtil.isNullOrAir(event.getCursor()) && ItemUtil.validateBoundItem(event.getCursor()) == null && !event.isShiftClick())
            {
                event.setCancelled(true);
                return;
            }
            
            updateNextTick(instance, player);
            return;
        }
        
        if(event.isShiftClick())
        {
            // Allow shift clicking scrolls into and out of the slot, other shift clicks disabled
            if((event.getView().getTopInventory().firstEmpty() == scrollSlot && ItemUtil.validateBoundItem(event.getCurrentItem()) != null) || event.getRawSlot() == scrollSlot)
            {
                updateNextTick(instance, player);
            }
            else
            {
                event.setCancelled(true);
            }
            
            if(GuiUtil.isMovable(event.getCurrentItem()) && ItemUtil.isNullOrAir(event.getCursor()))
            {
                if(event.isLeftClick() && instance.purgePerms)
                {
                    purgeTrade(instance, player, event.getRawSlot());
                }
                else if(event.isRightClick() && instance.extractPerms)
                {
                    extractTrade(instance, player, event.getRawSlot());
                }
            }
        }
        
        if(GuiUtil.isUninteractable(event.getCurrentItem()))
        {
            event.setCancelled(true);
        }
        
        if(event.getRawSlot() == buyScrollSlot && instance.buyPerms)
        {
            buyScroll(player);
            return;
        }
        
        if(event.getRawSlot() == cureSlot && instance.curePerms)
        {
            cureVillager(instance, player);
            return;
        }
        
        if(event.getRawSlot() == extendTradeSlotsSlot && instance.extendPerms)
        {
            extendSlots(instance, player);
            return;
        }
        
        if(event.getRawSlot() == commitSlot && (instance.tradesMoved || instance.tradesPurged > 0 || instance.tradesExtracted > 0 || getFreeTradeItems(instance).size() > 0))
        {
            commitChanges(instance, player);
            return;
        }
        
        if(event.getRawSlot() >= tradeSlotStart && event.getRawSlot() < instance.tradeSlotEnd)
        {
            if(!ItemUtil.isNullOrAir(event.getCursor()) && !GuiUtil.isMovable(event.getCursor()))
            {
                event.setCancelled(true);
            }
            else
            {
                instance.tradesMoved = true;
                updateCommitButtonNextTick(instance, player);
            }
        }
        
        if(event.getRawSlot() >= 54 && GuiUtil.isMovable(event.getCursor()) && !GuiUtil.isFree(event.getCursor()))
        {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInventoryDragged(final InventoryDragEvent event)
    {
        if(!instances.containsKey(event.getWhoClicked().getUniqueId()))
        {
            return;
        }
        
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onCloseInventory(final InventoryCloseEvent event)
    {
        if(!instances.containsKey(event.getPlayer().getUniqueId()))
        {
            return;
        }
        
        returnItemsAndRemoveFromMap(instances.get(event.getPlayer().getUniqueId()), event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        if(!instances.containsKey(event.getPlayer().getUniqueId()))
        {
            return;
        }
        
        returnItemsAndRemoveFromMap(instances.get(event.getPlayer().getUniqueId()), event.getPlayer());
    }
}
