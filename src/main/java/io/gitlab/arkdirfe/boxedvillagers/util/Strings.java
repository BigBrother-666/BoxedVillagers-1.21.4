package io.gitlab.arkdirfe.boxedvillagers.util;

import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public final class Strings
{
    private Strings()
    {
    }
    
    // Immutable Strings (can not be set through config)
    private static final Map<StringRef, String> immutableStrings = new EnumMap<>(StringRef.class);
    
    // Mutable strings (can be overridden by config)
    private static final Map<StringRef, String> mutableStrings = new EnumMap<>(StringRef.class);
    
    /**
     * Initializes immutable strings, only needs to be called once
     */
    public static void initImmutable()
    {
        immutableStrings.clear();
        
        // Command Strings
        immutableStrings.put(StringRef.CMD_BV_GIVE, "give"); //
        immutableStrings.put(StringRef.CMD_BV_RELOAD, "reload"); //
        immutableStrings.put(StringRef.CMD_BV_HELP, "help"); //
        immutableStrings.put(StringRef.CMD_BV_GIVE_UNBOUND_SCROLL, "unbound"); //
        immutableStrings.put(StringRef.CMD_BV_GIVE_UNBOUND_SCROLL_NONLETHAL, "unbound-nonlethal"); //
        immutableStrings.put(StringRef.CMD_BV_GIVE_TRADE, "trade"); //
        immutableStrings.put(StringRef.CMD_BV_RENAME, "rename"); //
        
        // Item Tags
        immutableStrings.put(StringRef.TAG_BOXED_VILLAGER_ITEM, "BoxedVillagerItem"); //
        immutableStrings.put(StringRef.TAG_IS_BOUND, "IsBound"); //
        immutableStrings.put(StringRef.TAG_DATA_COMPOUND, "VillagerData"); //
        immutableStrings.put(StringRef.TAG_CURES, "Cures"); //
        immutableStrings.put(StringRef.TAG_INPUT_1, "Input1"); //
        immutableStrings.put(StringRef.TAG_INPUT_2, "Input2"); //
        immutableStrings.put(StringRef.TAG_OUTPUT, "Output"); //
        immutableStrings.put(StringRef.TAG_BASE_AMOUNT, "BaseAmount"); //
        immutableStrings.put(StringRef.TAG_MAX_USES, "MaxUses"); //
        immutableStrings.put(StringRef.TAG_USES, "Uses"); //
        immutableStrings.put(StringRef.TAG_REDUCTION, "CureReduction"); //
        immutableStrings.put(StringRef.TAG_PROFESSION, "Profession"); //
        immutableStrings.put(StringRef.TAG_RANK, "Rank"); //
        immutableStrings.put(StringRef.TAG_TRADE_COUNT, "TradeCount"); //
        immutableStrings.put(StringRef.TAG_TIMESTAMP, "LastRestocked"); //
        immutableStrings.put(StringRef.TAG_TRADE_SLOTS, "TradeSlots"); //
        immutableStrings.put(StringRef.TAG_NONLETHAL, "NonLethal"); //
        immutableStrings.put(StringRef.TAG_NAME, "Name"); //
        
        // UI Helper Tags
        immutableStrings.put(StringRef.TAG_UNINTERACTABLE, "Uninteractable"); //
        immutableStrings.put(StringRef.TAG_MOVABLE, "Movable"); //
        immutableStrings.put(StringRef.TAG_FREE, "Free"); //
        immutableStrings.put(StringRef.TAG_EXTRACTED, "Extracted"); //
        immutableStrings.put(StringRef.TAG_SERIALIZED_TRADE_DATA, "SerializedTradeData"); //
        
        // Config Entries
        immutableStrings.put(StringRef.CONFIG_STRINGS_STRING_ENTRIES, "strings"); //
        immutableStrings.put(StringRef.CONFIG_STRINGS_COLORS, "colors"); //
        immutableStrings.put(StringRef.CONFIG_TIME_WORLD, "timeWorld"); //
        immutableStrings.put(StringRef.CONFIG_HELP, "helpPages"); //
        immutableStrings.put(StringRef.CONFIG_COST_CURE, "cureCost"); //
        immutableStrings.put(StringRef.CONFIG_COST_PURGE, "purgeCost"); //
        immutableStrings.put(StringRef.CONFIG_COST_SLOT, "slotCost"); //
        immutableStrings.put(StringRef.CONFIG_COST_SCROLL, "scrollCost"); //
        immutableStrings.put(StringRef.CONFIG_COST_EXTRACT, "extractCost"); //
        immutableStrings.put(StringRef.CONFIG_COST_ADD, "addCost"); //
        immutableStrings.put(StringRef.CONFIG_HELP_WIDTH, "helpWidth"); //
        immutableStrings.put(StringRef.CONFIG_CURRENCY_FALLBACK, "fallbackCurrencySymbol"); //
        immutableStrings.put(StringRef.CONFIG_MIN_SLOTS, "minTradeSlots"); //
        immutableStrings.put(StringRef.CONFIG_MAX_SLOTS, "maxTradeSlots"); //
        
        // Permission Strings
        immutableStrings.put(StringRef.PERM_WITCHDOCTOR, "boxedvillagers.witchdoctor"); //
        immutableStrings.put(StringRef.PERM_WITCHDOCTOR_ADVANCED, "boxedvillagers.witchdoctor.advanced"); //
        immutableStrings.put(StringRef.PERM_WITCHDOCTOR_EXTRACT, "boxedvillagers.witchdoctor.extract"); //
        immutableStrings.put(StringRef.PERM_ADMIN, "boxedvillagers.admin"); //
        
        // Formatting Strings
        immutableStrings.put(StringRef.FORMAT_DEFAULT_COLOR, "<norm>"); //
        immutableStrings.put(StringRef.FORMAT_ENCHANT_COLOR, "<enchant>"); //
        immutableStrings.put(StringRef.FORMAT_HELP_COLOR, "<info>"); //
        
        // Static Logger Strings (Logger-facing)
        immutableStrings.put(StringRef.LOG_LOADED, "Loaded!"); //
        immutableStrings.put(StringRef.LOG_UNLOADED, "Unloaded!"); //
        immutableStrings.put(StringRef.LOG_ERROR_TIME_WORLD, "Error loading time world from config!"); //
        immutableStrings.put(StringRef.LOG_REGISTER_COMMANDS, "Registered commands and listeners!"); //
        immutableStrings.put(StringRef.LOG_LOAD_COSTS, "Loaded costs for operations!"); //
        immutableStrings.put(StringRef.LOG_CANT_REGISTER_COMMAND_BOXEDVILLAGERS, "Unable to register BoxedVillager commands! This should never happen, if it does, fix yer damn strings!"); //
        immutableStrings.put(StringRef.LOG_CANT_REGISTER_COMMAND_WITCHDOCTOR, "Unable to register Witchdoctor commands! This should never happen, if it does, fix yer damn strings!"); //
        immutableStrings.put(StringRef.LOG_UNAVAILABLE_FROM_CONSOLE, "Only reload command available from console!"); //
        immutableStrings.put(StringRef.LOG_RESTOCK_TIME_RAN_BACKWARDS, "Restock attempted with lower world time than last restocked time. If you see this message once it's nothing to worry about, if you see it often you might want to look into things."); //
        immutableStrings.put(StringRef.LOG_INVALID_STRING_OVERRIDE, "Please refer to the comments in strings.yml for proper override usage because you clearly haven't read it."); //
        immutableStrings.put(StringRef.LOG_CUSTOM_CONFIG_LOAD_ERROR, "Could not save config to"); //
        immutableStrings.put(StringRef.LOG_ECONOMY_SETUP_SUCCESS, "Economy found! Full functionality available for money-based costs."); //
        immutableStrings.put(StringRef.LOG_ECONOMY_SETUP_FAIL, "No economy found! Money costs will be ignored."); //
        immutableStrings.put(StringRef.LOG_CONFIG_ERROR_GENERIC, "Something went wrong while reading the config, this is likely caused by invalid formatting."); //
        
        // Dynamic Logger Strings (Logger-facing)
        immutableStrings.put(StringRef.LOG_DYN_NO_WORLD, "No world with name %s, this WILL break!"); // string world name
        immutableStrings.put(StringRef.LOG_DYN_MISSING_CONFIG_SECTION, "Config section %s missing!"); // string config section
        immutableStrings.put(StringRef.LOG_DYN_MISSING_CONFIG_SECTION_OVERRIDES, "Config section %s missing or no overrides were specified!"); // string config section
        immutableStrings.put(StringRef.LOG_DYN_NO_TITLE, "No title found in help page %s!"); // string help title
        immutableStrings.put(StringRef.LOG_DYN_NO_CONTENT, "No content found in help page %s!"); // string help content
        immutableStrings.put(StringRef.LOG_DYN_UNKNOWN_MATERIAL, "Unknown material or unsupported currency %s! Ignoring."); // string currency key
        immutableStrings.put(StringRef.LOG_DYN_UNEXPECTED_NUMBER, "Unexpected number of cost entries for %s (got %d, expected %d)! This WILL break!"); // string config section, int list size, int expected size
        immutableStrings.put(StringRef.LOG_DYN_LOAD_HELP, "Loaded %d help pages!"); // int count
        immutableStrings.put(StringRef.LOG_DYN_LOAD_STRING_OVERRIDES, "Loaded %d string overrides!"); // int count
        immutableStrings.put(StringRef.LOG_DYN_LOAD_COLOR_OVERRIDES, "Loaded %d color overrides!"); // int count
    }
    
    /**
     * Restores mutable strings to their default values.
     */
    public static void restoreMutable()
    {
        mutableStrings.clear();
        
        // Tooltip Static Strings
        mutableStrings.put(StringRef.TT_UNBOUND_SCROLL_TITLE, "<basic>Unbound Villager Scroll"); //
        mutableStrings.put(StringRef.TT_UNBOUND_SCROLL_LORE, "<info>Shift Right Click<norm> on a villager to <evil>§mensnare its mortal soul§r<norm> capture it.\nCaptured villagers do not benefit from previous cures or\nHero of the Village and can not unlock additional trades."); //
        mutableStrings.put(StringRef.TT_BOUND_SCROLL_TITLE, "<advanced>Bound Villager Scroll"); //
        mutableStrings.put(StringRef.TT_NONLETHAL_ADMIN_ITEM, "<warn>NONLETHAL SCROLL (ADMIN ITEM)!"); //
        mutableStrings.put(StringRef.TT_HELP_TITLE, "<title>Help"); //
        mutableStrings.put(StringRef.TT_HELP_NO_SCROLL, "Place your bound scroll below to begin the process.\nYou can purchase scrolls at the right."); //
        mutableStrings.put(StringRef.TT_HELP_HAS_SCROLL, "Purchase scrolls at the right.\nUse the buttons on the left to upgrade your villager."); //
        mutableStrings.put(StringRef.TT_HELP_HAS_SCROLL_ADVANCED, "Move the trades below around to change their order.\nUse the button on the right to commit your changes.\n<info>Note: Prices shown below ignore cures."); //
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
        mutableStrings.put(StringRef.CHAT_GIVE_TRADE_USAGE, "<info>Usage: /bv give trade <input> <input> <output> <uses> <reduction per cure> [player]\n<info>Use hotbar indices (0-8) for inputs, the second input may be -1 if the trade only has one input."); //
        mutableStrings.put(StringRef.CHAT_INSUFFICIENT_PERMISSION, "<warn>Insufficient Permission!"); //
        mutableStrings.put(StringRef.CHAT_UNKNOWN_SUB_COMMAND, "<warn>Unknown Sub-Command!"); //
        mutableStrings.put(StringRef.CHAT_NO_HELP_PAGE, "<warn>No help page available under this name!"); //
        mutableStrings.put(StringRef.CHAT_SCROLL_BOUND, "<warn>Scroll already bound!"); //
        mutableStrings.put(StringRef.CHAT_NO_TRADES, "<warn>That villager has no trades!"); //
        mutableStrings.put(StringRef.CHAT_PLAYER_OFFLINE, "<warn>Player offline!"); //
        mutableStrings.put(StringRef.CHAT_NOT_HOLDING_SCROLL, "<warn>Hold the villager scroll you want to rename in your hand!"); //
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
        return immutableStrings.getOrDefault(key, mutableStrings.getOrDefault(key, "ERROR RETRIEVING STRING!"));
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
