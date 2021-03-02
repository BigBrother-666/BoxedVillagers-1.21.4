package io.gitlab.arkdirfe.boxedvillagers.commands;

import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiManager;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class WitchdoctorCommandExecutor implements TabExecutor
{
    private final BoxedVillagers plugin;
    private final WitchdoctorGuiManager gui;

    public WitchdoctorCommandExecutor(BoxedVillagers plugin, WitchdoctorGuiManager gui)
    {
        this.plugin = plugin;
        this.gui = gui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args)
    {
        if(command.getName().equalsIgnoreCase("witchdoctor"))
        {
            if(sender instanceof Player)
            {
                if(args.length > 0 && args[0].equalsIgnoreCase("admin") && sender.hasPermission(Strings.PERM_ADMIN))
                {
                    gui.openGui(((Player) sender).getPlayer(), true);
                    return true;
                }
                if(sender.hasPermission(Strings.PERM_WITCHDOCTOR))
                {
                    gui.openGui(((Player) sender).getPlayer(), false);
                    return true;
                }
                else
                {
                    sender.sendMessage("§cInsufficient Permission!");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        if(args.length <= 1 && sender.hasPermission(Strings.PERM_ADMIN))
        {
            return Arrays.asList("admin");
        }

        return new ArrayList<String>();
    }
}
