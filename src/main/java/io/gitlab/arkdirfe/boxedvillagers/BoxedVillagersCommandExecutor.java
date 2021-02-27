package io.gitlab.arkdirfe.boxedvillagers;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BoxedVillagersCommandExecutor implements TabExecutor
{
    private final BoxedVillagers plugin;

    public  BoxedVillagersCommandExecutor(BoxedVillagers plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(command.getName().equalsIgnoreCase("boxedvillagers") || command.getName().equalsIgnoreCase("bv"))
        {
            if(args.length > 0)
            {
                String subCmd = args[0];

                if(subCmd.equalsIgnoreCase("give"))
                {
                    Player player = getPlayer((args.length == 2 || args.length == 3), sender, args, 2);
                    if(player == null)
                    {
                        return true;
                    }

                    int slot = player.getInventory().firstEmpty();
                    if(slot < 0)
                    {
                        sender.sendMessage("Inventory Full!");
                    }
                    else
                    {
                        player.getInventory().addItem(getUnboundScroll());
                        return true;
                    }
                }
                else if (subCmd.equalsIgnoreCase("cure"))
                {
                    int numCures = 1;

                    Player player = getPlayer(args.length < 3, sender, args, 2);

                    if(player == null)
                    {
                        return true;
                    }

                    NBTItem nbtItem = Util.validateBoundItem(player.getInventory().getItemInMainHand());

                    if(nbtItem != null)
                    {
                        VillagerData data = new VillagerData(nbtItem);
                        if(data.cures == 7)
                        {
                            player.sendMessage("Already at max cures!");
                        }
                        else
                        {
                            if(args.length > 1)
                            {
                                numCures = Integer.parseInt(args[1]);
                            }

                            data.cure(nbtItem, numCures);
                            ItemStack item = data.writeToItem(nbtItem, false);
                            Util.updateBoundScrollTooltip(item, data);
                            player.getInventory().setItemInMainHand(item);
                            player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.NEUTRAL, 0.5f, 1);
                            player.sendMessage("Villager Cured!");
                        }
                    }
                    else
                    {
                        player.sendMessage("Invalid Item!");
                    }
                    return true;
                }
            }
        }

        return false;
    }

    private ItemStack getUnboundScroll()
    {
        ItemStack scroll = new ItemStack(Material.PAPER);
        ItemMeta meta = scroll.getItemMeta();
        meta.setDisplayName("§aUnbound Villager Scroll");
        meta.setLore(Arrays.asList("§r§fRight click on a villager to §4§mensnare its mortal soul§r§f capture it.",
                "§r§fCaptured villagers do not benefit from previous cures or",
                "§r§fHero of the Village and can not unlock additional trades."));
        scroll.setItemMeta(meta);

        NBTItem nbtscoll = new NBTItem(scroll);
        nbtscoll.setUUID(Strings.TAG_BOXED_VILLAGER_ITEM, UUID.randomUUID());
        nbtscoll.setBoolean(Strings.TAG_IS_BOUND, false);
        return nbtscoll.getItem();

    }

    private Player getPlayer(boolean condition, CommandSender sender, String[] args, int playerIndex)
    {
        if(condition)
        {
            if(!(sender instanceof Player))
            {
                sender.sendMessage("Use /bv [arg1] [arg2] [player] to run as non-player!");
                return null;
            }

            return (Player)sender;
        }
        else
        {
            Player player = Bukkit.getServer().getPlayer(args[playerIndex]);
            if(player == null)
            {
                sender.sendMessage("Player offline!");
            }

            return player;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        if(command.getName().equalsIgnoreCase("boxedvillagers") || command.getName().equalsIgnoreCase("bv"))
        {
            if(sender instanceof Player)
            {
                if((args.length == 1 || args.length == 2))
                {
                    if(args[0].equalsIgnoreCase("give"))
                    {
                        return Arrays.asList("unbound");
                    }
                    else if (args[0].equalsIgnoreCase("cure"))
                    {
                        return Arrays.asList("1", "2", "3", "4", "5", "6", "7");
                    }
                }

                if((args.length == 2) && args[0].equalsIgnoreCase("give"))
                {
                    return null;
                }

                if((args.length == 0 || args.length == 1))
                {
                    return Arrays.asList("give", "cure");
                }
            }
        }

        return null;
    }
}
