package io.gitlab.arkdirfe.boxedvillagers.ui;

import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.data.WitchdoctorGuiData;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import io.gitlab.arkdirfe.boxedvillagers.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WitchdoctorGuiManager implements Listener
{
    private final BoxedVillagers plugin;
    private final Map<UUID, WitchdoctorGuiData> guiMap;

    private final int scrollSlot = getSlot(1, 4);

    public WitchdoctorGuiManager(BoxedVillagers plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        guiMap = new HashMap<>();
    }

    public void openGui(final HumanEntity player)
    {
        Inventory gui = Bukkit.createInventory(null, 54, Strings.UI_WD_TITLE);

        player.openInventory(gui);
        WitchdoctorGuiData data = new WitchdoctorGuiData(gui);
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

        items[getSlot(0, 4)] = getScrollIndicator();
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

            items[getSlot(1, 1)] = null; // Cure button
            items[getSlot(1, 7)] = null; // Commit changes button

            // Draw trades

            data.gui.setContents(items);
        }
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
                Arrays.asList("§r§fPlace your scroll below to begin the process."),
                Arrays.asList(ItemFlag.HIDE_ENCHANTS));

        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        return setUninteractable(item);
    }

    // Event Handlers. At this point I know only players can open this UI so I can skip some checks.

    @EventHandler
    public void onCloseInventory(final InventoryCloseEvent event)
    {
        if(event.getView().getTitle().equals(Strings.UI_WD_TITLE))
        {
            return;
        }

        HumanEntity player =  event.getInventory().getViewers().get(0);
        WitchdoctorGuiData data = guiMap.get(player.getUniqueId());

        if(data == null)
        {
            return;
        }

        ItemStack scroll = data.gui.getItem(scrollSlot);

        if(scroll != null)
        {
            player.getInventory().addItem(scroll);
        }

        guiMap.remove(player.getUniqueId());
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

        ItemStack item = event.getCurrentItem();

        if(Util.isValidItem(item))
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

            data.scroll = data.gui.getItem(scrollSlot);
            updateGui(data);
        }

        event.getWhoClicked().sendMessage("" + event.getRawSlot());
    }
}
