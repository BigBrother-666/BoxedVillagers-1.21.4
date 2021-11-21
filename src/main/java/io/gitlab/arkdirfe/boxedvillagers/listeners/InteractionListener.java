package io.gitlab.arkdirfe.boxedvillagers.listeners;

import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.data.VillagerData;
import io.gitlab.arkdirfe.boxedvillagers.util.ItemUtil;
import io.gitlab.arkdirfe.boxedvillagers.util.StringFormatter;
import io.gitlab.arkdirfe.boxedvillagers.util.StringRef;
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
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

import java.util.concurrent.ThreadLocalRandom;

public class InteractionListener implements Listener
{
    /**
     * Handles player-world interactions in regard to villagers and scrolls.
     *
     * @param plugin Reference to the plugin.
     */
    public InteractionListener(final BoxedVillagers plugin)
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Handles player right-clicks on a villager with an unbound scroll in hand.
     *
     * @param event The event.
     */
    @EventHandler
    public void onVillagerInteract(final PlayerInteractEntityEvent event)
    {
        // If the event is cancelled (most likely due to some world protection plugin) do not execute, don't want people stealing others villagers.
        if(event.getHand() == EquipmentSlot.OFF_HAND || !event.getPlayer().isSneaking() || event.isCancelled())
        {
            return;
        }
        
        if(event.getRightClicked() instanceof Villager villager)
        {
            Player player = event.getPlayer();
            NBTItem nbtItem = ItemUtil.validateUnboundItem(player.getInventory().getItemInMainHand());
            
            if(nbtItem != null)
            {
                event.setCancelled(true);
                
                if(!player.hasPermission(Strings.PERM_CAPTURE))
                {
                    player.sendMessage(StringFormatter.formatLine(Strings.get(StringRef.CHAT_NO_CAPTURE_PERMISSION)));
                    return;
                }
                
                {
                    if(nbtItem.getBoolean(Strings.TAG_IS_BOUND))
                    {
                        player.sendMessage(StringFormatter.formatLine(Strings.get(StringRef.CHAT_SCROLL_BOUND)));
                        return;
                    }
                }
                
                if(villager.getRecipeCount() == 0)
                {
                    player.sendMessage(StringFormatter.formatLine(Strings.get(StringRef.CHAT_NO_TRADES)));
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
                        villager.damage(999999999);
                    }
                }
            }
        }
    }
    
    /**
     * Handles the player left-clicking with a bound scroll to open the trade UI.
     *
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
     *
     * @param event The event.
     */
    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event)
    {
        HumanEntity player = event.getPlayer();
        if(player.getOpenInventory().getTopInventory() instanceof MerchantInventory merchantInventory)
        {
            // Players can't rename in-world villagers to an empty string, if the title is empty it has to be a Merchant Inventory generated by this plugin.
            if(!player.getOpenInventory().getTitle().equals(""))
            {
                return;
            }
            
            NBTItem nbtItem = ItemUtil.validateBoundItem(player.getInventory().getItemInMainHand());
            
            if(nbtItem != null)
            {
                VillagerData data = new VillagerData(nbtItem);
                data.updateUses(merchantInventory.getMerchant());
                
                player.getInventory().setItemInMainHand(data.getItem());
            }
        }
    }
    
    /**
     * Prevents dropping of scrolls while in a merchant GUI. Also handles giving players XP rewards for trading.
     *
     * @param event The event.
     */
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        if(player.getOpenInventory().getTopInventory() instanceof MerchantInventory merchantInventory)
        {
            // Players can't rename in-world villagers to an empty string, if the title is empty it has to be a Merchant Inventory generated by this plugin.
            if(!player.getOpenInventory().getTitle().equals(""))
            {
                return;
            }
            
            if(event.getRawSlot() == 2 && BoxedVillagers.getMaxXPReward() > 0) // 2 is trade output slot, ignore everything if no xp rewards are set up
            {
                MerchantRecipe selected = merchantInventory.getSelectedRecipe();
                if(selected != null && event.getRawSlot() == 2 && selected.getUses() < selected.getMaxUses())
                {
                    int tradeCount = 1;
                    if(event.isShiftClick())
                    {
                        int primaryCost = selected.getIngredients().get(0).getAmount();
                        int secondaryCost = selected.getIngredients().get(1).getAmount();
                        int primaryCount = merchantInventory.getItem(0) == null ? 0 : merchantInventory.getItem(0).getAmount();
                        int secondaryCount = merchantInventory.getItem(1) == null ? 0 : merchantInventory.getItem(1).getAmount();
                        
                        if(secondaryCost == 0)
                        {
                            tradeCount = Math.min(primaryCount / primaryCost, selected.getMaxUses() - selected.getUses());
                        }
                        else
                        {
                            tradeCount = Math.min(Math.min(primaryCount / primaryCost, secondaryCount / secondaryCost), selected.getMaxUses() - selected.getUses());
                        }
                    }
                    
                    while(tradeCount > 0)
                    {
                        tradeCount--;
                        player.giveExp(ThreadLocalRandom.current().nextInt(BoxedVillagers.getMinXPReward(), BoxedVillagers.getMaxXPReward()));
                    }
                    
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, (float) (0.75f + Math.random() * 0.5f));
                }
                
                return; // Can't be a click on scroll so exit early
            }
            
            NBTItem nbtItem = ItemUtil.validateBoundItem(event.getCurrentItem());
            
            if(nbtItem != null)
            {
                event.setCancelled(true);
            }
        }
    }
    
    /**
     * Prevents using special items from being used in crafting recipes.
     *
     * @param event The event.
     */
    @EventHandler
    public void onPrepareItemCraft(final PrepareItemCraftEvent event)
    {
        for(ItemStack stack : event.getInventory().getStorageContents())
        {
            if(ItemUtil.isFromThisPlugin(stack))
            {
                event.getInventory().setResult(null);
                break;
            }
        }
    }
}

