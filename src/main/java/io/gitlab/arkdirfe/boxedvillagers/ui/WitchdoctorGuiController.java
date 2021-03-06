package io.gitlab.arkdirfe.boxedvillagers.ui;

import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.data.CostData;
import io.gitlab.arkdirfe.boxedvillagers.data.TradeData;
import io.gitlab.arkdirfe.boxedvillagers.data.VillagerData;
import io.gitlab.arkdirfe.boxedvillagers.util.GuiUtil;
import io.gitlab.arkdirfe.boxedvillagers.util.ItemUtil;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private final BoxedVillagers plugin;

    /**
     * Responsible for updating the witchdoctor GUI and keeping track of its state.
     *
     * @param gui     The linked GUI.
     * @param player  The player who opened the GUI.
     * @param manager Reference to the GUI manager.
     * @param plugin  Reference to the plugin.
     * @param admin   Admin mode. If true costs are ignored.
     */
    public WitchdoctorGuiController(@NotNull final Inventory gui, @NotNull final HumanEntity player, @NotNull final WitchdoctorGuiManager manager, @NotNull final BoxedVillagers plugin, final boolean admin)
    {
        this.player = player;
        this.gui = gui;
        this.manager = manager;
        this.plugin = plugin;
        this.admin = admin;
        tradeSlotEnd = manager.tradeSlotStart;

        advancedPerms = player.hasPermission(Strings.get("PERM_WITCHDOCTOR_ADVANCED"));
        extractPerms = player.hasPermission(Strings.get("PERM_WITCHDOCTOR_EXTRACT"));

        player.openInventory(gui);
        update();
    }

    // --- Getters

    @Nullable
    public ItemStack getScroll()
    {
        return scroll;
    }

    @NotNull
    public HumanEntity getPlayer()
    {
        return player;
    }

    // --- Setters/Accessors

    public void tradeMoved()
    {
        tradesMoved = true;
    }

    /**
     * Sets the scroll and extracts villager data if it is valid.
     *
     * @param scroll The scroll.
     */
    public void setScroll(@Nullable final ItemStack scroll)
    {
        if(!ItemUtil.isNullOrAir(scroll))
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

    /**
     * Puts items into the correct slots.
     */
    public void update()
    {
        if(scroll == null)
        {
            updateNoScroll();
        }
        else
        {
            updateHasScroll();
        }
    }

    /**
     * No scroll, only info slot and buy button.
     */
    private void updateNoScroll()
    {
        ItemStack[] items = new ItemStack[54];

        for(int i = 0; i < 54; i++)
        {
            items[i] = ItemUtil.getUIFillerItem(Material.LIME_STAINED_GLASS_PANE);
        }

        items[manager.buyScrollSlot] = ItemUtil.getBuyScrollItem(calculateScrollCost());
        items[manager.helpSlot] = ItemUtil.getNoScrollHelpItem();
        items[manager.scrollSlot] = null;

        gui.setContents(items);
    }

    /**
     * Draw full GUI
     */
    private void updateHasScroll()
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

    /**
     * Updates only the cure button.
     */
    public void updateCureButton()
    {
        gui.setItem(manager.cureSlot, ItemUtil.getCureItem(calculateCureCost()));
    }

    /**
     * Updates only the commit button.
     */
    public void updateCommitButton()
    {
        if(advancedPerms)
        {
            gui.setItem(manager.commitSlot, ItemUtil.getCommitItem(getFreeTradeItems().size(), tradesMoved, tradesPurged, tradesExtracted, calculateCommitCost()));
        }
    }

    // --- General Methods

    /**
     * Checks if the slot is a valid trade slot.
     *
     * @param slot The slot to check.
     * @return True if slot is valid, false otherwise.
     */
    public boolean isTradeSlot(final int slot)
    {
        return slot >= manager.tradeSlotStart && slot < tradeSlotEnd;
    }

    /**
     * Checks if changes that warrant a commit have been made.
     *
     * @return True if yes, false if no.
     */
    public boolean canCommit()
    {
        int free = getFreeTradeItems().size();
        return (tradesMoved || tradesPurged > 0 || tradesExtracted > 0 || free > 0);
    }

    /**
     * Resets variables that track committable changes.
     */
    private void resetTracking()
    {
        tradesMoved = false;
        tradesPurged = 0;
        tradesExtracted = 0;
    }

    /**
     * Checks whether the player can pay the commit cost, if so changes are written to the scroll item.
     */
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
            gui.setItem(manager.scrollSlot, villagerData.getItem());
            ((Player) player).playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            resetTracking();

            update();
        }
        else
        {
            ((Player) player).playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        }
    }

    /**
     * Checks whether the player can pay the cure cost, if so the villager is cured once.
     */
    public void cureVillager()
    {
        if(villagerData.getCures() == 7)
        {
            return;
        }

        CostData cureCost = calculateCureCost();

        if(playerCanPay(cureCost) || admin)
        {
            if(!admin)
            {
                payCosts(cureCost);
            }

            villagerData.cure(1);
            gui.setItem(manager.scrollSlot, villagerData.getItem());
            ((Player) player).playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.5f, 1);
            updateCureButton();
        }
        else
        {
            ((Player) player).playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1);
        }
    }

    /**
     * Removes an available trade from the villager. Needs to be committed to be permanent.
     *
     * @param slot The slot the trade is in.
     */
    public void purgeTrade(final int slot)
    {
        gui.setItem(slot, null);

        ((Player) player).playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        tradesPurged++;
        updateCommitButton();
    }

    /**
     * Gives the player a scroll if they can afford it.
     */
    public void buyScroll()
    {
        CostData scrollCost = calculateScrollCost();

        if(playerCanPay(scrollCost) && player.getInventory().firstEmpty() != -1)
        {
            payCosts(scrollCost);
            player.getInventory().addItem(ItemUtil.getUnboundScroll(false));
            ((Player) player).playSound(player.getLocation(), Sound.ENTITY_VILLAGER_WORK_LIBRARIAN, 1f, 1);
        }
        else
        {
            ((Player) player).playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1);
        }
    }

    /**
     * Extends the villager's trade slots if possible.
     */
    public void extendSlots()
    {
        if(villagerData.getTradeSlots() == VillagerData.MAX_TRADE_SLOTS)
        {
            return;
        }

        CostData slotCost = calculateSlotExtensionCost();

        if(playerCanPay(slotCost))
        {
            payCosts(slotCost);

            villagerData.addTradeSlots(1);
            gui.setItem(manager.scrollSlot, villagerData.getItem());
            ((Player) player).playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1);
            update();
        }
        else
        {
            ((Player) player).playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1);
        }
    }

    /**
     * Transforms a trade into an extracted trade.
     *
     * @param slot The slot of the trade.
     */
    public void extractTrade(final int slot) // Already null checked by manager
    {
        ItemStack item = gui.getItem(slot);

        if(ItemUtil.isNullOrAir(item) || GuiUtil.isFree(item) || GuiUtil.isExtracted(item))
        {
            return;
        }

        ((Player) player).playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 2);

        gui.setItem(slot, ItemUtil.convertTradeToExtracted(item));
        tradesExtracted++;
        updateCommitButton();
    }

    /**
     * Handles a click on the scroll slot.
     *
     * @param view        The InventoryView that is currently open.
     * @param slotItem    The item in the slot.
     * @param cursorItem  The item on the cursor.
     * @param slotEmpty   Whether the slot is empty.
     * @param cursorEmpty Whether the cursor is empty.
     */
    public void clickScrollSlot(@NotNull final InventoryView view, @Nullable final ItemStack slotItem, @Nullable final ItemStack cursorItem, final boolean slotEmpty, final boolean cursorEmpty)
    {
        boolean slotScroll = ItemUtil.validateBoundItem(slotItem) != null;
        boolean cursorScroll = ItemUtil.validateBoundItem(cursorItem) != null;

        if((slotEmpty && cursorScroll))
        {
            gui.setItem(manager.scrollSlot, cursorItem);
            view.setCursor(new ItemStack(Material.AIR));
        }
        else if((slotScroll && cursorScroll))
        {
            gui.setItem(manager.scrollSlot, cursorItem);
            view.setCursor(slotItem);
        }
        else if((slotScroll && cursorEmpty))
        {
            gui.setItem(manager.scrollSlot, null);
            view.setCursor(slotItem);
        }

        resetTracking();

        setScroll(gui.getItem(manager.scrollSlot));
        update();
    }

    /**
     * Handles a click on a trade slot.
     *
     * @param view          The InventoryView that is currently open.
     * @param slotItem      The item in the slot.
     * @param cursorItem    The item on the cursor.
     * @param event         The event that called this.
     * @param slotIndex     The slot in question.
     * @param slotEmpty     Whether the slot is empty.
     * @param cursorEmpty   Whether the cursor is empty.
     * @param slotMovable   Whether the item in the slot is movable.
     * @param cursorMovable Whether the item on the cursor is movable.
     */
    public void clickTradeSlot(@NotNull final InventoryView view, @Nullable final ItemStack slotItem, @Nullable final ItemStack cursorItem, @NotNull final InventoryClickEvent event, final int slotIndex, final boolean slotEmpty, final boolean cursorEmpty, final boolean slotMovable, final boolean cursorMovable)
    {
        if((slotEmpty && cursorMovable))
        {
            gui.setItem(slotIndex, cursorItem);
            view.setCursor(new ItemStack(Material.AIR));
            tradeMoved();
        }
        else if((slotMovable && cursorMovable))
        {
            gui.setItem(slotIndex, cursorItem);
            view.setCursor(slotItem);
            tradeMoved();
        }
        else if((slotMovable && cursorEmpty) && event.isLeftClick() && !event.isShiftClick())
        {
            gui.setItem(slotIndex, null);
            view.setCursor(slotItem);
            tradeMoved();
        }

        updateCommitButton();
    }

    // --- Utility Methods

    /**
     * Gets trades from the representations in the trade slots.
     *
     * @return A list of TradeData.
     */
    @NotNull
    public List<TradeData> getModifiedTrades()
    {
        List<TradeData> trades = new ArrayList<>();

        for(int i = manager.tradeSlotStart; i < tradeSlotEnd; i++)
        {
            ItemStack item = gui.getItem(i);

            if(!ItemUtil.isNullOrAir(item))
            {
                NBTItem nbtItem = new NBTItem(item);
                TradeData tradeData = new TradeData(nbtItem.getCompound(Strings.get("TAG_SERIALIZED_TRADE_DATA")), villagerData.getCures());
                trades.add(tradeData);
            }
        }

        return trades;
    }

    /**
     * Returns a list of all items in the GUI that have the Free tag (extracted and committed or inserted by the player).
     *
     * @return The list.
     */
    @NotNull
    public List<ItemStack> getFreeTradeItems()
    {
        List<ItemStack> stacks = new ArrayList<>();

        for(int i = manager.tradeSlotStart; i < tradeSlotEnd; i++)
        {
            ItemStack item = gui.getItem(i);

            if(!ItemUtil.isNullOrAir(item) && GuiUtil.isFree(item))
            {
                stacks.add(item);
            }
        }

        return stacks;
    }

    /**
     * Returns a list of all items in the GUI that have the Extracted tag (set to extract but not yet committed).
     *
     * @return The list.
     */
    @NotNull
    private List<ItemStack> getExtractedTradeItems() // Gets only freshly extracted trades, not ones that were inserted later
    {
        List<ItemStack> stacks = new ArrayList<>();

        for(int i = manager.tradeSlotStart; i < tradeSlotEnd; i++)
        {
            ItemStack item = gui.getItem(i);

            if(!ItemUtil.isNullOrAir(item) && GuiUtil.isExtracted(item) && !GuiUtil.isFree(item))
            {
                stacks.add(item);
            }
        }

        return stacks;
    }

    /**
     * Calculates the cost for curing the current villager.
     *
     * @return The cost.
     */
    @Nullable
    private CostData calculateCureCost()
    {
        if(villagerData.getCures() == 7)
        {
            return null;
        }
        return plugin.cureCosts.get(villagerData.getCures());
    }

    /**
     * Calculates the cost for committing the current changes.
     *
     * @return The cost.
     */
    @NotNull
    private CostData calculateCommitCost()
    {
        int free = getFreeTradeItems().size();
        return CostData.sum(plugin.purgeCost.getMultiplied(tradesPurged), plugin.extractCost.getMultiplied(tradesExtracted), plugin.addCost.getMultiplied(free));
    }

    /**
     * Calculates the cost for purchasing a scroll.
     *
     * @return The cost.
     */
    @NotNull
    private CostData calculateScrollCost()
    {
        return plugin.scrollCost;
    }

    /**
     * Calculates the cost for extending the trade slots of the current villager.
     *
     * @return The cost.
     */
    @Nullable
    private CostData calculateSlotExtensionCost()
    {
        if(villagerData.getTradeSlots() == VillagerData.MAX_TRADE_SLOTS)
        {
            return null;
        }
        return plugin.slotExtensionCosts.get(villagerData.getTradeSlots() - VillagerData.MIN_TRADE_SLOTS);
    }

    /**
     * Checks whether the player has enough resources in their inventory and money accounts to pay.
     *
     * @param cost The cost that is checked.
     * @return True if the player can pay, false otherwise.
     */
    private boolean playerCanPay(@Nullable final CostData cost)
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

        // Check for currencies here!

        return true;
    }

    /**
     * Deducts the cost from the player's inventory and money accounts.
     *
     * @param cost The cost.
     */
    private void payCosts(@Nullable final CostData cost)
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

        // Pay currencies here!
    }
}
