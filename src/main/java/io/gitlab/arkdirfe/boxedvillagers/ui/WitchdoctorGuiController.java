package io.gitlab.arkdirfe.boxedvillagers.ui;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.data.CostData;
import io.gitlab.arkdirfe.boxedvillagers.data.TradeData;
import io.gitlab.arkdirfe.boxedvillagers.data.VillagerData;
import io.gitlab.arkdirfe.boxedvillagers.util.GuiUtil;
import io.gitlab.arkdirfe.boxedvillagers.util.ItemUtil;
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
    private final boolean advancedPerms;
    public final boolean extractPerms;

    private final WitchdoctorGuiManager manager;

    public WitchdoctorGuiController(Inventory gui, HumanEntity player,  WitchdoctorGuiManager manager, boolean admin)
    {
        this.player = player;
        this.gui = gui;
        this.manager = manager;
        this.admin = admin;
        tradeSlotEnd = manager.tradeSlotStart;

        advancedPerms = player.hasPermission(Strings.PERM_WITCHDOCTOR_ADVANCED);
        extractPerms = player.hasPermission(Strings.PERM_WITCHDOCTOR_EXTRACT);

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
            items[i] = ItemUtil.getUIFillerItem(Material.LIME_STAINED_GLASS_PANE);
        }

        items[manager.buyScrollSlot] = ItemUtil.getBuyScrollItem(calculateScrollCost());
        items[manager.helpSlot] = ItemUtil.getNoScrollHelpItem();
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

            items[manager.buyScrollSlot] = ItemUtil.getBuyScrollItem(calculateScrollCost());
            items[manager.helpSlot] = ItemUtil.getScrollHelpItem(advancedPerms);

            if(advancedPerms)
            {
                items[manager.extendTradeSlotsSlot] = ItemUtil.getSlotExtensionItem(villagerData, calculateSlotExtensionCost());

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
                        items[i] = ItemUtil.getTradeItem(trade, extractPerms);
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
        gui.setItem(manager.cureSlot, ItemUtil.getCureItem(villagerData.getCures(), villagerData, calculateCureCost()));
    }

    public void updateCommitButton()
    {
        if(advancedPerms)
        {
            gui.setItem(manager.commitSlot, ItemUtil.getCommitItem(getFreeTradeItems().size(), tradesMoved, tradesPurged, tradesExtracted, calculateCommitCost()));
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

                player.getInventory().addItem(ItemUtil.convertExtractedToFree(item));
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
            player.getInventory().addItem(ItemUtil.getUnboundScroll(false));
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

        ((Player)player).playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 2);

        gui.setItem(slot, ItemUtil.convertTradeToExtracted(item));
        tradesExtracted++;
        updateCommitButton();
    }

    // --- Utility Methods

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
