package io.gitlab.arkdirfe.boxedvillagers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BoxedVillagers extends JavaPlugin
{
    public FileConfiguration config = getConfig();

    @Override
    public void onEnable()
    {
        Util.plugin = this;
        saveDefaultConfig();

        BoxedVillagersCommandExecutor boxedvillagersCmd = new BoxedVillagersCommandExecutor(this);
        this.getCommand("boxedvillagers").setExecutor(boxedvillagersCmd);
        this.getCommand("boxedvillagers").setTabCompleter(boxedvillagersCmd);
        new InteractionListener(this);
        getLogger().info("Loaded!");

        if(getServer().getWorld(config.getString(Strings.CONFIG_TIME_WORLD)) == null)
        {
            getLogger().severe("No world with name " + Strings.CONFIG_TIME_WORLD + ", this WILL break!");
        }
    }

    @Override
    public void onDisable()
    {
        getLogger().info("Unloaded!");
    }
}
