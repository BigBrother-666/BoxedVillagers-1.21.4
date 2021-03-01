package io.gitlab.arkdirfe.boxedvillagers.data;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class WitchdoctorGuiData
{
    public Inventory gui;
    public ItemStack scroll = null;
    public VillagerData villagerData = null;
    public HumanEntity player;

    public boolean tradesMoved;
    public int tradesPurged = 0;

    public WitchdoctorGuiData(Inventory gui, HumanEntity player)
    {
        this.player = player;
        this.gui = gui;
        player.openInventory(gui);
    }

    public void setScroll(ItemStack scroll)
    {
        this.scroll = scroll;
        if(scroll != null)
        {
            villagerData = new VillagerData(new NBTItem(scroll));
        }
        else
        {
            villagerData = null;
        }
    }

    public void resetTracking()
    {
        tradesMoved = false;
        tradesPurged = 0;
    }
}
