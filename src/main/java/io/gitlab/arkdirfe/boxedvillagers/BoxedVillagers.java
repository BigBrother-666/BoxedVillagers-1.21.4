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
    
    private ConfigAccessor stringsConfig;
    
    @Override
    public void onEnable()
    {
        Strings.initImmutable();
        stringsConfig = new ConfigAccessor(this, "strings.yml");
        
        guiMap = new HashMap<>();
        Util.plugin = this;
        
        reloadConfig();
        registerCommandsAndListeners();
        initializeVault();
        
        getLogger().info(Strings.get(StringRef.LOG_LOADED));
    }
    
    @Override
    public void onDisable()
    {
        witchdoctorGuiManager.cleanupOpenGuis();
        getLogger().info(Strings.get(StringRef.LOG_UNLOADED));
    }
    
    @Override
    public void reloadConfig()
    {
        reloadColorsAndStrings();
        saveDefaultConfig();
        super.reloadConfig();
        
        timeWorldName = getConfig().getString(Strings.get(StringRef.CONFIG_TIME_WORLD));
        
        if(timeWorldName == null)
        {
            getLogger().severe(Strings.get(StringRef.LOG_ERROR_TIME_WORLD));
        }
        else
        {
            if(getServer().getWorld(timeWorldName) == null)
            {
                getLogger().severe(String.format(Strings.get(StringRef.LOG_DYN_NO_WORLD), timeWorldName));
            }
        }
        
        fallbackCurrencySymbol = getConfig().getString(Strings.get(StringRef.CONFIG_CURRENCY_FALLBACK));
        
        try
        {
            maxTradeSlots = Math.max(0, Math.min(27, Integer.parseInt(getConfig().getString(Strings.get(StringRef.CONFIG_MAX_SLOTS)))));
            minTradeSlots = Math.max(0, Math.min(maxTradeSlots, Integer.parseInt(getConfig().getString(Strings.get(StringRef.CONFIG_MIN_SLOTS)))));
        }
        catch(Exception e)
        {
            minTradeSlots = 0;
            maxTradeSlots = 27;
            getLogger().severe(Strings.get(StringRef.LOG_CONFIG_ERROR_GENERIC));
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
        String configSection = Strings.get(StringRef.CONFIG_STRINGS_COLORS);
        ConfigurationSection section = stringsConfig.getConfig().getConfigurationSection(configSection);
        if(section == null)
        {
            getLogger().info(String.format(Strings.get(StringRef.LOG_DYN_MISSING_CONFIG_SECTION_OVERRIDES), configSection));
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
                    getLogger().severe(Strings.get(StringRef.LOG_INVALID_STRING_OVERRIDE));
                }
                else
                {
                    count++;
                }
            }
        }
        
        getLogger().info(String.format(Strings.get(StringRef.LOG_DYN_LOAD_COLOR_OVERRIDES), count));
    }
    
    /**
     * Loads string overrides from config.
     */
    private void loadStrings()
    {
        String configSection = Strings.get(StringRef.CONFIG_STRINGS_STRING_ENTRIES);
        ConfigurationSection section = stringsConfig.getConfig().getConfigurationSection(configSection);
        if(section == null)
        {
            getLogger().info(String.format(Strings.get(StringRef.LOG_DYN_MISSING_CONFIG_SECTION_OVERRIDES), configSection));
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
                getLogger().warning(Strings.get(StringRef.LOG_INVALID_STRING_OVERRIDE));
                continue;
            }
            
            if(value != null)
            {
                if(!Strings.set(keyRef, value))
                {
                    getLogger().warning(Strings.get(StringRef.LOG_INVALID_STRING_OVERRIDE));
                }
                else
                {
                    count++;
                }
            }
        }
        
        getLogger().info(String.format(Strings.get(StringRef.LOG_DYN_LOAD_STRING_OVERRIDES), count));
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
        
        getLogger().info(Strings.get(StringRef.LOG_REGISTER_COMMANDS));
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
        
        initSimpleCostMap(Strings.get(StringRef.CONFIG_COST_PURGE), purgeCost);
        initSimpleCostMap(Strings.get(StringRef.CONFIG_COST_SCROLL), scrollCost);
        initSimpleCostMap(Strings.get(StringRef.CONFIG_COST_EXTRACT), extractCost);
        initSimpleCostMap(Strings.get(StringRef.CONFIG_COST_ADD), addCost);
        
        initLayeredCostMap(Strings.get(StringRef.CONFIG_COST_CURE), cureCosts, 7);
        initLayeredCostMap(Strings.get(StringRef.CONFIG_COST_SLOT), slotExtensionCosts, 27);
        
        getLogger().info(Strings.get(StringRef.LOG_LOAD_COSTS));
    }
    
    /**
     *
     */
    private void initializeVault()
    {
        if(!setupEconomy())
        {
            getLogger().warning(Strings.get(StringRef.LOG_ECONOMY_SETUP_FAIL));
        }
        else
        {
            getLogger().info(Strings.get(StringRef.LOG_ECONOMY_SETUP_SUCCESS));
        }
    }
    
    /**
     * Reads help pages from config.
     */
    private void initHelpPages()
    {
        ConfigurationSection section = getConfig().getConfigurationSection(Strings.get(StringRef.CONFIG_HELP));
        if(section == null)
        {
            getLogger().severe(String.format(Strings.get(StringRef.LOG_DYN_MISSING_CONFIG_SECTION), Strings.get(StringRef.CONFIG_HELP)));
            return;
        }
        
        for(String key : section.getKeys(false))
        {
            String title = getConfig().getString(Strings.get(StringRef.CONFIG_HELP) + "." + key + ".title");
            String content = getConfig().getString(Strings.get(StringRef.CONFIG_HELP) + "." + key + ".content");
            
            if(title == null)
            {
                getLogger().warning(String.format(Strings.get(StringRef.LOG_DYN_NO_TITLE), key));
                title = "";
            }
            if(content == null)
            {
                getLogger().warning(String.format(Strings.get(StringRef.LOG_DYN_NO_CONTENT), key));
                content = "";
            }
            
            helpPages.put(key, new HelpData(title, content));
        }
        
        getLogger().info(String.format(Strings.get(StringRef.LOG_DYN_LOAD_HELP), helpPages.size()));
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
            getLogger().severe(String.format(Strings.get(StringRef.LOG_DYN_MISSING_CONFIG_SECTION), configSection));
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
                    getLogger().warning(String.format(Strings.get(StringRef.LOG_DYN_UNKNOWN_MATERIAL), innerKey));
                }
            }
            
            costs.add(cost);
        }
        
        if(costs.size() != expected)
        {
            getLogger().severe(String.format(Strings.get(StringRef.LOG_DYN_UNEXPECTED_NUMBER), configSection, costs.size(), expected));
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
            getLogger().severe(String.format(Strings.get(StringRef.LOG_DYN_MISSING_CONFIG_SECTION), configSection));
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
}
