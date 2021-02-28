package io.gitlab.arkdirfe.boxedvillagers.data;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class WitchdoctorGuiData
{
    public Inventory gui;
    public ItemStack scroll = null;

    public WitchdoctorGuiData(Inventory gui)
    {
        this.gui = gui;
    }
}
