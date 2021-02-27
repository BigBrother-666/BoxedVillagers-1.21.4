package io.gitlab.arkdirfe.boxedvillagers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BoxedVillagers extends JavaPlugin
{
    public static String TAG_BOXED_VILLAGER_ITEM = "BoxedVillagerItem";
    public static String TAG_IS_BOUND = "IsBound";
    public static String CONFIG_TIME_WORLD = "timeWorld";

    public FileConfiguration config = getConfig();

    @Override
    public void onEnable()
    {
        Util.plugin = this;

        BoxedVillagersCommandExecutor boxedvillagersCmd = new BoxedVillagersCommandExecutor(this);
        this.getCommand("boxedvillagers").setExecutor(boxedvillagersCmd);
        this.getCommand("boxedvillagers").setTabCompleter(boxedvillagersCmd);
        new InteractionListener(this);
        getLogger().info("Loaded!");

        config.addDefault(CONFIG_TIME_WORLD, "world");
        config.options().copyDefaults(true);
        saveConfig();

        if(getServer().getWorld(config.getString(CONFIG_TIME_WORLD)) == null)
        {
            getLogger().severe("No world with name " + CONFIG_TIME_WORLD + ", this WILL break!");
        }
    }

    @Override
    public void onDisable()
    {
        getLogger().info("Unloaded!");
    }
}
