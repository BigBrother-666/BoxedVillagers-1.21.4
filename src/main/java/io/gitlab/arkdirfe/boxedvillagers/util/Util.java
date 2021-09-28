package io.gitlab.arkdirfe.boxedvillagers.util;

import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import org.bukkit.World;

public final class Util
{
    private Util()
    {
    }
    
    public static BoxedVillagers plugin;
    
    /**
     * Gets the total world time from the configured world. This value is used for keeping track of villager restocks.
     *
     * @return The total world time.
     */
    public static long getTotalTime()
    {
        String timeWorldName = BoxedVillagers.getTimeWorldName();
        if(timeWorldName != null)
        {
            World world = plugin.getServer().getWorld(timeWorldName);
            if(world != null)
            {
                return world.getFullTime();
            }
            else
            {
                plugin.getLogger().severe(String.format(Strings.LOG_DYN_NO_WORLD, timeWorldName));
            }
        }
        
        return -1;
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
