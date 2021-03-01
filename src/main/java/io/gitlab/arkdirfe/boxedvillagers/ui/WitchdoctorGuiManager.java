package io.gitlab.arkdirfe.boxedvillagers.ui;


import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
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
    public final Map<Material, Integer> cureTier1CostMap;
    public final Map<Material, Integer> cureTier2CostMap;
    public final Map<Material, Integer> cureTier3CostMap;
    public final Map<Material, Integer> purgeCostMap;

    private final int scrollSlot = Util.getGuiSlot(1, 4);
    private final int helpSlot = Util.getGuiSlot(0, 4);
    private final int cureSlot = Util.getGuiSlot(1, 1);
    private final int commitSlot = Util.getGuiSlot(1, 7);
    private final List<Integer> tradeSlots = Arrays.asList(Util.getGuiSlot(3, 2), Util.getGuiSlot(3, 3), Util.getGuiSlot(3, 4), Util.getGuiSlot(3, 5), Util.getGuiSlot(3, 6),
            Util.getGuiSlot(4, 2), Util.getGuiSlot(4, 3), Util.getGuiSlot(4, 4), Util.getGuiSlot(4, 5), Util.getGuiSlot(4, 6));

    public WitchdoctorGuiManager(BoxedVillagers plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.plugin = plugin;
        this.guiMap = plugin.guiMap;
        this.cureTier1CostMap = plugin.cureTier1CostMap;
        this.cureTier2CostMap = plugin.cureTier2CostMap;
        this.cureTier3CostMap = plugin.cureTier3CostMap;
        this.purgeCostMap = plugin.purgeCostMap;
    }

    public void openGui(final HumanEntity player)
    {
        Inventory gui = Bukkit.createInventory(null, 54, Strings.UI_WD_TITLE);
        WitchdoctorGuiController data = new WitchdoctorGuiController(gui, player, scrollSlot, helpSlot, cureSlot, commitSlot, tradeSlots, this);
        guiMap.put(player.getUniqueId(), data);
    }

    // --- Interaction Event Handlers

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        InventoryView view = event.getView();
        if(!view.getTitle().equalsIgnoreCase(Strings.UI_WD_TITLE))
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

        // Handling for movable items (trade recipes), can be moved around inside the UI but not taken out

        int slotIndex = event.getRawSlot();
        boolean slotMovable = GuiUtil.isMovable(slotItem);
        boolean cursorMovable = GuiUtil.isMovable(cursorItem);

        if(tradeSlots.contains(slotIndex))
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
            else if((slotMovable && cursorEmpty))
            {
                controller.getGui().setItem(slotIndex, null);
                view.setCursor(slotItem);
            }

            controller.updateCommitButton();
        }
        else if(cursorMovable)
        {
            event.setCancelled(true);
        }

        if(slotMovable && cursorEmpty && event.isShiftClick()) // Purge trade
        {
            event.setCancelled(true);
            controller.purgeTrade(slotIndex);
            view.setCursor(new ItemStack(Material.AIR));
        }
    }

    // --- Shenanigans Preventing Handlers

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
        if(GuiUtil.isMovable(event.getItemDrop().getItemStack()))
        {
            event.getItemDrop().remove();
        }
    }

    // --- Handlers to ensure the player keeps their scroll

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
        WitchdoctorGuiController controller = guiMap.get(player.getUniqueId());

        if(controller == null)
        {
            return;
        }

        returnScrollAndRemoveFromMap(controller.getScroll(), player);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event)
    {
        UUID uuid = event.getPlayer().getUniqueId();
        if(guiMap.containsKey(uuid))
        {
            returnScrollAndRemoveFromMap(guiMap.get(uuid).getScroll(), event.getPlayer());
        }
    }

    // --- Cleanup Methods

    public void cleanupOpenGuis()
    {
        for(WitchdoctorGuiController controller : guiMap.values())
        {
            HumanEntity player = controller.getPlayer();
            returnScrollAndRemoveFromMap(controller.getScroll(), player);
        }
    }

    private void returnScrollAndRemoveFromMap(ItemStack scroll, HumanEntity player)
    {
        if(scroll != null)
        {
            player.getInventory().addItem(scroll);
        }

        guiMap.remove(player.getUniqueId());
    }
}
