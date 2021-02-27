package io.gitlab.arkdirfe.boxedvillagers;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public final class Util
{
    public static BoxedVillagers plugin;

    public static void updateBoundScrollTooltip(ItemStack item, VillagerData data)
    {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§2Bound Villager Scroll");
        meta.setLore(Arrays.asList("§r§fProfession: §a" + data.professionAsString(),
                "§r§fRank: " + data.rankAsString(),
                "§r§fCures: " + data.curesAsString(),
                "§r§aLeft Click in hand to trade!"));
        item.setItemMeta(meta);
    }

    public static long getTotalTime()
    {
        String worldName = plugin.config.getString(BoxedVillagers.CONFIG_TIME_WORLD);

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

    public static long getDay(long time)
    {
        return time / 24000;
    }

    public  static long getDayTime(long time)
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
