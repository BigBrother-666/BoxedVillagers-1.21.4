package io.gitlab.arkdirfe.boxedvillagers.commands;

import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.data.HelpData;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BoxedVillagersCommandExecutor implements TabExecutor
{
    private final BoxedVillagers plugin;

    /**
     * Handles the /boxedvillagers or /bv commands
     * @param plugin Reference to the plugin.
     * @param commandName Name of the command.
     */
    public BoxedVillagersCommandExecutor(@NotNull final BoxedVillagers plugin, @NotNull final String commandName)
    {
        this.plugin = plugin;
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
                            Player player = getPlayer(args.length == 3, sender, "");
                            if(player != null)
                            {
                                player.getInventory().addItem(ItemUtil.getUnboundScroll(false));
                            }
                        }
                        else if(itemName.equalsIgnoreCase(Strings.CMD_BV_GIVE_UNBOUND_SCROLL_NONLETHAL))
                        {
                            Player player = getPlayer(args.length == 3, sender, "");
                            if(player != null)
                            {
                                player.getInventory().addItem(ItemUtil.getUnboundScroll(true));
                            }
                        }
                        else if(itemName.equalsIgnoreCase(Strings.CMD_BV_GIVE_TRADE))
                        {
                            Player player = getPlayer(args.length == 7, sender, "");
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
                    sender.sendMessage(plugin.helpPages.get("default").getFormatted(50));
                }
                else if(args.length == 2)
                {
                    HelpData help = plugin.helpPages.get(args[1]);
                    if(help != null)
                    {
                        sender.sendMessage(help.getFormatted(50));
                    }
                    else
                    {
                        sender.sendMessage("§cNo help page available under this name!");
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
                    if(sender.hasPermission(Strings.PERM_ADMIN))
                    {
                        return Arrays.asList(Strings.CMD_BV_GIVE, Strings.CMD_BV_HELP, Strings.CMD_BV_RELOAD);
                    }
                }

                if(args.length >= 2 && args[0].equalsIgnoreCase(Strings.CMD_BV_GIVE) && args[1].equalsIgnoreCase(Strings.CMD_BV_GIVE_TRADE))
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
                return Collections.singletonList(Strings.CMD_BV_HELP);
            }

            if(args.length == 3 && !args[0].equalsIgnoreCase(Strings.CMD_BV_HELP))
            {
                return null;
            }
        }

        return new ArrayList<>();
    }

    /**
     * Gets player based on condition.
     * @param condition If true tries to convert sender into player, if false tries to get player from server.
     * @param sender Command sender.
     * @param playerName Name of player on server.
     * @return Player object if successful or null.
     */
    private Player getPlayer(final boolean condition, @NotNull final CommandSender sender, @NotNull final String playerName)
    {
        if(condition)
        {
            if(!(sender instanceof Player))
            {
                sender.sendMessage("Commands that are not reload not available from console!");
                return null;
            }

            return (Player) sender;
        }
        else
        {
            Player player = Bukkit.getServer().getPlayer(playerName);
            if(player == null)
            {
                sender.sendMessage("Player offline!");
            }

            return player;
        }
    }
}
