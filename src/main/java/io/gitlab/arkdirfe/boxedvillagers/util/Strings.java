package io.gitlab.arkdirfe.boxedvillagers.util;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public final class Strings
{
    private Strings()
    {
    }

    // Immutable Strings (can not be set through config)
    private static final Map<String, String> immutableStrings = Map.<String, String>ofEntries(
            // Command Strings
            new AbstractMap.SimpleImmutableEntry<>("CMD_BV_GIVE", "give"), //
            new AbstractMap.SimpleImmutableEntry<>("CMD_BV_RELOAD", "reload"), //
            new AbstractMap.SimpleImmutableEntry<>("CMD_BV_HELP", "help"), //
            new AbstractMap.SimpleImmutableEntry<>("CMD_BV_GIVE_UNBOUND_SCROLL", "unbound"), //
            new AbstractMap.SimpleImmutableEntry<>("CMD_BV_GIVE_UNBOUND_SCROLL_NONLETHAL", "unbound-nonlethal"), //
            new AbstractMap.SimpleImmutableEntry<>("CMD_BV_GIVE_TRADE", "trade"), //
            new AbstractMap.SimpleImmutableEntry<>("CMD_BV_RENAME", "rename"), //

            // Item Tags
            new AbstractMap.SimpleImmutableEntry<>("TAG_BOXED_VILLAGER_ITEM", "BoxedVillagerItem"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_IS_BOUND", "IsBound"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_DATA_COMPOUND", "VillagerData"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_CURES", "Cures"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_INPUT_1", "Input1"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_INPUT_2", "Input2"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_OUTPUT", "Output"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_BASE_AMOUNT", "BaseAmount"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_MAX_USES", "MaxUses"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_USES", "Uses"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_REDUCTION", "CureReduction"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_PROFESSION", "Profession"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_RANK", "Rank"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_TRADE_COUNT", "TradeCount"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_TIMESTAMP", "LastRestocked"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_TRADE_SLOTS", "TradeSlots"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_NONLETHAL", "NonLethal"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_NAME", "Name"), //

            // UI Helper Tags
            new AbstractMap.SimpleImmutableEntry<>("TAG_UNINTERACTABLE", "Uninteractable"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_MOVABLE", "Movable"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_FREE", "Free"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_EXTRACTED", "Extracted"), //
            new AbstractMap.SimpleImmutableEntry<>("TAG_SERIALIZED_TRADE_DATA", "SerializedTradeData"), //

            // Config Entries
            new AbstractMap.SimpleImmutableEntry<>("CONFIG_STRINGS_STRING_ENTRIES", "strings"), //
            new AbstractMap.SimpleImmutableEntry<>("CONFIG_STRINGS_COLORS", "colors"), //
            new AbstractMap.SimpleImmutableEntry<>("CONFIG_TIME_WORLD", "timeWorld"), //
            new AbstractMap.SimpleImmutableEntry<>("CONFIG_HELP", "helpPages"), //
            new AbstractMap.SimpleImmutableEntry<>("CONFIG_COST_CURE", "cureCost"), //
            new AbstractMap.SimpleImmutableEntry<>("CONFIG_COST_PURGE", "purgeCost"), //
            new AbstractMap.SimpleImmutableEntry<>("CONFIG_COST_SLOT", "slotCost"), //
            new AbstractMap.SimpleImmutableEntry<>("CONFIG_COST_SCROLL", "scrollCost"), //
            new AbstractMap.SimpleImmutableEntry<>("CONFIG_COST_EXTRACT", "extractCost"), //
            new AbstractMap.SimpleImmutableEntry<>("CONFIG_COST_ADD", "addCost"), //
            new AbstractMap.SimpleImmutableEntry<>("CONFIG_HELP_WIDTH", "helpWidth"), //

            // Permission Strings
            new AbstractMap.SimpleImmutableEntry<>("PERM_WITCHDOCTOR", "boxedvillagers.witchdoctor"), //
            new AbstractMap.SimpleImmutableEntry<>("PERM_WITCHDOCTOR_ADVANCED", "boxedvillagers.witchdoctor.advanced"), //
            new AbstractMap.SimpleImmutableEntry<>("PERM_WITCHDOCTOR_EXTRACT", "boxedvillagers.witchdoctor.extract"), //
            new AbstractMap.SimpleImmutableEntry<>("PERM_ADMIN", "boxedvillagers.admin"), //

            // Formatting Strings
            new AbstractMap.SimpleImmutableEntry<>("FORMAT_DEFAULT_COLOR", "<norm>"), //
            new AbstractMap.SimpleImmutableEntry<>("FORMAT_ENCHANT_COLOR", "<enchant>"), //
            new AbstractMap.SimpleImmutableEntry<>("FORMAT_HELP_COLOR", "<info>"), //

            // Static Logger Strings (Logger-facing)
            new AbstractMap.SimpleImmutableEntry<>("LOG_LOADED", "Loaded!"), //
            new AbstractMap.SimpleImmutableEntry<>("LOG_UNLOADED", "Unloaded!"), //
            new AbstractMap.SimpleImmutableEntry<>("LOG_ERROR_TIME_WORLD", "Error loading time world from config!"), //
            new AbstractMap.SimpleImmutableEntry<>("LOG_REGISTER_COMMANDS", "Registered commands and listeners!"), //
            new AbstractMap.SimpleImmutableEntry<>("LOG_LOAD_COSTS", "Loaded costs for operations!"), //
            new AbstractMap.SimpleImmutableEntry<>("LOG_CANT_REGISTER_COMMAND_BOXEDVILLAGERS", "Unable to register BoxedVillager commands! This should never happen, if it does, fix yer damn strings!"), //
            new AbstractMap.SimpleImmutableEntry<>("LOG_CANT_REGISTER_COMMAND_WITCHDOCTOR", "Unable to register Witchdoctor commands! This should never happen, if it does, fix yer damn strings!"), //
            new AbstractMap.SimpleImmutableEntry<>("LOG_UNAVAILABLE_FROM_CONSOLE", "Only reload command available from console!"), //
            new AbstractMap.SimpleImmutableEntry<>("LOG_RESTOCK_TIME_RAN_BACKWARDS", "Restock attempted with lower world time than last restocked time. If you see this message once it's nothing to worry about, if you see it often you might want to look into things."), //
            new AbstractMap.SimpleImmutableEntry<>("LOG_INVALID_STRING_OVERRIDE", "Please refer to the comments in strings.yml for proper override usage because you clearly haven't read it."), //

            // Dynamic Logger Strings (Logger-facing)
            new AbstractMap.SimpleImmutableEntry<>("LOG_DYN_NO_WORLD", "No world with name %s, this WILL break!"), // string world name
            new AbstractMap.SimpleImmutableEntry<>("LOG_DYN_MISSING_CONFIG_SECTION", "Config section %s missing!"), // string config section
            new AbstractMap.SimpleImmutableEntry<>("LOG_DYN_MISSING_CONFIG_SECTION_OVERRIDES", "Config section %s missing or no overrides were specified!"), // string config section
            new AbstractMap.SimpleImmutableEntry<>("LOG_DYN_NO_TITLE", "No title found in help page %s!"), // string help title
            new AbstractMap.SimpleImmutableEntry<>("LOG_DYN_NO_CONTENT", "No content found in help page %s!"), // string help content
            new AbstractMap.SimpleImmutableEntry<>("LOG_DYN_UNKNOWN_MATERIAL", "Unknown material or unsupported currency %s! Ignoring."), // string currency key
            new AbstractMap.SimpleImmutableEntry<>("LOG_DYN_UNEXPECTED_NUMBER", "Unexpected number of cost entries for %s (got %d, expected %d)! This WILL break!"), // string config section, int list size, int expected size
            new AbstractMap.SimpleImmutableEntry<>("LOG_DYN_LOAD_HELP", "Loaded %d help pages!"), // int count
            new AbstractMap.SimpleImmutableEntry<>("LOG_DYN_LOAD_STRING_OVERRIDES", "Loaded %d string overrides!"), // int count
            new AbstractMap.SimpleImmutableEntry<>("LOG_DYN_LOAD_COLOR_OVERRIDES", "Loaded %d color overrides!") // int count
    );

    // Mutable strings (can be overridden by config)
    private static Map<String, String> mutableStrings = null;

    /**
     * Restores the mutable strings to their default values.
     */
    public static void restoreDefaults()
    {
        mutableStrings = new HashMap<>();

        // Tooltip Static Strings
        mutableStrings.put("TT_UNBOUND_SCROLL_TITLE", "<basic>Unbound Villager Scroll"); //
        mutableStrings.put("TT_UNBOUND_SCROLL_LORE", "<info>Shift Right Click<norm> on a villager to <evil>§mensnare its mortal soul§r<norm> capture it.\nCaptured villagers do not benefit from previous cures or\nHero of the Village and can not unlock additional trades."); //
        mutableStrings.put("TT_BOUND_SCROLL_TITLE", "<advanced>Bound Villager Scroll"); //
        mutableStrings.put("TT_NONLETHAL_ADMIN_ITEM", "<warn>NONLETHAL SCROLL (ADMIN ITEM)!"); //
        mutableStrings.put("TT_HELP_TITLE", "<title>Help"); //
        mutableStrings.put("TT_HELP_NO_SCROLL", "Place your bound scroll below to begin the process.\nYou can purchase scrolls at the right."); //
        mutableStrings.put("TT_HELP_HAS_SCROLL", "Purchase scrolls at the right.\nUse the buttons on the left to upgrade your villager."); //
        mutableStrings.put("TT_HELP_HAS_SCROLL_ADVANCED", "Move the trades below around to change their order.\nUse the button on the right to commit your changes.\n<info>Note: Prices shown below ignore cures."); //
        mutableStrings.put("TT_APPLIES_INSTANTLY", "<warn>Applies instantly, irreversible."); //
        mutableStrings.put("TT_SLOT_EXTENSION_TITLE", "<title>Extend Trade Slots"); //
        mutableStrings.put("TT_SLOT_EXTENSION_FULL", "Your villager has full trade slots."); //
        mutableStrings.put("TT_BUY_TITLE", "<title>Buy Villager Scroll"); //
        mutableStrings.put("TT_BUY_LORE", "Used to capture villagers."); //
        mutableStrings.put("TT_CURE_TITLE", "<title>Cure Villager"); //
        mutableStrings.put("TT_CURE_LORE", "Reduces all prices but never below 1."); //
        mutableStrings.put("TT_CURE_FULL", "Villager is at max cures!"); //
        mutableStrings.put("TT_COMMIT_TITLE", "<title>Commit Changes"); //
        mutableStrings.put("TT_COMMIT_NO_CHANGES", "No changes to commit!"); //
        mutableStrings.put("TT_COMMIT_CHANGES", "Uncommitted changes!"); //
        mutableStrings.put("TT_COMMIT_MOVED", "Trades were moved."); //
        mutableStrings.put("TT_TRADE_TITLE", "<basic>Stored Trade"); //
        mutableStrings.put("TT_TRADE_PURGE", "<info>Shift Left Click<norm> to purge this trade."); //
        mutableStrings.put("TT_TRADE_EXTRACT", "<info>Shift Right Click<norm> to extract this trade."); //
        mutableStrings.put("TT_CONVERT_EXTRACTED_TITLE", "<item>Extracted Trade"); //
        mutableStrings.put("TT_CONVERT_EXTRACTED_LORE", "<info>Commit to receive item."); //
        mutableStrings.put("TT_CONVERT_FREE_TITLE", "<advanced>Extracted Trade"); //
        mutableStrings.put("TT_CONVERT_FREE_LORE", "Acts like a regular trade in the Witch Doctor GUI.\nGets added to scroll when committed."); //
        mutableStrings.put("TT_COST_TO_STRING_HEADER", "Costs:"); //

        // Dynamic Tooltip Strings
        mutableStrings.put("TT_DYN_BOUND_SCROLL_LORE", "Name: %s\nCures: %s\nTrade Slots: %s\n<info>Right Click in hand to trade!"); // string name, string cures as string, string slots as string
        mutableStrings.put("TT_DYN_SLOTS_AS_STRING_NOT_FULL", "<dynamic>%d<norm>/<static>%d"); // int current, int max
        mutableStrings.put("TT_DYN_SLOTS_AS_STRING_FULL", "<dynamic>%d/%d"); // int max slots, int max slots
        mutableStrings.put("TT_DYN_SLOT_EXTENSION_SLOTS", "A villager can hold up to <static>%d<norm> trades.\nIt can currently hold <dynamic>%d<norm>."); // int max slots, int current slots
        mutableStrings.put("TT_DYN_COMMIT_PURGED", "<static>%d<norm> trades were purged."); // int purged
        mutableStrings.put("TT_DYN_COMMIT_EXTRACTED", "<static>%d<norm> trades were extracted."); // int extracted
        mutableStrings.put("TT_DYN_COMMIT_ADDED", "<static>%d<norm> new trades were added."); // int added
        mutableStrings.put("TT_DYN_TRADE_REDUCTION", "Price reduced by <static>%s<norm> for each cure."); // int cure reduction
        mutableStrings.put("TT_DYN_TRADE_TO_STRING_ITEM", "<static>%d <item>%s<norm>"); // int amount, string item name
        mutableStrings.put("TT_DYN_COST_TO_STRING_MONEY", "   -<static>%d<money> Money"); // int money
        mutableStrings.put("TT_DYN_COST_TO_STRING_CRYSTALS", "   -<static>%d<crystals> Crystals"); // int crystals
        mutableStrings.put("TT_DYN_COST_TO_STRING_ITEM", "   -<static>%d <item>%s"); // int amount, string item name

        // UI Strings
        mutableStrings.put("UI_WD_TITLE", "<uiheader>Witch Doctor"); //
        mutableStrings.put("UI_WD_TITLE_ADMIN", "<uiheader>Witch Doctor <warn>(ADMIN MODE)"); //

        // Chat Strings (Player-facing chat messages)
        mutableStrings.put("CHAT_GIVE_TRADE_USAGE", "<info>Usage: /bv give trade <input> <input> <output> <uses> <reduction per cure> [player]\n<info>Use hotbar indices (0-8) for inputs, the second input may be -1 if the trade only has one input."); //
        mutableStrings.put("CHAT_INSUFFICIENT_PERMISSION", "<warn>Insufficient Permission!"); //
        mutableStrings.put("CHAT_UNKNOWN_SUB_COMMAND", "<warn>Unknown Sub-Command!"); //
        mutableStrings.put("CHAT_NO_HELP_PAGE", "<warn>No help page available under this name!"); //
        mutableStrings.put("CHAT_SCROLL_BOUND", "<warn>Scroll already bound!"); //
        mutableStrings.put("CHAT_NO_TRADES", "<warn>That villager has no trades!"); //
        mutableStrings.put("CHAT_PLAYER_OFFLINE", "<warn>Player offline!"); //
    }

    /**
     * Gets a string from either the mutable or immutable list.
     *
     * @param key The key of the string.
     * @return The string itself or an error string if unsuccessful.
     */
    @NotNull
    public static String get(@NotNull final String key)
    {
        return immutableStrings.getOrDefault(key, mutableStrings.getOrDefault(key, "ERROR RETRIEVING STRING!"));
    }

    /**
     * Sets replaces an existing value with a new one.
     *
     * @param key   Key for the value.
     * @param value New value.
     * @return If the value existed true, otherwise false.
     */
    public static boolean set(@NotNull final String key, @NotNull final String value)
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
