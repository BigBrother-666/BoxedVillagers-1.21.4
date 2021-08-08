package io.gitlab.arkdirfe.boxedvillagers.commands;

import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.data.HelpData;
import io.gitlab.arkdirfe.boxedvillagers.data.VillagerData;
import io.gitlab.arkdirfe.boxedvillagers.util.ItemUtil;
import io.gitlab.arkdirfe.boxedvillagers.util.StringFormatter;
import io.gitlab.arkdirfe.boxedvillagers.util.StringRef;
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
     * Handles the /boxedvillagers or /bv command
     *
     * @param plugin      Reference to the plugin.
     * @param commandName Name of the command.
     */
    public BoxedVillagersCommandExecutor(@NotNull final BoxedVillagers plugin, @NotNull final String commandName)
    {
        this.plugin = plugin;
        this.helpWidth = plugin.getConfig().getInt(Strings.get(StringRef.CONFIG_HELP_WIDTH));
        PluginCommand cmd = plugin.getCommand(commandName);
        if(cmd != null)
        {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
        else
        {
            plugin.getLogger().severe(Strings.get(StringRef.LOG_CANT_REGISTER_COMMAND_BOXEDVILLAGERS));
        }
    }
    
    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args)
    {
        if(args.length > 0)
        {
            String subCmd = args[0];
            
            // Admin Commands
            
            if(sender.hasPermission(Strings.get(StringRef.PERM_ADMIN)))
            {
                if(subCmd.equalsIgnoreCase(Strings.get(StringRef.CMD_BV_GIVE)))
                {
                    if(args.length > 1)
                    {
                        String itemName = args[1];
                        if(itemName.equalsIgnoreCase(Strings.get(StringRef.CMD_BV_GIVE_UNBOUND_SCROLL)))
                        {
                            Player player = getPlayer(args, sender, args.length == 2, args.length == 3);
                            if(player != null)
                            {
                                player.getInventory().addItem(ItemUtil.getUnboundScroll(false));
                            }
                        }
                        else if(itemName.equalsIgnoreCase(Strings.get(StringRef.CMD_BV_GIVE_UNBOUND_SCROLL_NONLETHAL)))
                        {
                            Player player = getPlayer(args, sender, args.length == 2, args.length == 3);
                            if(player != null)
                            {
                                player.getInventory().addItem(ItemUtil.getUnboundScroll(true));
                            }
                        }
                        else if(itemName.equalsIgnoreCase(Strings.get(StringRef.CMD_BV_GIVE_TRADE)))
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
                                    sender.sendMessage(StringFormatter.splitAndFormatLines(Strings.get(StringRef.CHAT_GIVE_TRADE_USAGE)).toArray(new String[0]));
                                }
                            }
                            else
                            {
                                sender.sendMessage(StringFormatter.splitAndFormatLines(Strings.get(StringRef.CHAT_GIVE_TRADE_USAGE)).toArray(new String[0]));
                            }
                        }
                    }
                    
                    return true;
                }
                else if(subCmd.equalsIgnoreCase(Strings.get(StringRef.CMD_BV_RELOAD)))
                {
                    plugin.reloadConfig();
                    return true;
                }
            }
            
            // Player Commands
            
            if(subCmd.equalsIgnoreCase(Strings.get(StringRef.CMD_BV_HELP)))
            {
                if(args.length == 1 && BoxedVillagers.getHelpPages().containsKey("default"))
                {
                    sender.sendMessage(BoxedVillagers.getHelpPages().get("default").getFormatted(helpWidth));
                }
                else if(args.length == 2)
                {
                    HelpData help = BoxedVillagers.getHelpPages().get(args[1]);
                    if(help != null)
                    {
                        sender.sendMessage(help.getFormatted(helpWidth));
                    }
                    else
                    {
                        sender.sendMessage(StringFormatter.formatLine(Strings.get(StringRef.CHAT_NO_HELP_PAGE)));
                    }
                }
            }
            else if(subCmd.equalsIgnoreCase(Strings.get(StringRef.CMD_BV_RENAME)))
            {
                if(args.length > 1 && sender instanceof Player)
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
                    else
                    {
                        player.sendMessage(StringFormatter.formatLine(Strings.get(StringRef.CHAT_NOT_HOLDING_SCROLL)));
                    }
                }
            }
            else
            {
                sender.sendMessage(StringFormatter.formatLine(Strings.get(StringRef.CHAT_UNKNOWN_SUB_COMMAND)));
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
            if(sender.hasPermission(Strings.get(StringRef.PERM_ADMIN)))
            {
                if((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase(Strings.get(StringRef.CMD_BV_GIVE)))
                {
                    return Arrays.asList(Strings.get(StringRef.CMD_BV_GIVE_UNBOUND_SCROLL), Strings.get(StringRef.CMD_BV_GIVE_UNBOUND_SCROLL_NONLETHAL), Strings.get(StringRef.CMD_BV_GIVE_TRADE));
                }
                
                if((args.length == 0 || args.length == 1))
                {
                    return Arrays.asList(Strings.get(StringRef.CMD_BV_GIVE), Strings.get(StringRef.CMD_BV_HELP), Strings.get(StringRef.CMD_BV_RELOAD), Strings.get(StringRef.CMD_BV_RENAME));
                }
                
                if(args[0].equalsIgnoreCase(Strings.get(StringRef.CMD_BV_GIVE)) && args[1].equalsIgnoreCase(Strings.get(StringRef.CMD_BV_GIVE_TRADE)))
                {
                    return new ArrayList<>();
                }
            }
            
            // Player Commands
            
            if((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase(Strings.get(StringRef.CMD_BV_HELP)))
            {
                return new ArrayList<>(BoxedVillagers.getHelpPages().keySet());
            }
            
            if((args.length == 0 || args.length == 1))
            {
                return Arrays.asList(Strings.get(StringRef.CMD_BV_HELP), Strings.get(StringRef.CMD_BV_RENAME));
            }
            
            if(args.length == 3 && !args[0].equalsIgnoreCase(Strings.get(StringRef.CMD_BV_HELP)))
            {
                return null;
            }
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Returns a player object depending on conditions.
     *
     * @param args         Command arguments.
     * @param sender       Command sender.
     * @param senderPlayer Condition for when to interpret the sender as a player.
     * @param argPlayer    Condition for when to get an online player from the args.
     *
     * @return Player object if possible, null otherwise.
     */
    @Nullable
    private Player getPlayer(@NotNull final String[] args, @NotNull final CommandSender sender, final boolean senderPlayer, final boolean argPlayer)
    {
        if(senderPlayer)
        {
            if(!(sender instanceof Player))
            {
                sender.sendMessage(Strings.get(StringRef.LOG_UNAVAILABLE_FROM_CONSOLE));
                return null;
            }
            
            return (Player) sender;
        }
        if(argPlayer)
        {
            Player player = Bukkit.getServer().getPlayer(args[args.length - 1]);
            if(player == null)
            {
                sender.sendMessage(StringFormatter.formatLine(Strings.get(StringRef.CHAT_PLAYER_OFFLINE)));
            }
            
            return player;
        }
        
        return null;
    }
}
