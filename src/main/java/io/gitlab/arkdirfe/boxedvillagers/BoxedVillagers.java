package io.gitlab.arkdirfe.boxedvillagers;

import io.gitlab.arkdirfe.boxedvillagers.commands.BoxedVillagersCommandExecutor;
import io.gitlab.arkdirfe.boxedvillagers.commands.WitchdoctorCommandExecutor;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiController;
import io.gitlab.arkdirfe.boxedvillagers.listeners.InteractionListener;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiManager;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import io.gitlab.arkdirfe.boxedvillagers.util.Util;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class BoxedVillagers extends JavaPlugin
{
    public WitchdoctorGuiManager witchdoctorGuiManager;

    public Map<UUID, WitchdoctorGuiController> guiMap;
    public List<Map<Material, Integer>> cureCostMaps;
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

        if(getServer().getWorld(getConfig().getString(Strings.CONFIG_TIME_WORLD)) == null)
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
        cureCostMaps = new ArrayList<>();
        purgeCostMap = new HashMap<>();

        initSimpleCostMap(Strings.CONFIG_COST_PURGE, purgeCostMap);

        initLayeredCostMap(Strings.CONFIG_COST_CURE, cureCostMaps, 7);

        getLogger().info("Registered costs for operations!");
    }

    private void initLayeredCostMap(String configSection, List<Map<Material, Integer>> maps, int expectedCount)
    {
        int i = 1;

        for(String key : getConfig().getConfigurationSection(configSection).getKeys(false))
        {
            Map<Material, Integer> map = new HashMap<>();

            for (String innerKey : getConfig().getConfigurationSection(configSection + "." + i).getKeys(false))
            {
                Material mat = Material.matchMaterial(innerKey);
                if(mat != null)
                {
                    map.put(mat, getConfig().getInt(configSection + "." + i + "." + mat.toString()));
                }
            }

            maps.add(map);
            i++;
        }

        if(maps.size() != expectedCount)
        {
            getLogger().severe("Incorrect number of cure cost entries in config (expected " + expectedCount + ")! This WILL break!");
        }
    }

    private void initSimpleCostMap(String configSection, Map<Material, Integer> costMap)
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
