package io.gitlab.arkdirfe.boxedvillagers.listeners;

import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.data.VillagerData;
import io.gitlab.arkdirfe.boxedvillagers.util.ItemUtil;
import io.gitlab.arkdirfe.boxedvillagers.util.StringFormatter;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;

public class InteractionListener implements Listener
{
    /**
     * Handles player-world interactions with regards to villagers and scrolls.
     * @param plugin Reference to the plugin.
     */
    public InteractionListener(final BoxedVillagers plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Handles a player right clicks on a villager with an unbound scroll in hand.
     * @param event The event.
     */
    @EventHandler
    public void onVillagerInteract(final PlayerInteractEntityEvent event)
    {
        if(event.getHand() == EquipmentSlot.OFF_HAND || !event.getPlayer().isSneaking())
        {
            return;
        }

        if(event.getRightClicked() instanceof Villager)
        {
            Player player = event.getPlayer();
            NBTItem nbtItem = ItemUtil.validateUnboundItem(player.getInventory().getItemInMainHand());

            if(nbtItem != null)
            {
                event.setCancelled(true);

                if(nbtItem.getBoolean(Strings.TAG_IS_BOUND))
                {
                    player.sendMessage(StringFormatter.formatLine(Strings.CHAT_SCROLL_BOUND));
                    return;
                }

                Villager villager = (Villager) event.getRightClicked();

                if(villager.getRecipeCount() == 0)
                {
                    player.sendMessage(StringFormatter.formatLine(Strings.CHAT_NO_TRADES));
                }
                else
                {
                    boolean nonlethal = nbtItem.hasKey(Strings.TAG_NONLETHAL);
                    VillagerData data = new VillagerData(villager, nbtItem);
                    player.getInventory().setItemInMainHand(data.getItem());

                    // Particles and sounds
                    player.playSound(villager.getLocation(), Sound.ENTITY_WITHER_DEATH, SoundCategory.NEUTRAL, 0.25f, 2);
                    player.playSound(villager.getLocation(), Sound.BLOCK_BELL_RESONATE, SoundCategory.NEUTRAL, 1, 0.75f);

                    double x = villager.getLocation().getX();
                    double y = villager.getLocation().getY() + 1f;
                    double z = villager.getLocation().getZ();

                    player.getWorld().spawnParticle(Particle.ASH, x, y, z, 750, 0.3f, 0.5f, 0.3f);
                    player.getWorld().spawnParticle(Particle.LAVA, x, y, z, 25, 0.2f, 0.5f, 0.2f, 0);
                    player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, x, y, z, 50, 0.2f, 0.5f, 0.2f, 0.01f);

                    // Delete villager if scroll is lethal
                    if(!nonlethal)
                    {
                        villager.remove();
                    }
                }
            }
        }
    }

    /**
     * Handles the player left clicking in the air with a bound scroll to open the trade UI.
     * @param event The event.
     */
    @EventHandler
    public void onClickItem(final PlayerInteractEvent event)
    {
        if(event.getHand() == EquipmentSlot.OFF_HAND || event.getPlayer().isSneaking())
        {
            return;
        }

        Player player = event.getPlayer();
        NBTItem nbtItem = ItemUtil.validateBoundItem(player.getInventory().getItemInMainHand());

        if(nbtItem != null)
        {
            event.setCancelled(true);

            if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            {
                return;
            }

            VillagerData data = new VillagerData(nbtItem);
            data.attemptRestock();
            player.getInventory().setItemInMainHand(data.getItem());

            Merchant merchant = Bukkit.createMerchant(""); // Used for checking whether it's a real villager or my UI, players can't use name tags to change villager name to empty string
            merchant.setRecipes(data.getMerchantRecipes());
            player.openMerchant(merchant, true);
        }
    }

    /**
     * When a merchant GUI is closed that was generated by this plugin, updates the uses of the trades in the scroll.
     * @param event The event.
     */
    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event)
    {
        HumanEntity player = event.getPlayer();
        if(player.getOpenInventory().getTopInventory() instanceof MerchantInventory)
        {
            if(!player.getOpenInventory().getTitle().equals("")) // Players can't rename their villagers to an empty string.
            {
                return;
            }

            NBTItem nbtItem = ItemUtil.validateBoundItem(player.getInventory().getItemInMainHand());

            if(nbtItem != null)
            {
                VillagerData data = new VillagerData(nbtItem);
                data.updateUses(((MerchantInventory) player.getOpenInventory().getTopInventory()).getMerchant());

                player.getInventory().setItemInMainHand(data.getItem());
            }
        }
    }

    /**
     * Prevents dropping of scrolls while in a merchant GUI.
     * @param event The event.
     */
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        HumanEntity player = event.getWhoClicked();
        if(!player.getOpenInventory().getTitle().equals("")) // Players can't rename their villagers to an empty string.
        {
            return;
        }

        NBTItem nbtItem = ItemUtil.validateBoundItem(event.getCurrentItem());

        if(nbtItem != null && player.getOpenInventory().getTopInventory() instanceof MerchantInventory)
        {
            event.setCancelled(true);
        }
    }
}

