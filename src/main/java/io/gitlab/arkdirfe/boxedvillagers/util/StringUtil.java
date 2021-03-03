package io.gitlab.arkdirfe.boxedvillagers.util;

import io.gitlab.arkdirfe.boxedvillagers.data.CostData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;

public final class StringUtil
{
    public static int defaultCharacterWidth = 6;
    private static Map<String, Integer> specialCharacterWidths = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(" ", 4),
            new AbstractMap.SimpleEntry<>("!", 2),
            new AbstractMap.SimpleEntry<>("\"", 5),
            new AbstractMap.SimpleEntry<>("'", 3),
            new AbstractMap.SimpleEntry<>(")", 5),
            new AbstractMap.SimpleEntry<>("*", 5),
            new AbstractMap.SimpleEntry<>(",", 2),
            new AbstractMap.SimpleEntry<>(".", 2),
            new AbstractMap.SimpleEntry<>(":", 2),
            new AbstractMap.SimpleEntry<>(";", 2),
            new AbstractMap.SimpleEntry<>("<", 5),
            new AbstractMap.SimpleEntry<>(">", 5),
            new AbstractMap.SimpleEntry<>("@", 7),
            new AbstractMap.SimpleEntry<>("I", 4),
            new AbstractMap.SimpleEntry<>("[", 4),
            new AbstractMap.SimpleEntry<>("]", 4),
            new AbstractMap.SimpleEntry<>("f", 5),
            new AbstractMap.SimpleEntry<>("i", 2),
            new AbstractMap.SimpleEntry<>("k", 5),
            new AbstractMap.SimpleEntry<>("l", 3),
            new AbstractMap.SimpleEntry<>("t", 4),
            new AbstractMap.SimpleEntry<>("{", 5),
            new AbstractMap.SimpleEntry<>("|", 2),
            new AbstractMap.SimpleEntry<>("}", 5),
            new AbstractMap.SimpleEntry<>("~", 7));

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

    public static int charWidth(char c)
    {
        return specialCharacterWidths.getOrDefault(String.valueOf(c), defaultCharacterWidth);
    }

    public static int stringWidth(String string)
    {
        int width = 0;
        for (char c : string.toCharArray())
        {
            width += charWidth(c);
        }
        return width;
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
            strings.add(String.format("§r§f   -§6%d §a%s", entry.getValue(), capitalize(entry.getKey().toString(), "_")));
        }

        return strings;
    }
}
