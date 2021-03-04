package io.gitlab.arkdirfe.boxedvillagers;

import io.gitlab.arkdirfe.boxedvillagers.commands.BoxedVillagersCommandExecutor;
import io.gitlab.arkdirfe.boxedvillagers.commands.WitchdoctorCommandExecutor;
import io.gitlab.arkdirfe.boxedvillagers.data.CostData;
import io.gitlab.arkdirfe.boxedvillagers.data.HelpData;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiController;
import io.gitlab.arkdirfe.boxedvillagers.listeners.InteractionListener;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiManager;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import io.gitlab.arkdirfe.boxedvillagers.util.Util;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BoxedVillagers extends JavaPlugin
{
    public WitchdoctorGuiManager witchdoctorGuiManager;

    public Map<UUID, WitchdoctorGuiController> guiMap;
    public Map<String, HelpData> helpPages;
    public List<CostData> cureCosts;
    public List<CostData> slotExtensionCosts;
    public CostData purgeCost;
    public CostData scrollCost;
    public CostData extractCost;
    public CostData addCost;

    @Override
    public void onEnable()
    {
        guiMap = new HashMap<>();
        Util.plugin = this;

        reloadConfig();
        registerCommandsAndListeners();

        getLogger().info("Loaded!");
    }

    @Override
    public void onDisable()
    {
        witchdoctorGuiManager.cleanupOpenGuis();
        getLogger().info("Unloaded!");
    }

    @Override
    public void reloadConfig()
    {
        super.reloadConfig();
        saveDefaultConfig();

        Util.timeWorldName = getConfig().getString(Strings.CONFIG_TIME_WORLD);
        if(Util.timeWorldName == null)
        {
            getLogger().severe("Error loading time world from config!");
        }
        else
        {
            if(getServer().getWorld(Util.timeWorldName) == null)
            {
                getLogger().severe("No world with name " + Strings.CONFIG_TIME_WORLD + ", this WILL break!");
            }
        }

        initializeMaps();
    }

    private void registerCommandsAndListeners()
    {
        BoxedVillagersCommandExecutor boxedvillagersCmd = new BoxedVillagersCommandExecutor(this);
        getCommand("boxedvillagers").setExecutor(boxedvillagersCmd);
        getCommand("boxedvillagers").setTabCompleter(boxedvillagersCmd);

        witchdoctorGuiManager = new WitchdoctorGuiManager(this);
        WitchdoctorCommandExecutor witchdoctorCmd = new WitchdoctorCommandExecutor(this, witchdoctorGuiManager);
        getCommand("witchdoctor").setExecutor(witchdoctorCmd);
        getCommand("witchdoctor").setTabCompleter(witchdoctorCmd);

        new InteractionListener(this);
    }

    private void initializeMaps()
    {
        helpPages = new HashMap<>();
        cureCosts = new ArrayList<>();
        slotExtensionCosts = new ArrayList<>();
        purgeCost = new CostData();
        scrollCost = new CostData();
        extractCost = new CostData();
        addCost = new CostData();

        initHelpPages(Strings.CONFIG_HELP, helpPages);

        initSimpleCostMap(Strings.CONFIG_COST_PURGE, purgeCost);
        initSimpleCostMap(Strings.CONFIG_COST_SCROLL, scrollCost);
        initSimpleCostMap(Strings.CONFIG_COST_EXTRACT, extractCost);
        initSimpleCostMap(Strings.CONFIG_COST_ADD, addCost);

        initLayeredCostMap(Strings.CONFIG_COST_CURE, cureCosts, 7);
        initLayeredCostMap(Strings.CONFIG_COST_SLOT, slotExtensionCosts, 17);

        getLogger().info("Loaded costs for operations!");
    }

    private void initHelpPages(@NotNull String configSection, @NotNull Map<String, HelpData> helpPages)
    {
                for(String key : getConfig().getConfigurationSection(configSection).getKeys(false))
        {
            String title = "";
            String content = "";

            for(String innerKey : getConfig().getConfigurationSection(configSection + "." + key).getKeys(false))
            {
                if(innerKey.equalsIgnoreCase("title"))
                {
                    title = getConfig().getString(configSection + "." + key + "." + innerKey);
                }
                else if(innerKey.equalsIgnoreCase("content"))
                {
                    content = getConfig().getString(configSection + "." + key + "." + innerKey);
                }
            }

            helpPages.put(key, new HelpData(title, content));
        }

        getLogger().info("Loaded " + helpPages.size() + " help pages!");
    }

    private void initLayeredCostMap(@NotNull String configSection, @NotNull List<CostData> costs, int expected)
    {
        for(String key : getConfig().getConfigurationSection(configSection).getKeys(false))
        {
            CostData cost = new CostData();

            for(String innerKey : getConfig().getConfigurationSection(configSection + "." + key).getKeys(false))
            {
                if(innerKey.equalsIgnoreCase("free"))
                {
                    continue;
                }

                Material mat = Material.matchMaterial(innerKey);
                if(mat != null)
                {
                    cost.addResource(mat, getConfig().getInt(configSection + "." + key + "." + mat.toString()));
                    continue;
                }

                if(innerKey.equalsIgnoreCase("money"))
                {
                    cost.setMoney(getConfig().getInt(configSection + "." + key + "." + innerKey));
                }
                else if(innerKey.equalsIgnoreCase("crystals"))
                {
                    cost.setCrystals(getConfig().getInt(configSection + "." + key + "." + innerKey));
                }
                else
                {
                    getLogger().warning("Unknown material or unsupported currency " + innerKey + "! Ignoring.");
                }
            }

            costs.add(cost);
        }

        if(costs.size() != expected)
        {
            getLogger().severe("Unexpected number of cost entries for " + configSection + " (got " + costs.size() + ", expected " + expected + ")! This WILL break!");
        }
    }

    private void initSimpleCostMap(@NotNull String configSection, @NotNull CostData cost)
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
