package io.gitlab.arkdirfe.boxedvillagers;

import io.gitlab.arkdirfe.boxedvillagers.commands.BoxedVillagersCommandExecutor;
import io.gitlab.arkdirfe.boxedvillagers.commands.WitchdoctorCommandExecutor;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiController;
import io.gitlab.arkdirfe.boxedvillagers.listeners.InteractionListener;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiManager;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import io.gitlab.arkdirfe.boxedvillagers.util.Util;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoxedVillagers extends JavaPlugin
{
    public FileConfiguration config = getConfig();
    public WitchdoctorGuiManager witchdoctorGuiManager;

    public Map<UUID, WitchdoctorGuiController> guiMap;
    public Map<Material, Integer> cureTier1CostMap;
    public Map<Material, Integer> cureTier2CostMap;
    public Map<Material, Integer> cureTier3CostMap;
    public Map<Material, Integer> purgeCostMap;

    @Override
    public void onEnable()
    {
        Util.plugin = this;
        saveDefaultConfig();
        initializeMaps();

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
        witchdoctorGuiManager.cleanupOpenGuis();
        getLogger().info("Unloaded!");
    }

    private void initializeMaps()
    {
        guiMap = new HashMap<>();
        cureTier1CostMap = new HashMap<>();
        cureTier2CostMap = new HashMap<>();
        cureTier3CostMap = new HashMap<>();
        purgeCostMap = new HashMap<>();

        initCostMap(Strings.CONFIG_COST_CURE1, cureTier1CostMap);
        initCostMap(Strings.CONFIG_COST_CURE2, cureTier2CostMap);
        initCostMap(Strings.CONFIG_COST_CURE3, cureTier3CostMap);
        initCostMap(Strings.CONFIG_COST_PURGE, purgeCostMap);

        getLogger().info("Registered costs for operations!");
    }

    private void initCostMap(String configSection, Map<Material, Integer> costMap)
    {
        if(!costMap.isEmpty())
        {
            costMap.clear();
        }

        for(String key : getConfig().getConfigurationSection(configSection).getKeys(false))
        {
            Material mat = Material.matchMaterial(key);
            if(mat != null)
            {
                costMap.put(mat, getConfig().getInt(configSection + "." + mat.toString()));
            }
        }
    }
}
