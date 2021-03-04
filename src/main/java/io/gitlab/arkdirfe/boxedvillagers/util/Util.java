package io.gitlab.arkdirfe.boxedvillagers.util;

import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.data.VillagerData;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Util
{
    public static BoxedVillagers plugin;
    public static String timeWorldName = "";

    public static void updateBoundScrollTooltip(@NotNull final ItemStack item, @NotNull final VillagerData data)
    {
        setItemTitleLoreAndFlags(item, "§2Bound Villager Scroll", Arrays.asList("§r§fProfession: §a" + data.getProfessionAsString(), "§r§fRank: " + data.getRankAsString(), "§r§fCures: " + data.getCuresAsString(), "§r§fTrade Slots: " + data.getTradeSlotsAsString(), "§r§aLeft Click in hand to trade!"), Collections.singletonList(ItemFlag.HIDE_ENCHANTS));
    }

    public static long getTotalTime()
    {
        World world = plugin.getServer().getWorld(timeWorldName);
        if(world != null)
        {
            return world.getFullTime();
        }
        else
        {
            plugin.getLogger().severe("No world named " + timeWorldName + "!");
        }

        return -1;
    }

    public static boolean isNullOrAir(@Nullable final ItemStack item)
    {
        return (item == null || item.getType() == Material.AIR);
    }

    @Nullable
    public static NBTItem validateUnboundItem(@Nullable final ItemStack item)
    {
        if(isNullOrAir(item))
        {
            return null;
        }

        NBTItem nbtItem = new NBTItem(item);

        if(nbtItem.hasKey(Strings.TAG_BOXED_VILLAGER_ITEM))
        {
            return nbtItem;
        }

        return null;
    }

    @Nullable
    public static NBTItem validateBoundItem(@Nullable final ItemStack item)
    {
        if(isNullOrAir(item))
        {
            return null;
        }

        NBTItem nbtItem = new NBTItem(item);

        if(nbtItem.hasKey(Strings.TAG_BOXED_VILLAGER_ITEM) && nbtItem.getBoolean(Strings.TAG_IS_BOUND))
        {
            return nbtItem;
        }

        return null;
    }

    public static void setItemTitleLoreAndFlags(@NotNull final ItemStack item, @NotNull final String title, @Nullable final List<String> lore, @Nullable final List<ItemFlag> flags)
    {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(lore);

        if(flags != null)
        {
            for(ItemFlag f : flags)
            {
                meta.addItemFlags(f);
            }
        }

        item.setItemMeta(meta);
    }


    public static long getDay(final long time)
    {
        return time / 24000;
    }

    public static long getDayTime(final long time)
    {
        return time % 24000;
    }

    public static void logInfo(final String log)
    {
        plugin.getLogger().info(log);
    }

    public static void logWarning(final String log)
    {
        plugin.getLogger().warning(log);
    }

    public static void logSevere(final String log)
    {
        plugin.getLogger().severe(log);
    }
}
