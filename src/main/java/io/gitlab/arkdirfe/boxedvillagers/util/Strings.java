package io.gitlab.arkdirfe.boxedvillagers.util;

import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public final class Strings
{
    private Strings()
    {
    }
    
    // Mutable strings (can be overridden by config)
    private static final Map<StringRef, String> mutableStrings = new EnumMap<>(StringRef.class);
    
    // Immutable strings (can not be overridden by config)
    // Command Strings
    public static final String CMD_BV_GIVE = "give"; //
    public static final String CMD_BV_RELOAD = "reload"; //
    public static final String CMD_BV_HELP = "help"; //
    public static final String CMD_BV_GIVE_UNBOUND_SCROLL = "unbound"; //
    public static final String CMD_BV_GIVE_UNBOUND_SCROLL_NONLETHAL = "unbound-nonlethal"; //
    public static final String CMD_BV_GIVE_TRADE = "trade"; //
    public static final String CMD_BV_RENAME = "rename"; //
    
    // Item Tags
    public static final String TAG_BOXED_VILLAGER_ITEM = "BoxedVillagerItem"; //
    public static final String TAG_IS_BOUND = "IsBound"; //
    public static final String TAG_DATA_COMPOUND = "VillagerData"; //
    public static final String TAG_CURES = "Cures"; //
    public static final String TAG_INPUT_1 = "Input1"; //
    public static final String TAG_INPUT_2 = "Input2"; //
    public static final String TAG_OUTPUT = "Output"; //
    public static final String TAG_BASE_AMOUNT = "BaseAmount"; //
    public static final String TAG_MAX_USES = "MaxUses"; //
    public static final String TAG_USES = "Uses"; //
    public static final String TAG_REDUCTION = "CureReduction"; //
    public static final String TAG_PROFESSION = "Profession"; //
    public static final String TAG_RANK = "Rank"; //
    public static final String TAG_TRADE_COUNT = "TradeCount"; //
    public static final String TAG_TIMESTAMP = "LastRestocked"; //
    public static final String TAG_TRADE_SLOTS = "TradeSlots"; //
    public static final String TAG_NONLETHAL = "NonLethal"; //
    public static final String TAG_NAME = "Name"; //
    
    // UI Helper Tags
    public static final String TAG_UNINTERACTABLE = "Uninteractable"; //
    public static final String TAG_MOVABLE = "Movable"; //
    public static final String TAG_FREE = "Free"; //
    public static final String TAG_EXTRACTED = "Extracted"; //
    public static final String TAG_SERIALIZED_TRADE_DATA = "SerializedTradeData"; //
    
    // Config Entries
    public static final String CONFIG_STRINGS_STRING_ENTRIES = "strings"; //
    public static final String CONFIG_STRINGS_COLORS = "colors"; //
    public static final String CONFIG_TIME_WORLD = "timeWorld"; //
    public static final String CONFIG_HELP = "helpPages"; //
    public static final String CONFIG_COST_CURE = "cureCost"; //
    public static final String CONFIG_COST_PURGE = "purgeCost"; //
    public static final String CONFIG_COST_SLOT = "slotCost"; //
    public static final String CONFIG_COST_SCROLL = "scrollCost"; //
    public static final String CONFIG_COST_EXTRACT = "extractCost"; //
    public static final String CONFIG_COST_ADD = "addCost"; //
    public static final String CONFIG_HELP_WIDTH = "helpWidth"; //
    public static final String CONFIG_CURRENCY_FALLBACK = "fallbackCurrencySymbol"; //
    public static final String CONFIG_MIN_SLOTS = "minTradeSlots"; //
    public static final String CONFIG_MAX_SLOTS = "maxTradeSlots"; //
    
    // Permission Strings
    public static final String PERM_BOXEDVILLAGERS = "boxedvillagers.bv"; //
    public static final String PERM_BOXEDVILLAGERS_HELP = "boxedvillagers.bv.help"; //
    public static final String PERM_BOXEDVILLAGERS_RENAME = "boxedvillagers.bv.rename"; //
    public static final String PERM_BOXEDVILLAGERS_GIVE = "boxedvillagers.bv.give"; //
    public static final String PERM_BOXEDVILLAGERS_RELOAD = "boxedvillagers.bv.reload"; //
    public static final String PERM_CAPTURE = "boxedvillagers.capture"; //
    public static final String PERM_WITCHDOCTOR = "boxedvillagers.witchdoctor"; //
    public static final String PERM_WITCHDOCTOR_ADVANCED = "boxedvillagers.witchdoctor.advanced"; //
    public static final String PERM_WITCHDOCTOR_EXTRACT = "boxedvillagers.witchdoctor.extract"; //
    public static final String PERM_WITCHDOCTOR_ADMIN = "boxedvillagers.witchdoctor.admin"; //
    public static final String PERM_ADMIN = "boxedvillagers.admin"; //
    
    // Formatting Strings
    public static final String FORMAT_DEFAULT_COLOR = "<norm>"; //
    public static final String FORMAT_ENCHANT_COLOR = "<enchant>"; //
    public static final String FORMAT_HELP_COLOR = "<info>"; //
    
    // Static Logger Strings (Logger-facing)
    public static final String LOG_LOADED = "Loaded!"; //
    public static final String LOG_UNLOADED = "Unloaded!"; //
    public static final String LOG_ERROR_TIME_WORLD = "Error loading time world from config!"; //
    public static final String LOG_REGISTER_COMMANDS = "Registered commands and listeners!"; //
    public static final String LOG_LOAD_COSTS = "Loaded costs for operations!"; //
    public static final String LOG_CANT_REGISTER_COMMAND_BOXEDVILLAGERS = "Unable to register BoxedVillager commands! This should never happen =  if it does =  tell Arkdirfe to fix his damn strings!"; //
    public static final String LOG_CANT_REGISTER_COMMAND_WITCHDOCTOR = "Unable to register Witchdoctor commands! This should never happen =  if it does =  tell Arkdirfe to fix his damn strings!"; //
    public static final String LOG_UNAVAILABLE_FROM_CONSOLE = "Only reload command available from console!"; //
    public static final String LOG_RESTOCK_TIME_RAN_BACKWARDS = "Restock attempted with lower world time than last restocked time. If you see this message once it's nothing to worry about =  if you see it often you might want to look into things."; //
    public static final String LOG_INVALID_STRING_OVERRIDE = "Please refer to the comments in strings.yml for proper override usage because you clearly haven't read it."; //
    public static final String LOG_CUSTOM_CONFIG_LOAD_ERROR = "Could not save config to"; //
    public static final String LOG_ECONOMY_SETUP_SUCCESS = "Economy found! Full functionality available for money-based costs."; //
    public static final String LOG_ECONOMY_SETUP_FAIL = "No economy found! Money costs will be ignored."; //
    public static final String LOG_CONFIG_ERROR_GENERIC = "Something went wrong while reading the config =  this is likely caused by invalid formatting."; //
    public static final String LOG_RELOADING = "Reloading BoxedVillagers Config!";
    
    // Dynamic Logger Strings (Logger-facing)
    public static final String LOG_DYN_NO_WORLD = "No world with name %s =  this WILL break!"; // String world name
    public static final String LOG_DYN_MISSING_CONFIG_SECTION = "Config section %s missing!"; // String config section
    public static final String LOG_DYN_MISSING_CONFIG_SECTION_OVERRIDES = "Config section %s missing or no overrides were specified! Ignore this message this is intended."; // String config section
    public static final String LOG_DYN_NO_TITLE = "No title found in help page %s!"; // String help title
    public static final String LOG_DYN_NO_CONTENT = "No content found in help page %s!"; // String help content
    public static final String LOG_DYN_UNKNOWN_MATERIAL = "Unknown material or unsupported currency %s! Ignoring."; // String currency key
    public static final String LOG_DYN_UNEXPECTED_NUMBER = "Unexpected number of cost entries for %s (got %d =  expected %d! This WILL break!"; // String config section =  int list size =  int expected size
    public static final String LOG_DYN_LOAD_HELP = "Loaded %d help pages!"; // int count
    public static final String LOG_DYN_LOAD_STRING_OVERRIDES = "Loaded %d public static final String overrides!"; // int count
    public static final String LOG_DYN_LOAD_COLOR_OVERRIDES = "Loaded %d color overrides!"; // int count
    
    // Internal Error
    public static final String UNKNOWN_STRING = "ERROR RETRIEVING STRING!";
    
    /**
     * Restores mutable strings to their default values.
     */
    public static void restoreMutable()
    {
        mutableStrings.clear();
        
        // Tooltip Static Strings
        mutableStrings.put(StringRef.TT_UNBOUND_SCROLL_TITLE, "<basic>Unbound Villager Scroll"); //
        mutableStrings.put(StringRef.TT_UNBOUND_SCROLL_LORE, "<info>Shift Right Click<norm> on a villager to\n<evil>§mensnare its mortal soul§r<norm> capture it.\nCaptured villagers do not benefit\nfrom previous cures or <basic>Hero of the\n<basic>Village <norm>and can not unlock additional\ntrades from their profession."); //
        mutableStrings.put(StringRef.TT_BOUND_SCROLL_TITLE, "<advanced>Bound Villager Scroll"); //
        mutableStrings.put(StringRef.TT_NONLETHAL_ADMIN_ITEM, "<warn>NONLETHAL SCROLL (ADMIN ITEM)!"); //
        mutableStrings.put(StringRef.TT_HELP_TITLE, "<title>Help"); //
        mutableStrings.put(StringRef.TT_HELP_NO_SCROLL, "Place your bound scroll below.\n\nPurchase scrolls to the right."); //
        mutableStrings.put(StringRef.TT_HELP_HAS_SCROLL, "Purchase scrolls to the right.\n\nUse the buttons on the left to\nupgrade your villager."); //
        mutableStrings.put(StringRef.TT_HELP_HAS_SCROLL_ADVANCED, "\nMove the trades below around\nto change their order.\n\nUse the button on the right to\ncommit your changes.\n\n<info>Note: Prices shown below ignore\n<info>cures."); //
        mutableStrings.put(StringRef.TT_APPLIES_INSTANTLY, "<warn>Applies instantly, irreversible."); //
        mutableStrings.put(StringRef.TT_SLOT_EXTENSION_TITLE, "<title>Extend Trade Slots"); //
        mutableStrings.put(StringRef.TT_SLOT_EXTENSION_FULL, "Your villager has full trade slots."); //
        mutableStrings.put(StringRef.TT_BUY_TITLE, "<title>Buy Villager Scroll"); //
        mutableStrings.put(StringRef.TT_BUY_LORE, "Used to capture villagers."); //
        mutableStrings.put(StringRef.TT_CURE_TITLE, "<title>Cure Villager"); //
        mutableStrings.put(StringRef.TT_CURE_LORE, "Reduces all prices but never below 1."); //
        mutableStrings.put(StringRef.TT_CURE_FULL, "Villager is at max cures!"); //
        mutableStrings.put(StringRef.TT_COMMIT_TITLE, "<title>Commit Changes"); //
        mutableStrings.put(StringRef.TT_COMMIT_NO_CHANGES, "No changes to commit!"); //
        mutableStrings.put(StringRef.TT_COMMIT_CHANGES, "Uncommitted changes!"); //
        mutableStrings.put(StringRef.TT_COMMIT_MOVED, "Trades were moved."); //
        mutableStrings.put(StringRef.TT_TRADE_TITLE, "<basic>Stored Trade"); //
        mutableStrings.put(StringRef.TT_TRADE_PURGE, "<info>Shift Left Click<norm> to purge this trade."); //
        mutableStrings.put(StringRef.TT_TRADE_EXTRACT, "<info>Shift Right Click<norm> to extract this trade."); //
        mutableStrings.put(StringRef.TT_CONVERT_EXTRACTED_TITLE, "<item>Extracted Trade"); //
        mutableStrings.put(StringRef.TT_CONVERT_EXTRACTED_LORE, "<info>Commit to receive item."); //
        mutableStrings.put(StringRef.TT_CONVERT_FREE_TITLE, "<advanced>Extracted Trade"); //
        mutableStrings.put(StringRef.TT_CONVERT_FREE_LORE, "Acts like a regular trade in the Witch Doctor GUI.\nGets added to scroll when committed."); //
        mutableStrings.put(StringRef.TT_COST_TO_STRING_HEADER, "Cost:"); //
        
        // Dynamic Tooltip Strings
        mutableStrings.put(StringRef.TT_DYN_BOUND_SCROLL_LORE, "Name: %s\nCures: %s\nTrade Slots: %s\n<info>Right Click in hand to trade!"); // string name, string cures as string, string slots as string
        mutableStrings.put(StringRef.TT_DYN_SLOTS_AS_STRING_NOT_FULL, "<dynamic>%d<norm>/<static>%d"); // int current, int max
        mutableStrings.put(StringRef.TT_DYN_SLOTS_AS_STRING_FULL, "<dynamic>%d/%d"); // int max slots, int max slots
        mutableStrings.put(StringRef.TT_DYN_SLOT_EXTENSION_SLOTS, "A villager can hold up to <static>%d<norm> trades.\nIt can currently hold <dynamic>%d<norm>."); // int max slots, int current slots
        mutableStrings.put(StringRef.TT_DYN_COMMIT_PURGED, "<static>%d<norm> trades were purged."); // int purged
        mutableStrings.put(StringRef.TT_DYN_COMMIT_EXTRACTED, "<static>%d<norm> trades were extracted."); // int extracted
        mutableStrings.put(StringRef.TT_DYN_COMMIT_ADDED, "<static>%d<norm> new trades were added."); // int added
        mutableStrings.put(StringRef.TT_DYN_TRADE_REDUCTION, "Price reduced by <static>%s<norm> for each cure."); // int cure reduction
        mutableStrings.put(StringRef.TT_DYN_TRADE_TO_STRING_ITEM, "<static>%d <item>%s<norm>"); // int amount, string item name
        mutableStrings.put(StringRef.TT_DYN_COST_TO_STRING_MONEY, "   -<static>%.2f%s<money>"); // int money, string currency suffix
        mutableStrings.put(StringRef.TT_DYN_COST_TO_STRING_ITEM, "   -<static>%d <item>%s"); // int amount, string item name
        
        // UI Strings
        mutableStrings.put(StringRef.UI_WD_TITLE, "<uiheader>Witch Doctor"); //
        mutableStrings.put(StringRef.UI_WD_TITLE_ADMIN, "<uiheader>Witch Doctor <warn>(ADMIN MODE)"); //
        
        // Chat Strings (Player-facing chat messages)
        mutableStrings.put(StringRef.CHAT_GIVE_TRADE_USAGE, "<info>Usage:\n<info>/bv give trade <input1> <input2> <output> <uses> <reduction per cure> [player]\n<info>Use hotbar indices (0-8) for inputs, input2 may be -1 for trades with one input"); //
        mutableStrings.put(StringRef.CHAT_INSUFFICIENT_PERMISSION, "<warn>Insufficient Permission!"); //
        mutableStrings.put(StringRef.CHAT_UNKNOWN_SUB_COMMAND, "<warn>Unknown Sub-Command!"); //
        mutableStrings.put(StringRef.CHAT_NO_HELP_PAGE, "<warn>No help page available under this name!"); //
        mutableStrings.put(StringRef.CHAT_SCROLL_BOUND, "<warn>Scroll already bound!"); //
        mutableStrings.put(StringRef.CHAT_NO_TRADES, "<warn>That villager has no trades!"); //
        mutableStrings.put(StringRef.CHAT_PLAYER_OFFLINE, "<warn>Player offline!"); //
        mutableStrings.put(StringRef.CHAT_NOT_HOLDING_SCROLL, "<warn>Hold the villager scroll you want to rename in your hand!"); //
        mutableStrings.put(StringRef.CHAT_NO_CAPTURE_PERMISSION, "<warn>You do not have permission to capture villagers!"); //
    }
    
    /**
     * Gets a string from either the mutable or immutable list.
     *
     * @param key The key of the string.
     *
     * @return The string itself or an error string if unsuccessful.
     */
    @NotNull
    public static String get(@NotNull final StringRef key)
    {
        return mutableStrings.getOrDefault(key, UNKNOWN_STRING);
    }
    
    /**
     * Sets replaces an existing value with a new one.
     *
     * @param key   Key for the value.
     * @param value New value.
     *
     * @return If the value existed true, otherwise false.
     */
    public static boolean set(@NotNull final StringRef key, @NotNull final String value)
    {
        if(mutableStrings.containsKey(key))
        {
            mutableStrings.put(key, value);
        }
        else
        {
            return false;
        }
        
        return true;
    }
    
    
}
