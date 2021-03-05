package io.gitlab.arkdirfe.boxedvillagers.util;

public final class Strings
{
    private Strings(){}

    //Command Strings
    public static String CMD_BV_GIVE = "give";
    public static String CMD_BV_RELOAD = "reload";
    public static String CMD_BV_HELP = "help";
    public static String CMD_BV_GIVE_UNBOUND_SCROLL = "unbound";
    public static String CMD_BV_GIVE_UNBOUND_SCROLL_NONLETHAL = "unbound-nonlethal";
    public static String CMD_BV_GIVE_TRADE = "trade";
    public static String CMD_BV_RENAME = "rename";

    // Item Tags
    public static String TAG_BOXED_VILLAGER_ITEM = "BoxedVillagerItem";
    public static String TAG_IS_BOUND = "IsBound";
    public static String TAG_DATA_COMPOUND = "VillagerData";
    public static String TAG_CURES = "Cures";
    public static String TAG_INPUT_1 = "Input1";
    public static String TAG_INPUT_2 = "Input2";
    public static String TAG_OUTPUT = "Output";
    public static String TAG_BASE_AMOUNT = "BaseAmount";
    public static String TAG_MAX_USES = "MaxUses";
    public static String TAG_USES = "Uses";
    public static String TAG_REDUCTION = "CureReduction";
    public static String TAG_PROFESSION = "Profession";
    public static String TAG_RANK = "Rank";
    public static String TAG_TRADE_COUNT = "TradeCount";
    public static String TAG_TIMESTAMP = "LastRestocked";
    public static String TAG_TRADE_SLOTS = "TradeSlots";
    public static String TAG_NONLETHAL = "NonLethal";
    public static String TAG_NAME = "Name";

    // UI Helper Tags
    public static String TAG_UNINTERACTABLE = "Uninteractable";
    public static String TAG_MOVABLE = "Movable";
    public static String TAG_FREE = "Free";
    public static String TAG_EXTRACTED = "Extracted";
    public static String TAG_SERIALIZED_TRADE_DATA = "SerializedTradeData";

    // Config Entries
    public static String CONFIG_TIME_WORLD = "timeWorld";
    public static String CONFIG_HELP = "helpPages";
    public static String CONFIG_COST_CURE = "cureCost";
    public static String CONFIG_COST_PURGE = "purgeCost";
    public static String CONFIG_COST_SLOT = "slotCost";
    public static String CONFIG_COST_SCROLL = "scrollCost";
    public static String CONFIG_COST_EXTRACT = "extractCost";
    public static String CONFIG_COST_ADD = "addCost";
    public static String CONFIG_HELP_WIDTH = "helpWidth";

    // Tooltip Static Strings
    public static String TT_UNBOUND_SCROLL_TITLE = "<basic>Unbound Villager Scroll";
    public static String TT_UNBOUND_SCROLL_LORE = "<info>Shift Right Click<norm> on a villager to <evil>§mensnare its mortal soul§r<norm> capture it.\nCaptured villagers do not benefit from previous cures or\nHero of the Village and can not unlock additional trades.";
    public static String TT_BOUND_SCROLL_TITLE = "<advanced>Bound Villager Scroll";
    public static String TT_NONLETHAL_ADMIN_ITEM = "<warn>NONLETHAL SCROLL (ADMIN ITEM)!";
    public static String TT_HELP_TITLE = "<title>Help";
    public static String TT_HELP_NO_SCROLL = "Place your bound scroll below to begin the process.\nYou can purchase scrolls at the right.";
    public static String TT_HELP_HAS_SCROLL = "Purchase scrolls at the right.\nUse the buttons on the left to upgrade your villager.";
    public static String TT_HELP_HAS_SCROLL_ADVANCED = "Move the trades below around to change their order.\nUse the button on the right to commit your changes.\n<info>Note: Prices shown below ignore cures.";
    public static String TT_APPLIES_INSTANTLY = "<warn>Applies instantly, irreversible.";
    public static String TT_SLOT_EXTENSION_TITLE = "<title>Extend Trade Slots";
    public static String TT_SLOT_EXTENSION_FULL = "Your villager has full trade slots.";
    public static String TT_BUY_TITLE = "<title>Buy Villager Scroll";
    public static String TT_BUY_LORE = "Used to capture villagers.";
    public static String TT_CURE_TITLE = "<title>Cure Villager";
    public static String TT_CURE_LORE = "Reduces all prices but never below 1.";
    public static String TT_CURE_FULL = "Villager is at max cures!";
    public static String TT_COMMIT_TITLE = "<title>Commit Changes";
    public static String TT_COMMIT_NO_CHANGES = "No changes to commit!";
    public static String TT_COMMIT_CHANGES = "Uncommitted changes!";
    public static String TT_COMMIT_MOVED = "Trades were moved.";
    public static String TT_TRADE_TITLE = "<basic>Stored Trade";
    public static String TT_TRADE_PURGE = "<info>Shift Left Click<norm> to purge this trade.";
    public static String TT_TRADE_EXTRACT = "<info>Shift Right Click<norm> to extract this trade.";
    public static String TT_CONVERT_EXTRACTED_TITLE = "<item>Extracted Trade";
    public static String TT_CONVERT_EXTRACTED_LORE = "<info>Commit to receive item.";
    public static String TT_CONVERT_FREE_TITLE = "<advanced>Extracted Trade";
    public static String TT_CONVERT_FREE_LORE = "Acts like a regular trade in the Witch Doctor GUI.\nGets added to scroll when committed.";
    public static String TT_COST_TO_STRING_HEADER = "Costs:";

