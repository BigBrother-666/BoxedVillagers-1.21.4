package io.gitlab.arkdirfe.boxedvillagers.util;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public final class GuiUtil
{
    public static ItemStack setUninteractable(ItemStack item)
    {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean(Strings.TAG_UNINTERACTABLE, true);
        return nbtItem.getItem();
    }

    public static ItemStack setMovable(ItemStack item)
    {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean(Strings.TAG_MOVABLE, true);
        return nbtItem.getItem();
    }

    public static boolean isUninteractable(ItemStack item)
    {
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasKey(Strings.TAG_UNINTERACTABLE);
    }

    public static boolean isMovable(ItemStack item)
    {
        if(!Util.isNotNullOrAir(item))
        {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasKey(Strings.TAG_MOVABLE);
    }
}
