package io.gitlab.arkdirfe.boxedvillagers.util;

public final class Strings
{
    private Strings(){}

    //Command Strings
    public static final String CMD_BV_GIVE = "give";
    public static final String CMD_BV_RELOAD = "reload";
    public static final String CMD_BV_HELP = "help";
    public static final String CMD_BV_GIVE_UNBOUND_SCROLL = "unbound";
    public static final String CMD_BV_GIVE_UNBOUND_SCROLL_NONLETHAL = "unbound-nonlethal";
    public static final String CMD_BV_GIVE_TRADE = "trade";
    public static final String CMD_BV_RENAME = "rename";

    // Item Tags
    public static final String TAG_BOXED_VILLAGER_ITEM = "BoxedVillagerItem";
    public static final String TAG_IS_BOUND = "IsBound";
    public static final String TAG_DATA_COMPOUND = "VillagerData";
    public static final String TAG_CURES = "Cures";
    public static final String TAG_INPUT_1 = "Input1";
    public static final String TAG_INPUT_2 = "Input2";
    public static final String TAG_OUTPUT = "Output";
    public static final String TAG_BASE_AMOUNT = "BaseAmount";
    public static final String TAG_MAX_USES = "MaxUses";
    public static final String TAG_USES = "Uses";
    public static final String TAG_REDUCTION = "CureReduction";
    public static final String TAG_PROFESSION = "Profession";
    public static final String TAG_RANK = "Rank";
    public static final String TAG_TRADE_COUNT = "TradeCount";
    public static final String TAG_TIMESTAMP = "LastRestocked";
    public static final String TAG_TRADE_SLOTS = "TradeSlots";
    public static final String TAG_NONLETHAL = "NonLethal";
    public static final String TAG_NAME = "Name";

    // UI Helper Tags
    public static final String TAG_UNINTERACTABLE = "Uninteractable";
    public static final String TAG_MOVABLE = "Movable";
    public static final String TAG_FREE = "Free";
    public static final String TAG_EXTRACTED = "Extracted";
    public static final String TAG_SERIALIZED_TRADE_DATA = "SerializedTradeData";

    // Config Entries
    public static final String CONFIG_TIME_WORLD = "timeWorld";
    public static final String CONFIG_HELP = "helpPages";
    public static final String CONFIG_COST_CURE = "cureCost";
    public static final String CONFIG_COST_PURGE = "purgeCost";
    public static final String CONFIG_COST_SLOT = "slotCost";
    public static final String CONFIG_COST_SCROLL = "scrollCost";
    public static final String CONFIG_COST_EXTRACT = "extractCost";
    public static final String CONFIG_COST_ADD = "addCost";
    public static final String CONFIG_HELP_WIDTH = "helpWidth";

    // Tooltip static final Strings
    public static final String TT_UNBOUND_SCROLL_TITLE = "<basic>Unbound Villager Scroll";
    public static final String TT_UNBOUND_SCROLL_LORE = "<info>Shift Right Click<norm> on a villager to <evil>§mensnare its mortal soul§r<norm> capture it.\nCaptured villagers do not benefit from previous cures or\nHero of the Village and can not unlock additional trades.";
    public static final String TT_BOUND_SCROLL_TITLE = "<advanced>Bound Villager Scroll";
    public static final String TT_NONLETHAL_ADMIN_ITEM = "<warn>NONLETHAL SCROLL (ADMIN ITEM)!";
    public static final String TT_HELP_TITLE = "<title>Help";
    public static final String TT_HELP_NO_SCROLL = "Place your bound scroll below to begin the process.\nYou can purchase scrolls at the right.";
    public static final String TT_HELP_HAS_SCROLL = "Purchase scrolls at the right.\nUse the buttons on the left to upgrade your villager.";
    public static final String TT_HELP_HAS_SCROLL_ADVANCED = "Move the trades below around to change their order.\nUse the button on the right to commit your changes.\n<info>Note: Prices shown below ignore cures.";
    public static final String TT_APPLIES_INSTANTLY = "<warn>Applies instantly, irreversible.";
    public static final String TT_SLOT_EXTENSION_TITLE = "<title>Extend Trade Slots";
    public static final String TT_SLOT_EXTENSION_FULL = "Your villager has full trade slots.";
    public static final String TT_BUY_TITLE = "<title>Buy Villager Scroll";
    public static final String TT_BUY_LORE = "Used to capture villagers.";
    public static final String TT_CURE_TITLE = "<title>Cure Villager";
    public static final String TT_CURE_LORE = "Reduces all prices but never below 1.";
    public static final String TT_CURE_FULL = "Villager is at max cures!";
    public static final String TT_COMMIT_TITLE = "<title>Commit Changes";
    public static final String TT_COMMIT_NO_CHANGES = "No changes to commit!";
    public static final String TT_COMMIT_CHANGES = "Uncommitted changes!";
    public static final String TT_COMMIT_MOVED = "Trades were moved.";
    public static final String TT_TRADE_TITLE = "<basic>Stored Trade";
    public static final String TT_TRADE_PURGE = "<info>Shift Left Click<norm> to purge this trade.";
    public static final String TT_TRADE_EXTRACT = "<info>Shift Right Click<norm> to extract this trade.";
    public static final String TT_CONVERT_EXTRACTED_TITLE = "<item>Extracted Trade";
    public static final String TT_CONVERT_EXTRACTED_LORE = "<info>Commit to receive item.";
    public static final String TT_CONVERT_FREE_TITLE = "<advanced>Extracted Trade";
    public static final String TT_CONVERT_FREE_LORE = "Acts like a regular trade in the Witch Doctor GUI.\nGets added to scroll when committed.";
    public static final String TT_COST_TO_STRING_HEADER = "Costs:";

