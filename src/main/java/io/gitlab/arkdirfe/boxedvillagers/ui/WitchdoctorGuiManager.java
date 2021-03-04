package io.gitlab.arkdirfe.boxedvillagers.ui;


import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.util.GuiUtil;
import io.gitlab.arkdirfe.boxedvillagers.util.ItemUtil;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WitchdoctorGuiManager implements Listener
{
    private final BoxedVillagers plugin;

    public final int scrollSlot = GuiUtil.getGuiSlot(1, 4);
    public final int helpSlot = GuiUtil.getGuiSlot(0, 4);
    public final int cureSlot = GuiUtil.getGuiSlot(1, 2);
    public final int commitSlot = GuiUtil.getGuiSlot(1, 6);
    public final int extendTradeSlotsSlot = GuiUtil.getGuiSlot(1, 0);
    public final int buyScrollSlot = GuiUtil.getGuiSlot(1, 8);
    public final int tradeSlotStart = GuiUtil.getGuiSlot(3, 0);

    /**
     * Handles creation of witchdoctor GUIs and listens to related events.
     * @param plugin Reference to the plugin.
     */
    public WitchdoctorGuiManager(final BoxedVillagers plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    /**
     * Opens a witchdoctor GUI for a player.
     * @param player Player who ran the command.
     * @param admin Admin mode, if true costs are disregarded.
     */
    public void openGui(@NotNull final HumanEntity player, final boolean admin)
    {
        Inventory gui = Bukkit.createInventory(null, 54, Strings.UI_WD_TITLE + (admin ? " §4(ADMIN MODE)" : ""));
        WitchdoctorGuiController controller = new WitchdoctorGuiController(gui, player, this, plugin, admin);
        plugin.guiMap.put(player.getUniqueId(), controller);
    }

    // --- Interaction Event Handlers

    /**
     * Handles clicks on the open witchdoctor GUI.
     * @param event The event.
     */
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        InventoryView view = event.getView();
        WitchdoctorGuiController controller = getValidController(view, event.getInventory());

        if(controller == null)
        {
            return;
        }

        if(event.isShiftClick())
        {
            event.setCancelled(true);
        }

        ItemStack slotItem = event.getCurrentItem();
        boolean slotEmpty = ItemUtil.isNullOrAir(slotItem);
        ItemStack cursorItem = view.getCursor();
        boolean cursorEmpty = ItemUtil.isNullOrAir(cursorItem);

        if(!slotEmpty)
        {
            if(GuiUtil.isUninteractable(slotItem))
            {
                event.setCancelled(true);
            }
        }

        if(event.getRawSlot() == scrollSlot)
        {
            event.setCancelled(true);
            controller.clickScrollSlot(view, slotItem, cursorItem, slotEmpty, cursorEmpty);
            return;
        }

        if(event.getRawSlot() == cureSlot && controller.getScroll() != null)
        {
            controller.cureVillager();
            return;
        }

        if(event.getRawSlot() == commitSlot && controller.canCommit())
        {
            controller.commitChanges();
            return;
        }

        if(event.getRawSlot() == buyScrollSlot)
        {
            controller.buyScroll();
            return;
        }

        if(event.getRawSlot() == extendTradeSlotsSlot && controller.getScroll() != null)
        {
            controller.extendSlots();
            return;
        }

        // Handling for movable items (trade recipes), can be moved around inside the UI but not taken out

        int slotIndex = event.getRawSlot();
        boolean slotMovable = GuiUtil.isMovable(slotItem);
        boolean cursorMovable = GuiUtil.isMovable(cursorItem);

        if(controller.isTradeSlot(slotIndex))
        {
            event.setCancelled(true);
            controller.clickTradeSlot(view, slotItem, cursorItem, event, slotIndex, slotEmpty, cursorEmpty, slotMovable, cursorMovable);
        }
        else if(cursorMovable && !GuiUtil.isFree(cursorItem))
        {
            event.setCancelled(true);
        }

        if(slotMovable && cursorEmpty && event.isShiftClick() && event.isLeftClick()) // Purge trade
        {
            event.setCancelled(true);
            controller.purgeTrade(slotIndex);
            view.setCursor(new ItemStack(Material.AIR));
        }

        if(slotMovable && cursorEmpty && event.isShiftClick() && event.isRightClick())
        {
            event.setCancelled(true);

            if(controller.extractPerms)
            {
                controller.extractTrade(slotIndex);
            }
        }
    }

    // --- Shenanigans Preventing Handlers

    /**
     * Prevents dragging in witchdoctor GUIs.
     * @param event The event.
     */
    @EventHandler
    public void onInventoryDragged(final InventoryDragEvent event)
    {
        InventoryView view = event.getView();
        WitchdoctorGuiController controller = getValidController(view, event.getInventory());

        if(controller == null)
        {
            return;
        }

        event.setCancelled(true);
    }

    /**
     * Prevents a player from dropping movable items out of the witchdoctor GUI by holding them with their cursor and closing the GUI.
     * @param event The event.
     */
    @EventHandler
    public void onItemDropped(final PlayerDropItemEvent event)
    {
        if(GuiUtil.isMovable(event.getItemDrop().getItemStack()))
        {
            event.getItemDrop().remove();
        }
    }

    // --- Handlers to ensure the player keeps their scroll and extracted trades.

    /**
     * Ensures the player gets their items back when the witchdoctor GUI is closed.
     * @param event The event.
     */
    @EventHandler
    public void onCloseInventory(final InventoryCloseEvent event)
    {
        InventoryView view = event.getView();
        WitchdoctorGuiController controller = getValidController(view, event.getInventory());

        if(controller == null)
        {
            return;
        }

        returnItemsAndRemoveFromMap(controller, controller.getPlayer());
    }

    /**
     * Ensures the player gets their items back when they get disconnected.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        UUID uuid = event.getPlayer().getUniqueId();
        if(plugin.guiMap.containsKey(uuid))
        {
            returnItemsAndRemoveFromMap(plugin.guiMap.get(uuid), event.getPlayer());
        }
    }

    // --- Cleanup Methods

    /**
     * Called on disable, returns items to players who still have a witchdoctor GUI open.
     */
    public void cleanupOpenGuis()
    {
        for(WitchdoctorGuiController controller : plugin.guiMap.values())
        {
            HumanEntity player = controller.getPlayer();
            returnItemsAndRemoveFromMap(controller, player);
        }
    }

    /**
     * Returns the scroll as well as any uncommitted free trade items to the player's inventory.
     * @param controller The controller associated with the witchdoctor GUI.
     * @param player The player who opened the GUI.
     */
    private void returnItemsAndRemoveFromMap(@NotNull final WitchdoctorGuiController controller, @NotNull final HumanEntity player)
    {
        if(controller.getScroll() != null)
        {
            player.getInventory().addItem(controller.getScroll());
        }

        for(ItemStack item : controller.getFreeTradeItems())
        {
            player.getInventory().addItem(item);
        }

        plugin.guiMap.remove(player.getUniqueId());
    }

    // Utility Methods

    /**
     * Checks whether the currently open GUI is a witchdoctor GUI. Protected against renamed chest since those won't have a guiMap entry.
     * @param view The open inventory view.
     * @param inventory The open inventory.
     * @return A WitchdoctorGuiController or null.
     */
    private WitchdoctorGuiController getValidController(@NotNull InventoryView view, @NotNull final Inventory inventory)
    {
        if(!view.getTitle().startsWith(Strings.UI_WD_TITLE))
        {
            return null;
        }

        HumanEntity player = inventory.getViewers().get(0);
        return plugin.guiMap.get(player.getUniqueId());
    }
}
