package io.gitlab.arkdirfe.boxedvillagers.util;

public final class Strings
{
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

    // UI Helper Tags
    public static String TAG_UNINTERACTABLE = "Uninteractable";
    public static String TAG_MOVABLE = "Movable";
    public static String TAG_FREE = "Free";
    public static String TAG_EXTRACTED = "Extracted";
    public static String TAG_SERIALIZED_TRADE_DATA = "SerializedTradeData";

    // Config Entries
    public static String CONFIG_TIME_WORLD = "timeWorld";
    public static String CONFIG_COST_CURE = "cureCost";
    public static String CONFIG_COST_PURGE = "purgeCost";
    public static String CONFIG_COST_SLOT = "slotCost";
    public static String CONFIG_COST_SCROLL = "scrollCost";
    public static String CONFIG_COST_EXTRACT = "extractCost";
    public static String CONFIG_COST_ADD = "addCost";

    // UI Strings
    public static String UI_WD_TITLE = "Witch Doctor";

    // Permission Strings
    public static String PERM_WITCHDOCTOR = "boxedvillagers.witchdoctor";
    public static String PERM_WITCHDOCTOR_ADVANCED = "boxedvillagers.witchdoctor.advanced";
    public static String PERM_WITCHDOCTOR_EXTRACT = "boxedvillagers.witchdoctor.extract";
    public static String PERM_ADMIN = "boxedvillagers.admin";

    public static String numberToRoman(int number) // Only 5 for now, expand if needed
    {
        switch (number)
        {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            default:
                return "" + number;
        }
    }

    public static String capitalize(String string, String separator)
    {
        String[] words = string.split(separator);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++)
        {
            result.append(words[i].substring(0, 1).toUpperCase()).append(words[i].substring(1).toLowerCase());
            if(i < words.length - 1)
            {
                result.append(" ");
            }
        }

        return result.toString();
    }
}
