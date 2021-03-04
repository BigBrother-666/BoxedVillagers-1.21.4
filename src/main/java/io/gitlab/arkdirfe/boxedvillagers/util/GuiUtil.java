package io.gitlab.arkdirfe.boxedvillagers.util;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GuiUtil
{
    @NotNull
    public static ItemStack setUninteractable(@NotNull final ItemStack item)
    {
        return setTag(item, Strings.TAG_UNINTERACTABLE);
    }

    @NotNull
    public static ItemStack setMovable(@NotNull final ItemStack item)
    {
        return setTag(item, Strings.TAG_MOVABLE);
    }

    @NotNull
    public static ItemStack setFree(@NotNull final ItemStack item)
    {
        return setTag(item, Strings.TAG_FREE);
    }

    @NotNull
    public static ItemStack setExtracted(@NotNull final ItemStack item)
    {
        return setTag(item, Strings.TAG_EXTRACTED);
    }

    public static boolean isUninteractable(@Nullable final ItemStack item)
    {
        return hasTag(item, Strings.TAG_UNINTERACTABLE);
    }

    public static boolean isMovable(@Nullable final ItemStack item)
    {
        return hasTag(item, Strings.TAG_MOVABLE);
    }

    public static boolean isFree(@Nullable final ItemStack item)
    {
        return hasTag(item, Strings.TAG_FREE);
    }

    public static boolean isExtracted(@NotNull final ItemStack item)
    {
        return hasTag(item, Strings.TAG_EXTRACTED);
    }

    @NotNull
    private static ItemStack setTag(@NotNull final ItemStack item, @NotNull final String tag)
    {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean(tag, true);
        return nbtItem.getItem();
    }

    private static boolean hasTag(@Nullable final ItemStack item, @NotNull final String tag)
    {
        if(Util.isNullOrAir(item))
        {
            return false;
        }

        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.hasKey(tag);
    }

    public static int getGuiSlot(final int row, final int col)
    {
        return 9 * row + col;
    }
}
