package io.gitlab.arkdirfe.boxedvillagers.util;

import io.gitlab.arkdirfe.boxedvillagers.data.CostData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class StringUtil
{
    private StringUtil()
    {
    }

    public static int defaultCharacterWidth = 6;
    private static final Map<String, Integer> specialCharacterWidths = Map.ofEntries(new AbstractMap.SimpleEntry<>(" ", 4), new AbstractMap.SimpleEntry<>("!", 2), new AbstractMap.SimpleEntry<>("\"", 5), new AbstractMap.SimpleEntry<>("'", 3), new AbstractMap.SimpleEntry<>(")", 5), new AbstractMap.SimpleEntry<>("*", 5), new AbstractMap.SimpleEntry<>(",", 2), new AbstractMap.SimpleEntry<>(".", 2), new AbstractMap.SimpleEntry<>(":", 2), new AbstractMap.SimpleEntry<>(";", 2), new AbstractMap.SimpleEntry<>("<", 5), new AbstractMap.SimpleEntry<>(">", 5), new AbstractMap.SimpleEntry<>("@", 7), new AbstractMap.SimpleEntry<>("I", 4), new AbstractMap.SimpleEntry<>("[", 4), new AbstractMap.SimpleEntry<>("]", 4), new AbstractMap.SimpleEntry<>("f", 5), new AbstractMap.SimpleEntry<>("i", 2), new AbstractMap.SimpleEntry<>("k", 5), new AbstractMap.SimpleEntry<>("l", 3), new AbstractMap.SimpleEntry<>("t", 4), new AbstractMap.SimpleEntry<>("{", 5), new AbstractMap.SimpleEntry<>("|", 2), new AbstractMap.SimpleEntry<>("}", 5), new AbstractMap.SimpleEntry<>("~", 7));

    /**
     * Returns a number as roman numerals for enchantment display.
     *
     * @param number The number to convert.
     * @return String with the roman numeral.
     */
    @NotNull
    public static String numberToRoman(int number) // Only 5 for now, expand if needed
    {
        switch(number)
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

    /**
     * Returns pixel width of a char.
     *
     * @param c Char to check.
     * @return Pixel width.
     */
    public static int charWidth(final char c)
    {
        return specialCharacterWidths.getOrDefault(String.valueOf(c), defaultCharacterWidth);
    }

    /**
     * Returns the pixel width of a string.
     *
     * @param string String to check.
     * @return Pixel width.
     */
    public static int stringWidth(@NotNull final String string)
    {
        int width = 0;
        for(char c : string.toCharArray())
        {
            width += charWidth(c);
        }
        return width;
    }

    /**
     * Converts a string to a capitalized version.
     *
     * @param string    The string to convert.
     * @param separator String to convert to spaces.
     * @return The capitalized string.
     */
    @NotNull
    public static String capitalize(@NotNull final String string, @NotNull final String separator)
    {
        String[] words = string.split(separator);
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < words.length; i++)
        {
            result.append(words[i].substring(0, 1).toUpperCase()).append(words[i].substring(1).toLowerCase());
            if(i < words.length - 1)
            {
                result.append(" ");
            }
        }

        return result.toString();
    }

    /**
     * Converts a trade to a readable string.
     *
     * @param recipe The recipe to convert.
     * @param baseAmount Base amount of first ingredient before cures.
     * @return The converted recipe.
     */
    @NotNull
    public static String tradeToString(@NotNull final MerchantRecipe recipe, final int baseAmount)
    {
        ItemStack i1 = recipe.getIngredients().get(0);
        ItemStack i2 = recipe.getIngredients().get(1);
        ItemStack output = recipe.getResult();

        StringBuilder result = new StringBuilder();
        result.append(itemToString(i1, baseAmount));
        if(!ItemUtil.isNullOrAir(i2))
        {
            result.append(" + ").append(itemToString(i2, i2.getAmount()));
        }
        result.append("\n= ").append(itemToString(output, output.getAmount()));

        return result.toString();
    }

    /**
     * Converts an item into a readable string.
     * @param item Item to be converted.
     * @param amount Amount of the item (important for trades).
     * @return Item as string.
     */
    private static String itemToString(@NotNull final ItemStack item, final int amount)
    {
        StringBuilder result = new StringBuilder(String.format(Strings.TT_DYN_TRADE_TO_STRING_ITEM, amount, capitalize(item.getType().getKey().getKey(), "_")));

        if(item.getItemMeta() instanceof EnchantmentStorageMeta)
        {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            Map<Enchantment, Integer> enchants = null;
            if(meta.getStoredEnchants().size() > 0)
            {
                enchants = meta.getStoredEnchants();
            }
            if(meta.getEnchants().size() > 0)
            {
                enchants = meta.getEnchants();
            }

            if(enchants != null)
            {
                int i = 0;
                result.append(" <enchant>(");
                for(Map.Entry<Enchantment, Integer> ench : enchants.entrySet())
                {
                    result.append(String.format("%s %s", capitalize(ench.getKey().getKey().getKey(), "_"), numberToRoman(ench.getValue())));

                    if(i < enchants.size() - 1)
                    {
                        result.append(", ");
                    }

                    i++;
                }
                result.append(")");
            }
        }

        return result.toString();
    }

    /**
     * Converts a CostData to a series of strings.
     *
     * @param cost The CostData to convert.
     * @return List of strings representing the cost.
     */
    @NotNull
    public static List<String> costToString(@NotNull final CostData cost)
    {
        List<String> strings = new ArrayList<>();

        strings.add(Strings.TT_COST_TO_STRING_HEADER);
        if(cost.getMoney() > 0)
        {
            strings.add(String.format(Strings.TT_DYN_COST_TO_STRING_MONEY, cost.getMoney()));
        }

        if(cost.getCrystals() > 0)
        {
            strings.add(String.format(Strings.TT_DYN_COST_TO_STRING_CRYSTALS, cost.getCrystals()));
        }

        for(Map.Entry<Material, Integer> entry : cost.getResources().entrySet())
        {
            strings.add(String.format(Strings.TT_DYN_COST_TO_STRING_ITEM, entry.getValue(), capitalize(entry.getKey().toString(), "_")));
        }

        return strings;
    }
}
