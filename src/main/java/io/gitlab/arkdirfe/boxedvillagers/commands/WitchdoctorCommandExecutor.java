package io.gitlab.arkdirfe.boxedvillagers.commands;

import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiManager;
import io.gitlab.arkdirfe.boxedvillagers.util.StringFormatter;
import io.gitlab.arkdirfe.boxedvillagers.util.StringRef;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WitchdoctorCommandExecutor implements TabExecutor
{
    private final WitchdoctorGuiManager gui;
    
    /**
     * Handles the /witchdoctor or /wd command.
     *
     * @param plugin      Reference to the plugin.
     * @param manager     The GUI manager class.
     * @param commandName Name of the command.
     */
    public WitchdoctorCommandExecutor(@NotNull final BoxedVillagers plugin, @NotNull final WitchdoctorGuiManager manager, @NotNull String commandName)
    {
        this.gui = manager;
        PluginCommand cmd = plugin.getCommand(commandName);
        if(cmd != null)
        {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
        else
        {
            plugin.getLogger().severe(Strings.LOG_CANT_REGISTER_COMMAND_WITCHDOCTOR);
        }
    }
    
    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, final String[] args)
    {
        if(sender instanceof Player player)
        {
            if(args.length == 1 && args[0].equalsIgnoreCase("admin") && sender.hasPermission(Strings.PERM_WITCHDOCTOR_ADMIN))
            {
                gui.openGui(player, true);
            }
            else if(args.length == 2 && args[0].equalsIgnoreCase(Strings.CMD_WD_OPEN))
            {
                openCommand(sender, args[1]);
            }
            else
            {
                gui.openGui(player, false);
            }
            
            return true;
        }
        else if(args.length == 2 && args[0].equalsIgnoreCase(Strings.CMD_WD_OPEN))
        {
            openCommand(sender, args[1]);
            return true;
        }
        
        return false;
    }
    
    /**
     * Helper method for the open subcommand.
     *
     * @param sender Who sent the command.
     * @param target Target who should receive the UI.
     */
    private void openCommand(CommandSender sender, String target)
    {
        Player player = Bukkit.getServer().getPlayer(target);
        
        if(player == null)
        {
            sender.sendMessage(StringFormatter.formatLine(Strings.get(StringRef.CHAT_PLAYER_OFFLINE)));
            return;
        }
        
        gui.openGui(player, false);
    }
    
    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args)
    {
        if(args.length == 2 && args[0].equalsIgnoreCase(Strings.CMD_WD_OPEN) && sender.hasPermission(Strings.PERM_WITCHDOCTOR_OPEN))
        {
            return null;
        }
        
        ArrayList<String> suggestions = new ArrayList<>();
        
        if(args.length <= 1 && sender.hasPermission(Strings.PERM_WITCHDOCTOR_ADMIN))
        {
            suggestions.add(Strings.CMD_WD_ADMIN);
        }
        if(args.length <= 1 && sender.hasPermission(Strings.PERM_WITCHDOCTOR_OPEN))
        {
            suggestions.add(Strings.CMD_WD_OPEN);
        }
        
        return suggestions;
    }
}
