package io.gitlab.arkdirfe.boxedvillagers.util;

import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.data.VillagerData;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class Util
{
    public static BoxedVillagers plugin;

    public static void updateBoundScrollTooltip(ItemStack item, VillagerData data)
    {
        setItemTitleLoreAndFlags(item,
                "§2Bound Villager Scroll",
                Arrays.asList("§r§fProfession: §a" + data.getProfessionAsString(),
                        "§r§fRank: " + data.getRankAsString(),
                        "§r§fCures: " + data.getCuresAsString(),
                        "§r§fTrade Slots: " + data.getTradeSlotsAsString(),
                        "§r§aLeft Click in hand to trade!"),
                Arrays.asList(ItemFlag.HIDE_ENCHANTS));
    }

    public static long getTotalTime()
    {
        String worldName = plugin.getConfig().getString(Strings.CONFIG_TIME_WORLD);

        if(worldName != null)
        {
            World world = plugin.getServer().getWorld(worldName);
            if(world != null)
            {
                return world.getFullTime();
            }
            else
            {
                logSevere("No world named " + worldName + "!");
            }
        }
        else
        {
            logSevere("Error retrieving world name from config!");
        }

        return -1;
    }

    public static boolean isNotNullOrAir(ItemStack item)
    {
        return !(item == null || item.getType() == Material.AIR);
    }

    public static NBTItem validateUnboundItem(ItemStack item)
    {
        if (!isNotNullOrAir(item))
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

    public static NBTItem validateBoundItem(ItemStack item)
    {
        if (!isNotNullOrAir(item))
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

    public static ItemStack setItemTitleLoreAndFlags(ItemStack item, String title, List<String> lore, List<ItemFlag> flags)
    {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(lore);

        if (flags != null)
        {
            for(ItemFlag f : flags)
            {
                meta.addItemFlags(f);
            }
        }

        item.setItemMeta(meta);

        return item;
    }



    public static long getDay(long time)
    {
        return time / 24000;
    }

    public static long getDayTime(long time)
    {
        return time % 24000;
    }

    public static void logInfo(String log)
    {
        plugin.getLogger().info(log);
    }

    public static void logWarning(String log)
    {
        plugin.getLogger().warning(log);
    }

    public static void logSevere(String log)
    {
        plugin.getLogger().severe(log);
    }
}
