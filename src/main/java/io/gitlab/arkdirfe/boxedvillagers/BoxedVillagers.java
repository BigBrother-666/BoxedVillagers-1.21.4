package io.gitlab.arkdirfe.boxedvillagers;

import io.gitlab.arkdirfe.boxedvillagers.commands.BoxedVillagersCommandExecutor;
import io.gitlab.arkdirfe.boxedvillagers.commands.WitchdoctorCommandExecutor;
import io.gitlab.arkdirfe.boxedvillagers.data.CostData;
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
    public List<CostData> cureCosts;
    public List<CostData> slotExtensionCosts;
    public CostData purgeCost;
    public CostData scrollCost;
    public CostData extractCost;
    public CostData addCost;

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
        cureCosts = new ArrayList<>();
        slotExtensionCosts = new ArrayList<>();
        purgeCost = new CostData();
        scrollCost = new CostData();
        extractCost = new CostData();
        addCost = new CostData();

        initSimpleCostMap(Strings.CONFIG_COST_PURGE, purgeCost);
        initSimpleCostMap(Strings.CONFIG_COST_SCROLL, scrollCost);
        initSimpleCostMap(Strings.CONFIG_COST_EXTRACT, extractCost);
        initSimpleCostMap(Strings.CONFIG_COST_ADD, addCost);

        initLayeredCostMap(Strings.CONFIG_COST_CURE, cureCosts, 1, 7);
        initLayeredCostMap(Strings.CONFIG_COST_SLOT, slotExtensionCosts, 11, 17);

        getLogger().info("Registered costs for operations!");
    }

    private void initLayeredCostMap(String configSection, List<CostData> costs, int from, int expected)
    {
        int i = from;

        for(String key : getConfig().getConfigurationSection(configSection).getKeys(false))
        {
            CostData cost = new CostData();

            for (String innerKey : getConfig().getConfigurationSection(configSection + "." + i).getKeys(false))
            {
                if(innerKey.equalsIgnoreCase("free"))
                {
                    continue;
                }

                Material mat = Material.matchMaterial(innerKey);
                if(mat != null)
                {
                    cost.addResource(mat, getConfig().getInt(configSection + "." + i + "." + mat.toString()));
                    continue;
                }

                if(innerKey.equalsIgnoreCase("money"))
                {
                    cost.setMoney(getConfig().getInt(configSection + "." + i + "." + innerKey));
                }
                else if(innerKey.equalsIgnoreCase("crystals"))
                {
                    cost.setCrystals(getConfig().getInt(configSection + "." + i + "." + innerKey));
                }
                else
                {
                    getLogger().warning("Unknown material or unsupported currency " + innerKey + "! Ignoring.");
                }
            }

            costs.add(cost);
            i++;
        }

        if(costs.size() != expected)
        {
            getLogger().severe("Unexpected number of cost entries for " + configSection + " (got "+ costs.size() + ", expected " + expected + ")! This WILL break!");
        }
    }

    private void initSimpleCostMap(String configSection, CostData cost)
    {
        for(String key : getConfig().getConfigurationSection(configSection).getKeys(false))
        {
            Material mat = Material.matchMaterial(key);
            if(mat != null)
            {
                cost.addResource(mat, getConfig().getInt(configSection + "." + mat.toString()));
            }
        }
    }
}
