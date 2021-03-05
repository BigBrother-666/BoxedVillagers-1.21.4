package io.gitlab.arkdirfe.boxedvillagers.commands;

import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.data.HelpData;
import io.gitlab.arkdirfe.boxedvillagers.data.VillagerData;
import io.gitlab.arkdirfe.boxedvillagers.util.ItemUtil;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoxedVillagersCommandExecutor implements TabExecutor
{
    private final BoxedVillagers plugin;
    private final int helpWidth;

    /**
     * Handles the /boxedvillagers or /bv commands
     * @param plugin Reference to the plugin.
     * @param commandName Name of the command.
     */
    public BoxedVillagersCommandExecutor(@NotNull final BoxedVillagers plugin, @NotNull final String commandName)
    {
        this.plugin = plugin;
        this.helpWidth = plugin.getConfig().getInt(Strings.CONFIG_HELP_WIDTH);
        PluginCommand cmd = plugin.getCommand(commandName);
        if(cmd != null)
        {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
        else
        {
            plugin.getLogger().severe("Unable to register BoxedVillager commands! This should never happen, if it does, fix yer damn strings!");
        }
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args)
    {
        if(args.length > 0)
        {
            String subCmd = args[0];

            if(sender.hasPermission(Strings.PERM_ADMIN)) // Admin Commands
            {
                if(subCmd.equalsIgnoreCase(Strings.CMD_BV_GIVE))
                {
                    if(args.length > 1)
                    {
                        String itemName = args[1];
                        if(itemName.equalsIgnoreCase(Strings.CMD_BV_GIVE_UNBOUND_SCROLL))
                        {
                            Player player = getPlayer(args, sender, args.length == 2, args.length == 3);
                            if(player != null)
                            {
                                player.getInventory().addItem(ItemUtil.getUnboundScroll(false));
                            }
                        }
                        else if(itemName.equalsIgnoreCase(Strings.CMD_BV_GIVE_UNBOUND_SCROLL_NONLETHAL))
                        {
                            Player player = getPlayer(args, sender, args.length == 2, args.length == 3);
                            if(player != null)
                            {
                                player.getInventory().addItem(ItemUtil.getUnboundScroll(true));
                            }
                        }
                        else if(itemName.equalsIgnoreCase(Strings.CMD_BV_GIVE_TRADE))
                        {
                            Player player = getPlayer(args, sender, args.length == 7, args.length == 8);
                            if(player != null)
                            {
                                ItemStack item = ItemUtil.getGeneratedTradeItem(player, args);
                                if(item != null)
                                {
                                    player.getInventory().addItem(item);
                                }
                                else
                                {
                                    sender.sendMessage(Strings.ERROR_GIVE_TRADE_INVALID_SLOT);
                                }
                            }
                        }
                    }
                }
                else if(subCmd.equalsIgnoreCase(Strings.CMD_BV_RELOAD))
                {
                    plugin.reloadConfig();
                }
            }

            // Player Commands

            if(subCmd.equalsIgnoreCase("help"))
            {
                if(args.length == 1 && plugin.helpPages.containsKey("default"))
                {
                    sender.sendMessage(plugin.helpPages.get("default").getFormatted(helpWidth));
                }
                else if(args.length == 2)
                {
                    HelpData help = plugin.helpPages.get(args[1]);
                    if(help != null)
                    {
                        sender.sendMessage(help.getFormatted(helpWidth));
                    }
                    else
                    {
                        sender.sendMessage("§cNo help page available under this name!");
                    }
                }
            }
            else if(subCmd.equalsIgnoreCase(Strings.CMD_BV_RENAME))
            {
                if(args.length > 1)
                {
                    if(sender instanceof Player)
                    {
                        Player player = (Player) sender;

                        ItemStack item = player.getInventory().getItemInMainHand();
                        NBTItem nbtItem = ItemUtil.validateBoundItem(item);
                        if(nbtItem != null)
                        {
                            StringBuilder newName = new StringBuilder();

                            for(int i = 1; i < args.length; i++)
                            {
                                newName.append(args[i]).append(" ");
                            }

                            VillagerData data = new VillagerData(nbtItem);
                            data.rename(newName.toString());
                            player.getInventory().setItemInMainHand(data.getItem());
                        }
                    }
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args)
    {
        if(sender instanceof Player)
        {
            if(sender.hasPermission(Strings.PERM_ADMIN))
            {
                if((args.length == 1 || args.length == 2))
                {
                    if(args[0].equalsIgnoreCase(Strings.CMD_BV_GIVE))
                    {
                        return Arrays.asList(Strings.CMD_BV_GIVE_UNBOUND_SCROLL, Strings.CMD_BV_GIVE_UNBOUND_SCROLL_NONLETHAL, Strings.CMD_BV_GIVE_TRADE);
                    }
                }

                if((args.length == 0 || args.length == 1))
                {
                    return Arrays.asList(Strings.CMD_BV_GIVE, Strings.CMD_BV_HELP, Strings.CMD_BV_RELOAD, Strings.CMD_BV_RENAME);
                }

                if(args[0].equalsIgnoreCase(Strings.CMD_BV_GIVE) && args[1].equalsIgnoreCase(Strings.CMD_BV_GIVE_TRADE))
                {
                    return new ArrayList<>();
                }
            }

            // Player Commands

            if((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase(Strings.CMD_BV_HELP))
            {
                return new ArrayList<>(plugin.helpPages.keySet());
            }

            if((args.length == 0 || args.length == 1))
            {
                return Arrays.asList(Strings.CMD_BV_HELP, Strings.CMD_BV_RENAME);
            }

            if(args.length == 3 && !args[0].equalsIgnoreCase(Strings.CMD_BV_HELP))
            {
                return null;
            }
        }

        return new ArrayList<>();
    }

    /**
     * Returns a player object depending on conditions.
     * @param args Command arguments.
     * @param sender Command sender.
     * @param senderPlayer Condition for when to interpret the sender as a player.
     * @param argPlayer Condition for when to get an online player from the args.
     * @return Player object if possible, null otherwise.
     */
    @Nullable
    private Player getPlayer(@NotNull final String[] args, @NotNull final CommandSender sender, final boolean senderPlayer, final boolean argPlayer)
    {
        if(senderPlayer)
        {
            if(!(sender instanceof Player))
            {
                sender.sendMessage("Commands that are not reload not available from console!");
                return null;
            }

            return (Player) sender;
        }
        if(argPlayer)
        {
            Player player = Bukkit.getServer().getPlayer(args[args.length - 1]);
            if(player == null)
            {
                sender.sendMessage("Player offline!");
            }

            return player;
        }

        return null;
    }
}
