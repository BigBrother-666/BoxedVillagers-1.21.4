package io.gitlab.arkdirfe.boxedvillagers;

import io.gitlab.arkdirfe.boxedvillagers.commands.BoxedVillagersCommandExecutor;
import io.gitlab.arkdirfe.boxedvillagers.commands.WitchdoctorCommandExecutor;
import io.gitlab.arkdirfe.boxedvillagers.data.CostData;
import io.gitlab.arkdirfe.boxedvillagers.data.HelpData;
import io.gitlab.arkdirfe.boxedvillagers.listeners.InteractionListener;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiController;
import io.gitlab.arkdirfe.boxedvillagers.ui.WitchdoctorGuiManager;
import io.gitlab.arkdirfe.boxedvillagers.util.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class BoxedVillagers extends JavaPlugin
{
    private WitchdoctorGuiManager witchdoctorGuiManager;
    
    private static Map<UUID, WitchdoctorGuiController> guiMap;
    private static Map<String, HelpData> helpPages;
    private static List<CostData> cureCosts;
    private static List<CostData> slotExtensionCosts;
    private static CostData purgeCost;
    private static CostData scrollCost;
    private static CostData extractCost;
    private static CostData addCost;
    private static Economy economy;
    
    // Config Values
    private static String timeWorldName;
    private static String fallbackCurrencySymbol;
    private static int minTradeSlots;
    private static int maxTradeSlots;
    private static int maxCures;
    
    private static int minXPReward;
    private static int maxXPReward;
    
    private static ConfigAccessor stringsConfig;
    
    private final List<String> loggedMessages = new ArrayList<>();
    private boolean loggingActive;
    
    @Override
    public void onEnable()
    {
        stringsConfig = new ConfigAccessor(this, "strings.yml");
        
        guiMap = new HashMap<>();
        Util.plugin = this;
        
        reloadConfig();
        registerCommandsAndListeners();
        initializeVault();
        
        getLogger().info(Strings.LOG_LOADED);
    }
    
    @Override
    public void onDisable()
    {
        witchdoctorGuiManager.cleanupOpenGuis();
        getLogger().info(Strings.LOG_UNLOADED);
    }
    
    @Override
    public void reloadConfig()
    {
        reloadColorsAndStrings();
        saveDefaultConfig();
        super.reloadConfig();
        
        timeWorldName = getConfig().getString(Strings.CONFIG_TIME_WORLD);
        
        if(timeWorldName == null)
        {
            LogMessage(Strings.LOG_ERROR_TIME_WORLD, Level.SEVERE);
        }
        else
        {
            if(getServer().getWorld(timeWorldName) == null)
            {
                LogMessage(String.format(Strings.LOG_DYN_NO_WORLD, timeWorldName), Level.SEVERE);
            }
        }
        
        fallbackCurrencySymbol = getConfig().getString(Strings.CONFIG_CURRENCY_FALLBACK);
        
        try
        {
            maxTradeSlots = Math.max(0, Math.min(27, Integer.parseInt(Objects.requireNonNull(getConfig().getString(Strings.CONFIG_MAX_SLOTS)))));
            minTradeSlots = Math.max(0, Math.min(maxTradeSlots, Integer.parseInt(Objects.requireNonNull(getConfig().getString(Strings.CONFIG_MIN_SLOTS)))));
            maxCures = Math.max(0, Math.min(7, Integer.parseInt(Objects.requireNonNull(getConfig().getString(Strings.CONFIG_MAX_CURES)))));
            minXPReward = Math.abs(Integer.parseInt(Objects.requireNonNull(getConfig().getString(Strings.CONFIG_MIN_XP))));
            maxXPReward = Math.max(minXPReward, Math.abs(Integer.parseInt(Objects.requireNonNull(getConfig().getString(Strings.CONFIG_MAX_XP)))));
        }
        catch(Exception e)
        {
            minTradeSlots = 0;
            maxTradeSlots = 27;
            maxCures = 7;
            minXPReward = 3;
            maxXPReward = 6;
            LogMessage(Strings.LOG_CONFIG_ERROR_GENERIC, Level.SEVERE);
        }
        
        initializeMaps();
    }
    
    private void reloadColorsAndStrings()
    {
        stringsConfig.saveDefaultConfig();
        stringsConfig.reloadConfig();
        Strings.restoreMutable(); // Call before it is ever accessed, should guarantee that the strings are there
        StringFormatter.restoreDefaultColors();
        loadColors();
        loadStrings();
    }
    
    /**
     * Loads color overrides from config.
     */
    private void loadColors()
    {
        String configSection = Strings.CONFIG_STRINGS_COLORS;
        ConfigurationSection section = stringsConfig.getConfig().getConfigurationSection(configSection);
        if(section == null)
        {
            LogMessage(String.format(Strings.LOG_DYN_MISSING_CONFIG_SECTION_OVERRIDES, configSection), Level.INFO);
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
                    LogMessage(Strings.LOG_INVALID_STRING_OVERRIDE, Level.SEVERE);
                }
                else
                {
                    count++;
                }
            }
        }
        
        LogMessage(String.format(Strings.LOG_DYN_LOAD_COLOR_OVERRIDES, count), Level.INFO);
    }
    
    /**
     * Loads string overrides from config.
     */
    private void loadStrings()
    {
        String configSection = Strings.CONFIG_STRINGS_STRING_ENTRIES;
        ConfigurationSection section = stringsConfig.getConfig().getConfigurationSection(configSection);
        if(section == null)
        {
            LogMessage(String.format(Strings.LOG_DYN_MISSING_CONFIG_SECTION_OVERRIDES, configSection), Level.INFO);
            return;
        }
        
        int count = 0;
        
        for(String key : section.getKeys(false))
        {
            String value = stringsConfig.getConfig().getString(configSection + "." + key);
            StringRef keyRef;
            try
            {
                keyRef = StringRef.valueOf(key);
            }
            catch(IllegalArgumentException e)
            {
                LogMessage(Strings.LOG_INVALID_STRING_OVERRIDE, Level.WARNING);
                continue;
            }
            
            if(value != null)
            {
                if(!Strings.set(keyRef, value))
                {
                    LogMessage(Strings.LOG_INVALID_STRING_OVERRIDE, Level.WARNING);
                    ;
                }
                else
                {
                    count++;
                }
            }
        }
        
        LogMessage(String.format(Strings.LOG_DYN_LOAD_STRING_OVERRIDES, count), Level.INFO);
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
        
        getLogger().info(Strings.LOG_REGISTER_COMMANDS);
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
        
        initSimpleCostMap(Strings.CONFIG_COST_PURGE, purgeCost);
        initSimpleCostMap(Strings.CONFIG_COST_SCROLL, scrollCost);
        initSimpleCostMap(Strings.CONFIG_COST_EXTRACT, extractCost);
        initSimpleCostMap(Strings.CONFIG_COST_ADD, addCost);
        
        initLayeredCostMap(Strings.CONFIG_COST_CURE, cureCosts, 7);
        initLayeredCostMap(Strings.CONFIG_COST_SLOT, slotExtensionCosts, 27);
        
        LogMessage(Strings.LOG_LOAD_COSTS, Level.INFO);
    }
    
    /**
     *
     */
    private void initializeVault()
    {
        if(!setupEconomy())
        {
            getLogger().warning(Strings.LOG_ECONOMY_SETUP_FAIL);
        }
        else
        {
            getLogger().info(Strings.LOG_ECONOMY_SETUP_SUCCESS);
        }
    }
    
    /**
     * Reads help pages from config.
     */
    private void initHelpPages()
    {
        ConfigurationSection section = getConfig().getConfigurationSection(Strings.CONFIG_HELP);
        if(section == null)
        {
            LogMessage(String.format(Strings.LOG_DYN_MISSING_CONFIG_SECTION, Strings.CONFIG_HELP), Level.SEVERE);
            return;
        }
        
        for(String key : section.getKeys(false))
        {
            String title = getConfig().getString(Strings.CONFIG_HELP + "." + key + ".title");
            String content = getConfig().getString(Strings.CONFIG_HELP + "." + key + ".content");
            
            if(title == null)
            {
                LogMessage(String.format(Strings.LOG_DYN_NO_TITLE, key), Level.WARNING);
                title = "";
            }
            if(content == null)
            {
                LogMessage(String.format(Strings.LOG_DYN_NO_CONTENT, key), Level.WARNING);
                content = "";
            }
            
            helpPages.put(key, new HelpData(title, content));
        }
        
        LogMessage(String.format(Strings.LOG_DYN_LOAD_HELP, helpPages.size()), Level.INFO);
    }
    
    /**
     * Uses Vault to initialize an economy.
     *
     * @return success
     */
    private boolean setupEconomy()
    {
        if(getServer().getPluginManager().getPlugin("Vault") == null)
        {
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp == null)
        {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
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
            LogMessage(String.format(Strings.LOG_DYN_MISSING_CONFIG_SECTION, configSection), Level.SEVERE);
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
                    cost.addResource(mat, getConfig().getInt(configSection + "." + key + "." + mat));
                }
                else if(innerKey.equalsIgnoreCase("money"))
                {
                    cost.setMoney(getConfig().getDouble(configSection + "." + key + "." + innerKey));
                }
                else
                {
                    LogMessage(String.format(Strings.LOG_DYN_UNKNOWN_MATERIAL, innerKey), Level.WARNING);
                }
            }
            
            costs.add(cost);
        }
        
        if(costs.size() != expected)
        {
            LogMessage(String.format(Strings.LOG_DYN_UNEXPECTED_NUMBER, configSection, costs.size(), expected), Level.SEVERE);
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
            LogMessage(String.format(Strings.LOG_DYN_MISSING_CONFIG_SECTION, configSection), Level.SEVERE);
            return;
        }
        
        for(String key : section.getKeys(false))
        {
            Material mat = Material.matchMaterial(key);
            if(mat != null)
            {
                cost.addResource(mat, getConfig().getInt(configSection + "." + mat));
            }
        }
    }
    
    private void LogMessage(String string, Level loggingLevel)
    {
        String prefix = "";
        if(loggingLevel == Level.INFO)
        {
            getLogger().info(string);
            prefix = "#adf3ffINFO: ";
        }
        else if(loggingLevel == Level.WARNING)
        {
            getLogger().warning(string);
            prefix = "#ffa13dWARNING: ";
        }
        else if(loggingLevel == Level.SEVERE)
        {
            getLogger().severe(string);
            prefix = "#ff0000SEVERE: ";
        }
        
        if(loggingActive)
        {
            loggedMessages.add(prefix + string);
        }
    }
    
    public void StartLog()
    {
        loggingActive = true;
        loggedMessages.clear();
    }
    
    public List<String> GetLogs()
    {
        loggingActive = false;
        return StringFormatter.formatAll(loggedMessages);
    }
    
    // Getters for private static members
    
    public static Map<UUID, WitchdoctorGuiController> getGuiMap()
    {
        return guiMap;
    }
    
    public static Map<String, HelpData> getHelpPages()
    {
        return helpPages;
    }
    
    public static List<CostData> getCureCosts()
    {
        return cureCosts;
    }
    
    public static List<CostData> getSlotExtensionCosts()
    {
        return slotExtensionCosts;
    }
    
    public static CostData getPurgeCost()
    {
        return purgeCost;
    }
    
    public static CostData getScrollCost()
    {
        return scrollCost;
    }
    
    public static CostData getExtractCost()
    {
        return extractCost;
    }
    
    public static CostData getAddCost()
    {
        return addCost;
    }
    
    public static Economy getEconomy()
    {
        return economy;
    }
    
    public static String getTimeWorldName()
    {
        return timeWorldName;
    }
    
    public static String getFallbackCurrencySymbol()
    {
        return fallbackCurrencySymbol;
    }
    
    public static int getMinTradeSlots()
    {
        return minTradeSlots;
    }
    
    public static int getMaxTradeSlots()
    {
        return maxTradeSlots;
    }
    
    public static int getMaxCures()
    {
        return maxCures;
    }
    
    public static int getMinXPReward()
    {
        return minXPReward;
    }
    
    public static int getMaxXPReward()
    {
        return maxXPReward;
    }
}
