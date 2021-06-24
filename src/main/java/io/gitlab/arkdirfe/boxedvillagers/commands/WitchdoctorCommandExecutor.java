package io.gitlab.arkdirfe.boxedvillagers.commands;

import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiManager;
import io.gitlab.arkdirfe.boxedvillagers.util.StringFormatter;
import io.gitlab.arkdirfe.boxedvillagers.util.StringRef;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
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
            plugin.getLogger().severe(Strings.get(StringRef.LOG_CANT_REGISTER_COMMAND_WITCHDOCTOR));
        }
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, final String[] args)
    {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            if(args.length > 0 && args[0].equalsIgnoreCase("admin") && sender.hasPermission(Strings.get(StringRef.PERM_ADMIN)))
            {
                gui.openGui(player, true);
            }
            else if(sender.hasPermission(Strings.get(StringRef.PERM_WITCHDOCTOR)))
            {
                gui.openGui(player, false);
            }
            else
            {
                sender.sendMessage(StringFormatter.formatLine(Strings.get(StringRef.CHAT_INSUFFICIENT_PERMISSION)));
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args)
    {
        if(args.length <= 1 && sender.hasPermission(Strings.get(StringRef.PERM_ADMIN)))
        {
            return Collections.singletonList("admin");
        }

        return new ArrayList<>();
    }
}
