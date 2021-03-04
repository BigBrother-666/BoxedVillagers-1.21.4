package io.gitlab.arkdirfe.boxedvillagers.commands;

import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiManager;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WitchdoctorCommandExecutor implements TabExecutor
{
    private final BoxedVillagers plugin;
    private final WitchdoctorGuiManager gui;

    public WitchdoctorCommandExecutor(final BoxedVillagers plugin, final WitchdoctorGuiManager gui)
    {
        this.plugin = plugin;
        this.gui = gui;
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, final String[] args)
    {
        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            if(args.length > 0 && args[0].equalsIgnoreCase("admin") && sender.hasPermission(Strings.PERM_ADMIN))
            {
                gui.openGui(player, true);
            }
            else if(sender.hasPermission(Strings.PERM_WITCHDOCTOR))
            {
                gui.openGui(player, false);
            }
            else
            {
                sender.sendMessage("§cInsufficient Permission!");
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args)
    {
        if(args.length <= 1 && sender.hasPermission(Strings.PERM_ADMIN))
        {
            return Collections.singletonList("admin");
        }

        return new ArrayList<>();
    }
}