    // Dynamic Tooltip Strings
    public static String TT_DYN_BOUND_SCROLL_LORE = "Name: %s\nCures: %s\nTrade Slots: %s\n<info>Right Click in hand to trade!"; // string name, string cures as string, string slots as string
    public static String TT_DYN_SLOTS_AS_STRING_NOT_FULL = "<dynamic>%d<norm>/<static>%d"; // int current, int max
    public static String TT_DYN_SLOTS_AS_STRING_FULL = "<dynamic>%d/%d"; // int max, int max;
    public static String TT_DYN_SLOT_EXTENSION_SLOTS = "A villager can hold up to <static>%d<norm> trades.\nIt can currently hold <dynamic>%d<norm>."; // int max, int current
    public static String TT_DYN_COMMIT_PURGED = "<static>%d<norm> trades were purged."; // int purged
    public static String TT_DYN_COMMIT_EXTRACTED = "<static>%d<norm> trades were extracted."; // int extracted
    public static String TT_DYN_COMMIT_ADDED = "<static>%d<norm> new trades were added."; // int added
    public static String TT_DYN_TRADE_REDUCTION = "Price reduced by <static>%s<norm> for each cure."; // int cure reduction
    public static String TT_DYN_TRADE_TO_STRING_ITEM = "<static>%d <item>%s<norm>"; // int amount, string item name
    public static String TT_DYN_COST_TO_STRING_MONEY = "   -<static>%d<money> Money"; // int money
    public static String TT_DYN_COST_TO_STRING_CRYSTALS = "   -<static>%d<crystals> Crystals"; // int crystals
    public static String TT_DYN_COST_TO_STRING_ITEM = "   -<static>%d <item>%s"; // int amount, string item name

    // UI Strings
    public static String UI_WD_TITLE = "Witch Doctor";

    // Permission Strings
    public static String PERM_WITCHDOCTOR = "boxedvillagers.witchdoctor";
    public static String PERM_WITCHDOCTOR_ADVANCED = "boxedvillagers.witchdoctor.advanced";
    public static String PERM_WITCHDOCTOR_EXTRACT = "boxedvillagers.witchdoctor.extract";
    public static String PERM_ADMIN = "boxedvillagers.admin";

    // Chat Strings (Player-facing chat messages)
    public static String CHAT_GIVE_TRADE_USAGE = "<info>Usage: /bv give trade <input> <input> <output> <uses> <reduction per cure> [player]\n<info>Use hotbar indices (0-8) for inputs, the second input may be -1 if the trade only has one input.";
    public static String CHAT_INSUFFICIENT_PERMISSION = "<warn>Insufficient Permission!";
    public static String CHAT_UNKNOWN_SUB_COMMAND = "<warn>Unknown Sub-Command!";
    public static String CHAT_NO_HELP_PAGE = "<warn>No help page available under this name!";
    public static String CHAT_SCROLL_BOUND = "<warn>Scroll already bound!";
    public static String CHAT_NO_TRADES = "<warn>That villager has no trades!";
}
