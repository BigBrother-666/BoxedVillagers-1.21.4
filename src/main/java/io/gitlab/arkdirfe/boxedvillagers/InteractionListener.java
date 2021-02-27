package io.gitlab.arkdirfe.boxedvillagers;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
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

            if(nbtItem.hasKey(BoxedVillagers.TAG_BOXED_VILLAGER_ITEM))
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
                    item = data.writeToItem(nbtItem, true);

                    Util.updateBoundScrollTooltip(item, data);

                    player.getInventory().setItemInMainHand(item);

                    // Particles and sounds
                    player.playSound(villager.getLocation(), Sound.ENTITY_WITHER_DEATH, SoundCategory.NEUTRAL, 0.25f, 2);
                    player.playSound(villager.getLocation(), Sound.BLOCK_BELL_RESONATE, SoundCategory.NEUTRAL, 1, 0.75f);

                    double x = villager.getLocation().getX();
                    double y = villager.getLocation().getY() + 1f;
                    double z = villager.getLocation().getZ();

                    player.getWorld().spawnParticle(Particle.ASH, x, y, z, 750, 0.3f, 0.5f, 0.3f);
                    player.getWorld().spawnParticle(Particle.LAVA, x, y, z, 25, 0.2f, 0.5f, 0.2f, 0);
                    player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, x, y, z, 50, 0.2f, 0.5f, 0.2f, 0.01f);

                    // Delete villager

                    villager.remove();
                }
            }
        }
    }

    @EventHandler
    public void onClickItem(PlayerInteractEvent event)
    {
        if(event.getHand() == EquipmentSlot.OFF_HAND)
        {
            return;
        }

        System.out.println(event.getAction().toString());

        if(event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK)
        {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if(item.getType() == Material.AIR)
        {
            return;
        }

        NBTItem nbtItem = new NBTItem(item);

        if(nbtItem.hasKey(BoxedVillagers.TAG_BOXED_VILLAGER_ITEM) && nbtItem.getBoolean(BoxedVillagers.TAG_IS_BOUND))
        {
            event.setCancelled(true);
            VillagerData data = new VillagerData(nbtItem);
            data.attemptRestock();
            item = data.writeToItem(nbtItem, false);
            player.getInventory().setItemInMainHand(item);

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

            if(nbtItem.hasKey(BoxedVillagers.TAG_BOXED_VILLAGER_ITEM) && nbtItem.getBoolean(BoxedVillagers.TAG_IS_BOUND))
            {
                VillagerData data = new VillagerData(nbtItem);
                data.updateUses(((MerchantInventory) player.getOpenInventory().getTopInventory()).getMerchant());
                item = data.writeToItem(nbtItem, false);

                player.getInventory().setItemInMainHand(item);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        ItemStack item = event.getCurrentItem();

        if(item == null || item.getType() == Material.AIR)
        {
            return;
        }

        NBTItem nbtItem = new NBTItem(item);

        if(nbtItem.hasKey(BoxedVillagers.TAG_BOXED_VILLAGER_ITEM) && nbtItem.getBoolean(BoxedVillagers.TAG_IS_BOUND))
        {
            HumanEntity player = event.getWhoClicked();
            if(player.getOpenInventory().getTopInventory() instanceof MerchantInventory)
            {
                event.setCancelled(true);
            }
        }
    }
}

