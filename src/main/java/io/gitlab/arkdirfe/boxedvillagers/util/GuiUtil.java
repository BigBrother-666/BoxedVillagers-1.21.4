package io.gitlab.arkdirfe.boxedvillagers.util;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public final class GuiUtil
{
    public static ItemStack setUninteractable(ItemStack item)
    {
        return setTag(item, Strings.TAG_UNINTERACTABLE);
    }

    public static ItemStack setMovable(ItemStack item)
    {
        return setTag(item, Strings.TAG_MOVABLE);
    }

    public static ItemStack setFree(ItemStack item)
    {
        return setTag(item, Strings.TAG_FREE);
    }

    public static ItemStack setExtracted(ItemStack item)
    {
        return setTag(item, Strings.TAG_EXTRACTED);
    }

    public static boolean isUninteractable(ItemStack item)
    {
        return hasTag(item, Strings.TAG_UNINTERACTABLE);
    }

    public static boolean isMovable(ItemStack item)
    {
        return hasTag(item, Strings.TAG_MOVABLE);
    }

    public static boolean isFree(ItemStack item)
    {
        return hasTag(item, Strings.TAG_FREE);
    }

    public static boolean isExtracted(ItemStack item)
    {
        return hasTag(item, Strings.TAG_EXTRACTED);
    }

    private static ItemStack setTag(ItemStack item, String tag)
    {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean(tag, true);
        return nbtItem.getItem();
    }

    private static boolean hasTag(ItemStack item, String tag)
    {
        if(!Util.isNotNullOrAir(item))
        {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasKey(tag);
    }

    public static int getGuiSlot(int row, int col)
    {
        return 9 * row + col;
    }

}