    // Dynamic Tooltip Strings
    public static final String TT_DYN_BOUND_SCROLL_LORE = "Name: %s\nCures: %s\nTrade Slots: %s\n<info>Right Click in hand to trade!"; // string name, string cures as string, string slots as string
    public static final String TT_DYN_SLOTS_AS_STRING_NOT_FULL = "<dynamic>%d<norm>/<static final>%d"; // int current, int max
    public static final String TT_DYN_SLOTS_AS_STRING_FULL = "<dynamic>%d/%d"; // int max slots, int max slots
    public static final String TT_DYN_SLOT_EXTENSION_SLOTS = "A villager can hold up to <static final>%d<norm> trades.\nIt can currently hold <dynamic>%d<norm>."; // int max slots, int current slots
    public static final String TT_DYN_COMMIT_PURGED = "<static final>%d<norm> trades were purged."; // int purged
    public static final String TT_DYN_COMMIT_EXTRACTED = "<static final>%d<norm> trades were extracted."; // int extracted
    public static final String TT_DYN_COMMIT_ADDED = "<static final>%d<norm> new trades were added."; // int added
    public static final String TT_DYN_TRADE_REDUCTION = "Price reduced by <static final>%s<norm> for each cure."; // int cure reduction
    public static final String TT_DYN_TRADE_TO_STRING_ITEM = "<static final>%d <item>%s<norm>"; // int amount, string item name
    public static final String TT_DYN_COST_TO_STRING_MONEY = "   -<static final>%d<money> Money"; // int money
    public static final String TT_DYN_COST_TO_STRING_CRYSTALS = "   -<static final>%d<crystals> Crystals"; // int crystals
    public static final String TT_DYN_COST_TO_STRING_ITEM = "   -<static final>%d <item>%s"; // int amount, string item name

    // UI Strings
    public static final String UI_WD_TITLE = "Witch Doctor";

    // Permission Strings
    public static final String PERM_WITCHDOCTOR = "boxedvillagers.witchdoctor";
    public static final String PERM_WITCHDOCTOR_ADVANCED = "boxedvillagers.witchdoctor.advanced";
    public static final String PERM_WITCHDOCTOR_EXTRACT = "boxedvillagers.witchdoctor.extract";
    public static final String PERM_ADMIN = "boxedvillagers.admin";

    // Chat Strings (Player-facing chat messages)
    public static final String CHAT_GIVE_TRADE_USAGE = "<info>Usage: /bv give trade <input> <input> <output> <uses> <reduction per cure> [player]\n<info>Use hotbar indices (0-8) for inputs, the second input may be -1 if the trade only has one input.";
    public static final String CHAT_INSUFFICIENT_PERMISSION = "<warn>Insufficient Permission!";
    public static final String CHAT_UNKNOWN_SUB_COMMAND = "<warn>Unknown Sub-Command!";
    public static final String CHAT_NO_HELP_PAGE = "<warn>No help page available under this name!";
    public static final String CHAT_SCROLL_BOUND = "<warn>Scroll already bound!";
    public static final String CHAT_NO_TRADES = "<warn>That villager has no trades!";

    // Dynamic Debug Strings (Logger-facing)
    public static final String LOG_DYN_NO_WORLD = "No world with name %s, this WILL break!"; // string world name
    public static final String LOG_DYN_MISSING_CONFIG_SECTION = "Config section %s missing!"; // string config section
    public static final String LOG_DYN_NO_TITLE = "No title found in help page %s!"; // string help title
    public static final String LOG_DYN_NO_CONTENT = "No content found in help page %s!"; // string help content
    public static final String LOG_DYN_UNKNOWN_MATERIAL = "Unknown material or unsupported currency %s! Ignoring."; // string currency key
    public static final String LOG_DYN_UNEXPECTED_NUMBER = "Unexpected number of cost entries for %s (got %d, expected %d)! This WILL break!"; // string config section, int list size, int expected size
}
