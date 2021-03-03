package io.gitlab.arkdirfe.boxedvillagers.ui;


import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.data.CostData;
import io.gitlab.arkdirfe.boxedvillagers.util.GuiUtil;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import io.gitlab.arkdirfe.boxedvillagers.util.Util;
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

import java.util.*;

public class WitchdoctorGuiManager implements Listener
{
    private final BoxedVillagers plugin;
    private final Map<UUID, WitchdoctorGuiController> guiMap;
    public final List<CostData> cureCosts;
    public final List<CostData> slotExtensionCosts;
    public final CostData purgeCost;
    public final CostData scrollCost;
    public final CostData extractCost;
    public final CostData addCost;

    public final int scrollSlot = GuiUtil.getGuiSlot(1, 4);
    public final int helpSlot = GuiUtil.getGuiSlot(0, 4);
    public final int cureSlot = GuiUtil.getGuiSlot(1, 2);
    public final int commitSlot = GuiUtil.getGuiSlot(1, 6);
    public final int extendTradeSlotsSlot = GuiUtil.getGuiSlot(1, 0);
    public final int buyScrollSlot = GuiUtil.getGuiSlot(1, 8);
    public final int tradeSlotStart = GuiUtil.getGuiSlot(3, 0);

    public WitchdoctorGuiManager(BoxedVillagers plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.plugin = plugin;
        this.guiMap = plugin.guiMap;
        this.cureCosts = plugin.cureCosts;
        this.slotExtensionCosts = plugin.slotExtensionCosts;
        this.purgeCost = plugin.purgeCost;
        this.scrollCost = plugin.scrollCost;
        this.extractCost = plugin.extractCost;
        this.addCost = plugin.addCost;
    }

    public void openGui(final HumanEntity player, boolean admin)
    {
        Inventory gui = Bukkit.createInventory(null, 54, Strings.UI_WD_TITLE + (admin ? " §4(ADMIN MODE)" : ""));
        WitchdoctorGuiController controller = new WitchdoctorGuiController(gui, player, this, admin);
        guiMap.put(player.getUniqueId(), controller);
    }

    // --- Interaction Event Handlers

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        InventoryView view = event.getView();
        if(!view.getTitle().startsWith(Strings.UI_WD_TITLE))
        {
            return;
        }

        HumanEntity player =  event.getInventory().getViewers().get(0);
        WitchdoctorGuiController controller = guiMap.get(player.getUniqueId());

        if(controller == null)
        {
            return;
        }

        if(event.isShiftClick())
        {
            event.setCancelled(true);
        }

        ItemStack slotItem = event.getCurrentItem();
        boolean slotEmpty = !Util.isNotNullOrAir(slotItem);
        ItemStack cursorItem = view.getCursor();
        boolean cursorEmpty = !Util.isNotNullOrAir(cursorItem);

        if(!slotEmpty)
        {
            if(GuiUtil.isUninteractable(slotItem))
            {
                event.setCancelled(true);
            }
        }

        if(event.getRawSlot() == scrollSlot)
        {
            boolean slotScroll = Util.validateBoundItem(slotItem) != null;
            boolean cursorScroll = Util.validateBoundItem(cursorItem) != null;

            event.setCancelled(true);

            if((slotEmpty && cursorScroll))
            {
                controller.getGui().setItem(scrollSlot, cursorItem);
                view.setCursor(new ItemStack(Material.AIR));
            }
            else if((slotScroll && cursorScroll))
            {
                controller.getGui().setItem(scrollSlot, cursorItem);
                view.setCursor(slotItem);
            }
            else if((slotScroll && cursorEmpty))
            {
                controller.getGui().setItem(scrollSlot, null);
                view.setCursor(slotItem);
            }

            controller.resetTracking();

            controller.setScroll(controller.getGui().getItem(scrollSlot));
            controller.update();
            return;
        }

        if(event.getRawSlot() == cureSlot && controller.getScroll() != null)
        {
            if(controller.getVillagerData().getCures() != 7)
            {
                controller.cureVillager();
            }

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
            if((slotEmpty && cursorMovable))
            {
                controller.getGui().setItem(slotIndex, cursorItem);
                view.setCursor(new ItemStack(Material.AIR));
                controller.tradeMoved();
            }
            else if((slotMovable && cursorMovable))
            {
                controller.getGui().setItem(slotIndex, cursorItem);
                view.setCursor(slotItem);
                controller.tradeMoved();
            }
            else if((slotMovable && cursorEmpty) && event.isLeftClick() && !event.isShiftClick())
            {
                controller.getGui().setItem(slotIndex, null);
                view.setCursor(slotItem);
                controller.tradeMoved();
            }

            controller.updateCommitButton();
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

    @EventHandler
    public void onInventoryDragged(InventoryDragEvent event)
    {
        InventoryView view = event.getView();
        if(!view.getTitle().startsWith(Strings.UI_WD_TITLE))
        {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDropped(PlayerDropItemEvent event)
    {
        if(GuiUtil.isMovable(event.getItemDrop().getItemStack()))
        {
            event.getItemDrop().remove();
        }
    }

    // --- Handlers to ensure the player keeps their scroll

    @EventHandler
    public void onCloseInventory(final InventoryCloseEvent event)
    {
        if(!event.getView().getTitle().startsWith(Strings.UI_WD_TITLE))
        {
            return;
        }

        if(guiMap.size() == 0)
        {
            return;
        }

        HumanEntity player = event.getInventory().getViewers().get(0);
        WitchdoctorGuiController controller = guiMap.get(player.getUniqueId());

        if(controller == null)
        {
            return;
        }

        returnItemsAndRemoveFromMap(controller, player);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        UUID uuid = event.getPlayer().getUniqueId();
        if(guiMap.containsKey(uuid))
        {
            returnItemsAndRemoveFromMap(guiMap.get(uuid), event.getPlayer());
        }
    }

    // --- Cleanup Methods

    public void cleanupOpenGuis()
    {
        for(WitchdoctorGuiController controller : guiMap.values())
        {
            HumanEntity player = controller.getPlayer();
            returnItemsAndRemoveFromMap(controller, player);
        }
    }

    private void returnItemsAndRemoveFromMap(WitchdoctorGuiController controller, HumanEntity player)
    {
        if(controller.getScroll() != null)
        {
            player.getInventory().addItem(controller.getScroll());
        }

        for (ItemStack item : controller.getFreeTradeItems())
        {
            player.getInventory().addItem(item);
        }

        guiMap.remove(player.getUniqueId());
    }
}
