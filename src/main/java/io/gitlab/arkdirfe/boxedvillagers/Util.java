package io.gitlab.arkdirfe.boxedvillagers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public final class Util
{
    public static void updateBoundScrollTooltip(ItemStack item, VillagerData data)
    {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§2Bound Villager Scroll");
        meta.setLore(Arrays.asList("§r§fProfession: §a" + data.professionAsString(),
                "§r§fRank: " + data.rankAsString(),
                "§r§fCures: " + data.curesAsString()));
        item.setItemMeta(meta);
    }
}
