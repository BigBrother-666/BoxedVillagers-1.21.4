package io.gitlab.arkdirfe.boxedvillagers;

import io.gitlab.arkdirfe.boxedvillagers.commands.BoxedVillagersCommandExecutor;
import io.gitlab.arkdirfe.boxedvillagers.commands.WitchdoctorCommandExecutor;
import io.gitlab.arkdirfe.boxedvillagers.data.CostData;
import io.gitlab.arkdirfe.boxedvillagers.data.HelpData;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiController;
import io.gitlab.arkdirfe.boxedvillagers.listeners.InteractionListener;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiManager;
import io.gitlab.arkdirfe.boxedvillagers.util.ConfigAccessor;
import io.gitlab.arkdirfe.boxedvillagers.util.StringFormatter;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import io.gitlab.arkdirfe.boxedvillagers.util.Util;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    private ConfigAccessor stringsConfig;

    @Override
    public void onEnable()
    {
        stringsConfig = new ConfigAccessor(this, "strings.yml");

        guiMap = new HashMap<>();
        Util.plugin = this;

        reloadConfig();
        registerCommandsAndListeners();

        getLogger().info(Strings.get("LOG_LOADED"));
    }

    @Override
    public void onDisable()
    {
        witchdoctorGuiManager.cleanupOpenGuis();
        getLogger().info(Strings.get("LOG_UNLOADED"));
    }

    @Override
    public void reloadConfig()
    {
        reloadColorsAndStrings();
        saveDefaultConfig();
        super.reloadConfig();

        String timeWorldName = getConfig().getString(Strings.get("CONFIG_TIME_WORLD"));
        if(timeWorldName == null)
        {
            getLogger().severe(Strings.get("LOG_ERROR_TIME_WORLD"));
        }
        else
        {
            if(getServer().getWorld(timeWorldName) == null)
            {
                getLogger().severe(String.format(Strings.get("LOG_DYN_NO_WORLD"), timeWorldName));
            }
        }

        initializeMaps();
    }

    private void reloadColorsAndStrings()
    {
        stringsConfig.saveDefaultConfig();
        stringsConfig.reloadConfig();
        Strings.restoreDefaults(); // Call before it is ever accessed, should guarantee that the strings are there
        StringFormatter.restoreDefaultColors();
        loadColors();
        loadStrings();
    }

    private void loadColors()
    {
        String configSection = Strings.get("CONFIG_STRINGS_COLORS");
        ConfigurationSection section = stringsConfig.getConfig().getConfigurationSection(configSection);
        if(section == null)
        {
            getLogger().info(String.format(Strings.get("LOG_DYN_MISSING_CONFIG_SECTION_OVERRIDES"), configSection));
            return;
        }

        int count = 0;

        for(String key : section.getKeys(false))
        {
            String value = stringsConfig.getConfig().getString(configSection + "." + key);
            if(value != null)
            {
                if(!StringFormatter.setColor(key, value))
                {
                    getLogger().warning(Strings.get("LOG_INVALID_STRING_OVERRIDE"));
                }
                else
                {
                    count++;
                }
            }
        }

        getLogger().info(String.format(Strings.get("LOG_DYN_LOAD_COLOR_OVERRIDES"), count));
    }

    private void loadStrings()
    {
        String configSection = Strings.get("CONFIG_STRINGS_STRING_ENTRIES");
        ConfigurationSection section = stringsConfig.getConfig().getConfigurationSection(configSection);
        if(section == null)
        {
            getLogger().info(String.format(Strings.get("LOG_DYN_MISSING_CONFIG_SECTION_OVERRIDES"), configSection));
            return;
        }

        int count = 0;

        for(String key : section.getKeys(false))
        {
            String value = stringsConfig.getConfig().getString(configSection + "." + key);
            if(value != null)
            {
                if(!Strings.set(key, value))
                {
                    getLogger().warning(Strings.get("LOG_INVALID_STRING_OVERRIDE"));
                }
                else
                {
                    count++;
                }
            }
        }

        getLogger().info(String.format(Strings.get("LOG_DYN_LOAD_STRING_OVERRIDES"), count));
    }

    /**
     * Registers commands and listeners and saves a reference to the witchdoctorGuiManager, which is important for cleaning up if the server closes while someone has a GUI open.
     */
    private void registerCommandsAndListeners()
    {
        witchdoctorGuiManager = new WitchdoctorGuiManager(this);

        new BoxedVillagersCommandExecutor(this, "boxedvillagers");
        new WitchdoctorCommandExecutor(this, witchdoctorGuiManager, "witchdoctor");
        new InteractionListener(this);

        getLogger().info(Strings.get("LOG_REGISTER_COMMANDS"));
    }

    /**
     * Initializes various maps needed for the operation of the plugin.
     */
    private void initializeMaps()
    {
        helpPages = new HashMap<>();
        cureCosts = new ArrayList<>();
        slotExtensionCosts = new ArrayList<>();
        purgeCost = new CostData();
        scrollCost = new CostData();
        extractCost = new CostData();
        addCost = new CostData();

        initHelpPages();

        initSimpleCostMap(Strings.get("CONFIG_COST_PURGE"), purgeCost);
        initSimpleCostMap(Strings.get("CONFIG_COST_SCROLL"), scrollCost);
        initSimpleCostMap(Strings.get("CONFIG_COST_EXTRACT"), extractCost);
        initSimpleCostMap(Strings.get("CONFIG_COST_ADD"), addCost);

        initLayeredCostMap(Strings.get("CONFIG_COST_CURE"), cureCosts, 7);
        initLayeredCostMap(Strings.get("CONFIG_COST_SLOT"), slotExtensionCosts, 17);

        getLogger().info(Strings.get("LOG_LOAD_COSTS"));
    }

    /**
     * Reads help pages from config.
     */
    private void initHelpPages()
    {
        ConfigurationSection section = getConfig().getConfigurationSection(Strings.get("CONFIG_HELP"));
        if(section == null)
        {
            getLogger().severe(String.format(Strings.get("LOG_DYN_MISSING_CONFIG_SECTION"), Strings.get("CONFIG_HELP")));
            return;
        }

        for(String key : section.getKeys(false))
        {
            String title = getConfig().getString(Strings.get("CONFIG_HELP") + "." + key + ".title");
            String content = getConfig().getString(Strings.get("CONFIG_HELP") + "." + key + ".content");

            if(title == null)
            {
                getLogger().warning(String.format(Strings.get("LOG_DYN_NO_TITLE"), key));
                title = "";
            }
            if(content == null)
            {
                getLogger().warning(String.format(Strings.get("LOG_DYN_NO_CONTENT"), key));
                content = "";
            }

            helpPages.put(key, new HelpData(title, content));
        }

        getLogger().info(String.format(Strings.get("LOG_DYN_LOAD_HELP"), helpPages.size()));
    }

    /**
     * Loads a list of CostData from config, validates if it retrieved the correct amount.
     *
     * @param configSection Config section the list is located in.
     * @param costs         Reference to the list the costs are stored in.
     * @param expected      Expected number of entries.
     */
    private void initLayeredCostMap(@NotNull String configSection, @NotNull List<CostData> costs, int expected)
    {
        ConfigurationSection section = getConfig().getConfigurationSection(configSection);
        if(section == null)
        {
            getLogger().severe(String.format(Strings.get("LOG_DYN_MISSING_CONFIG_SECTION"), configSection));
            return;
        }

        for(String key : section.getKeys(false))
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
                }
                else if(innerKey.equalsIgnoreCase("money"))
                {
                    cost.setMoney(getConfig().getInt(configSection + "." + key + "." + innerKey));
                }
                else if(innerKey.equalsIgnoreCase("crystals"))
                {
                    cost.setCrystals(getConfig().getInt(configSection + "." + key + "." + innerKey));
                }
                else
                {
                    getLogger().warning(String.format(Strings.get("LOG_DYN_UNKNOWN_MATERIAL"), innerKey));
                }
            }

            costs.add(cost);
        }

        if(costs.size() != expected)
        {
            getLogger().severe(String.format(Strings.get("LOG_DYN_UNEXPECTED_NUMBER"), configSection, costs.size(), expected));
        }
    }

    /**
     * Loads a single CostData from config.
     *
     * @param configSection Config section the cost is located in.
     * @param cost          Reference to the CostData the cost is stored in.
     */
    private void initSimpleCostMap(@NotNull String configSection, @NotNull CostData cost)
    {
        ConfigurationSection section = getConfig().getConfigurationSection(configSection);
        if(section == null)
        {
            getLogger().severe(String.format(Strings.get("LOG_DYN_MISSING_CONFIG_SECTION"), configSection));
            return;
        }

        for(String key : section.getKeys(false))
        {
            Material mat = Material.matchMaterial(key);
            if(mat != null)
            {
                cost.addResource(mat, getConfig().getInt(configSection + "." + mat.toString()));
            }
        }
    }
}
