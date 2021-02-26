package io.gitlab.arkdirfe.boxedvillagers;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

public class InteractionListener implements Listener
{
    public InteractionListener(BoxedVillagers plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onVillagerInteract(PlayerInteractEntityEvent event)
    {
        if(event.getHand() == EquipmentSlot.OFF_HAND)
        {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if(event.getRightClicked() instanceof Villager)
        {
            Villager villager = (Villager) event.getRightClicked();
            NBTItem nbtItem = new NBTItem(item);

            if(nbtItem.getUUID(BoxedVillagers.TAG_BOXED_VILLAGER_ITEM) != null)
            {
                event.setCancelled(true);

                if(nbtItem.getBoolean(BoxedVillagers.TAG_IS_BOUND))
                {
                    player.sendMessage("Scroll already bound!");
                    return;
                }

                if(villager.getRecipeCount() == 0)
                {
                    player.sendMessage("That villager has no trades!");
                }
                else
                {
                    VillagerData data = new VillagerData(villager);
                    data.writeToItem(nbtItem, true);
                    item = nbtItem.getItem();

                    Util.updateBoundScrollTooltip(item, data);

                    player.getInventory().setItemInMainHand(item);

                    // Particles and sounds

                    // Delete villager
                }
            }
        }
    }

    @EventHandler
    public void onRightClickItem(PlayerInteractEvent event)
    {
        if(event.getHand() == EquipmentSlot.OFF_HAND)
        {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        NBTItem nbtItem = new NBTItem(item);

        if(nbtItem.getUUID(BoxedVillagers.TAG_BOXED_VILLAGER_ITEM) != null && nbtItem.getBoolean(BoxedVillagers.TAG_IS_BOUND))
        {
            event.setCancelled(true);
            VillagerData data = new VillagerData(nbtItem);
            //data.attemptRestock(); // TODO: Uncomment once timestamps are in

            Merchant merchant = Bukkit.createMerchant(data.professionAsString());
            merchant.setRecipes(data.trades);
            player.openMerchant(merchant, true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        HumanEntity player = event.getPlayer();
        if(player.getOpenInventory().getTopInventory() instanceof MerchantInventory)
        {
            ItemStack item = player.getInventory().getItemInMainHand();
            NBTItem nbtItem = new NBTItem(item);

            if(nbtItem.getUUID(BoxedVillagers.TAG_BOXED_VILLAGER_ITEM) != null && nbtItem.getBoolean(BoxedVillagers.TAG_IS_BOUND))
            {
                VillagerData data = new VillagerData(nbtItem);
                data.updateUses(((MerchantInventory) player.getOpenInventory().getTopInventory()).getMerchant());
                data.writeToItem(nbtItem, false);
                item = nbtItem.getItem();
                player.getInventory().setItemInMainHand(item);
            }
        }
    }
}

