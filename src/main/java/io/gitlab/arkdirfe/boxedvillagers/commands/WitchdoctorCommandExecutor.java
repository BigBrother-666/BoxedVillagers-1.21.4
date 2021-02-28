package io.gitlab.arkdirfe.boxedvillagers.commands;

import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

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
                gui.openGui(((Player) sender).getPlayer());
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        return null;
    }
}
