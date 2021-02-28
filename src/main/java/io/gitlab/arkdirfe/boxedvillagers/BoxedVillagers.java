package io.gitlab.arkdirfe.boxedvillagers;

import io.gitlab.arkdirfe.boxedvillagers.commands.BoxedVillagersCommandExecutor;
import io.gitlab.arkdirfe.boxedvillagers.commands.WitchdoctorCommandExecutor;
import io.gitlab.arkdirfe.boxedvillagers.listeners.InteractionListener;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiManager;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import io.gitlab.arkdirfe.boxedvillagers.util.Util;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BoxedVillagers extends JavaPlugin
{
    public FileConfiguration config = getConfig();
    private WitchdoctorGuiManager witchdoctorGuiManager;

    @Override
    public void onEnable()
    {
        Util.plugin = this;
        saveDefaultConfig();

        BoxedVillagersCommandExecutor boxedvillagersCmd = new BoxedVillagersCommandExecutor(this);
        this.getCommand("boxedvillagers").setExecutor(boxedvillagersCmd);
        this.getCommand("boxedvillagers").setTabCompleter(boxedvillagersCmd);

        witchdoctorGuiManager = new WitchdoctorGuiManager(this);
        WitchdoctorCommandExecutor witchdoctorCmd = new WitchdoctorCommandExecutor(this, witchdoctorGuiManager);
        this.getCommand("witchdoctor").setExecutor(witchdoctorCmd);
        this.getCommand("witchdoctor").setTabCompleter(witchdoctorCmd);

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
