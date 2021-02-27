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

        if(event.getRightClicked() instanceof Villager)
        {
            Player player = event.getPlayer();
            NBTItem nbtItem = Util.validateUnboundItem(player.getInventory().getItemInMainHand());

            if(nbtItem != null)
            {
                event.setCancelled(true);

                if(nbtItem.getBoolean(Strings.TAG_IS_BOUND))
                {
                    player.sendMessage("Scroll already bound!");
                    return;
                }

                Villager villager = (Villager) event.getRightClicked();

                if(villager.getRecipeCount() == 0)
                {
                    player.sendMessage("That villager has no trades!");
                }
                else
                {
                    VillagerData data = new VillagerData(villager);

                    ItemStack item = data.writeToItem(nbtItem, true);
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

        if(event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK)
        {
            return;
        }

        Player player = event.getPlayer();
        NBTItem nbtItem = Util.validateBoundItem(player.getInventory().getItemInMainHand());

        if(nbtItem != null)
        {
            event.setCancelled(true);
            VillagerData data = new VillagerData(nbtItem);
            data.attemptRestock();
            player.getInventory().setItemInMainHand(data.writeToItem(nbtItem, false));

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
            NBTItem nbtItem = Util.validateBoundItem(player.getInventory().getItemInMainHand());

            if(nbtItem != null)
            {
                VillagerData data = new VillagerData(nbtItem);
                data.updateUses(((MerchantInventory) player.getOpenInventory().getTopInventory()).getMerchant());

                player.getInventory().setItemInMainHand(data.writeToItem(nbtItem, false));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        NBTItem nbtItem = Util.validateBoundItem(event.getCurrentItem());

        if(nbtItem != null)
        {
            HumanEntity player = event.getWhoClicked();
            if(player.getOpenInventory().getTopInventory() instanceof MerchantInventory)
            {
                event.setCancelled(true);
            }
        }
    }
}

