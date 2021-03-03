package io.gitlab.arkdirfe.boxedvillagers.util;

import io.gitlab.arkdirfe.boxedvillagers.data.CostData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    // Error Strings
    public static String ERROR_GIVE_TRADE_INVALID_SLOT = "Invalid input! Make sure the slot numbers are between 0 and 8 (inclusive) and there are items in the slots! The second slot may be -1 if no second input is desired.)";

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

    public static String tradeToString(MerchantRecipe recipe, int baseAmount)
    {
        ItemStack i1 = recipe.getIngredients().get(0);
        ItemStack i2 = recipe.getIngredients().get(1);
        ItemStack output = recipe.getResult();

        StringBuilder result = new StringBuilder();
        result.append("§r§f");
        result.append(String.format("§6%d §a%s§f", baseAmount, capitalize(i1.getType().getKey().getKey(), "_")));
        if(i2.getType() != Material.AIR)
        {
            result.append(String.format(" + §6%d§f §a%s§f", i2.getAmount(), capitalize(i2.getType().getKey().getKey(), "_")));
        }
        result.append(String.format(" = §6%d§f §a%s§f", output.getAmount(), capitalize(output.getType().getKey().getKey(), "_")));
        if(output.getType() == Material.ENCHANTED_BOOK)
        {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta)output.getItemMeta();
            for(Map.Entry<Enchantment, Integer> ench : meta.getStoredEnchants().entrySet())
            {
                result.append(String.format(" §5(%s %s)§f", capitalize(ench.getKey().getKey().getKey(), "_"), numberToRoman(ench.getValue())));
            }
        }

        return result.toString();
    }

    public static List<String> costToString(CostData cost)
    {
        List<String> strings = new ArrayList<>();

        strings.add("§r§fCosts:");
        if(cost.getMoney() > 0)
        {
            strings.add(String.format("§r§f   -§6%d §eMoney", cost.getMoney()));
        }

        if(cost.getCrystals() > 0)
        {
            strings.add(String.format("§r§f   -§6%d §bCrystals", cost.getCrystals()));
        }

        for (Map.Entry<Material, Integer> entry : cost.getResources().entrySet())
        {
            strings.add(String.format("§r§f   -§6%d §a%s", entry.getValue(), Strings.capitalize(entry.getKey().toString(), "_")));
        }

        return strings;
    }
}
