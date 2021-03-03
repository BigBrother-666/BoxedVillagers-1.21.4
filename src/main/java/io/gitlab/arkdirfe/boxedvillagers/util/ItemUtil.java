package io.gitlab.arkdirfe.boxedvillagers.util;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.data.CostData;
import io.gitlab.arkdirfe.boxedvillagers.data.TradeData;
import io.gitlab.arkdirfe.boxedvillagers.data.VillagerData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class ItemUtil
{
    public static ItemStack getUnboundScroll(boolean nonlethal)
    {
        ItemStack scroll = new ItemStack(Material.PAPER);

        List<String> lore = new ArrayList<>();

        lore.add("§r§fRight click on a villager to §4§mensnare its mortal soul§r§f capture it.");
        lore.add("§r§fCaptured villagers do not benefit from previous cures or");
        lore.add("§r§fHero of the Village and can not unlock additional trades.");

        if(nonlethal)
        {
            lore.add("§r§4NONLETHAL SCROLL (ADMIN ITEM)!");
        }

        Util.setItemTitleLoreAndFlags(scroll,
                "§aUnbound Villager Scroll",
                lore, null);

        NBTItem nbtscoll = new NBTItem(scroll);
        nbtscoll.setUUID(Strings.TAG_BOXED_VILLAGER_ITEM, UUID.randomUUID());
        nbtscoll.setBoolean(Strings.TAG_IS_BOUND, false);
        if(nonlethal)
        {
            nbtscoll.setBoolean(Strings.TAG_NONLETHAL, true);
        }
        return nbtscoll.getItem();
    }

    // UI Items

    public static ItemStack getUIFillerItem(Material material)
    {
        ItemStack item = new ItemStack(material);

        Util.setItemTitleLoreAndFlags(item, " ", null, null);

        return GuiUtil.setUninteractable(item);
    }

    public static ItemStack getNoScrollHelpItem()
    {
        ItemStack item = new ItemStack(Material.PAPER);

        Util.setItemTitleLoreAndFlags(item, "§2Help",
                Arrays.asList("§r§fPlace your bound scroll below to begin the process.",
                        "§r§fYou can purchase scrolls at the right."),
                Arrays.asList(ItemFlag.HIDE_ENCHANTS));

        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        return GuiUtil.setUninteractable(item);
    }

    public static ItemStack getScrollHelpItem(boolean advancedPerms)
    {
        ItemStack item = new ItemStack(Material.PAPER);

        List<String> lore = new ArrayList<>();

        lore.add("§r§fEdit trades below.");
        lore.add("§r§fYou can purchase scrolls at the right.");
        lore.add("§r§fUse the buttons on the left to upgrade your villager.");

        if(advancedPerms)
        {
            lore.add("§r§fUse the button on the right to commit your changes.");
            lore.add("§r§fNote: Prices shown below ignore cures.");
        }

        Util.setItemTitleLoreAndFlags(item, "§2Help",
                lore,
                Arrays.asList(ItemFlag.HIDE_ENCHANTS));

        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        return GuiUtil.setUninteractable(item);
    }

    public static ItemStack getSlotExtensionItem(VillagerData villagerData, CostData slotCost)
    {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);

        List<String> lore = new ArrayList<>();

        if(villagerData.getTradeSlots() < VillagerData.maxTradeSlots)
        {
            lore.add(String.format("§r§fYour villager hold up to §6%d§f trades.", VillagerData.maxTradeSlots));
            lore.add(String.format("§r§fIt can currently hold §6%d§f.", villagerData.getTradeSlots()));
            lore.add("§r§4Applies instantly, irreversible.");

            if(slotCost.hasCost())
            {
                lore.addAll(Strings.costToString(slotCost));
            }
        }
        else
        {
            lore.add("§r§fYour villager has full trade slots.");
            item.setType(Material.BOOK);
        }

        Util.setItemTitleLoreAndFlags(item, "§2Extend Trade Slots",
                lore,
                null);

        return GuiUtil.setUninteractable(item);
    }

    public static ItemStack getBuyScrollItem(CostData scrollCost)
    {
        ItemStack item = new ItemStack(Material.PAPER);

        List<String> lore = new ArrayList<>();
        lore.add("§r§fUse it to capture villagers.");

        if(scrollCost.hasCost())
        {
            lore.addAll(Strings.costToString(scrollCost));
        }

        Util.setItemTitleLoreAndFlags(item, "§2Buy Villager Scroll",
                lore,
                null);

        return GuiUtil.setUninteractable(item);
    }

    public static ItemStack getCureItem(int cures, VillagerData villagerData, CostData cureCost)
    {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);

        List<String> lore = new ArrayList<>();

        if(villagerData.getCures() != 7)
        {
            lore.add("§r§fReduces all prices but never below 1.");
            lore.add("§r§4Applies instantly, irreversible.");

            if(cureCost.hasCost())
            {
                lore.addAll(Strings.costToString(cureCost));
            }
        }
        else
        {
            lore.add("§r§fVillager is at max cures!");
            item.setType(Material.APPLE);
        }

        Util.setItemTitleLoreAndFlags(item, "§2Cure Villager",
                lore,
                null);

        return GuiUtil.setUninteractable(item);
    }

    public static ItemStack getCommitItem(int free, boolean tradesMoved, int tradesPurged, int tradesExtracted, CostData commitCost)
    {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);

        List<String> lore = new ArrayList<>();

        if(!tradesMoved && tradesPurged == 0 && tradesExtracted == 0 && free == 0)
        {
            lore.add("§r§fNo changes to commit!");
        }
        else
        {
            lore.add("§r§fUncommitted changes!");
            item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        }

        if(tradesMoved)
        {
            lore.add("§r§fTrades were moved.");
        }
        if(tradesPurged > 0)
        {
            lore.add(String.format("§r§6%d§f trades were purged.", tradesPurged));
        }

        if(tradesExtracted > 0)
        {
            lore.add(String.format("§r§6%d§f trades were extracted.", tradesExtracted));
        }

        if(free > 0)
        {
            lore.add(String.format("§r§6%d§f new trades were added.", free));
        }

        if(commitCost.hasCost())
        {
            lore.addAll(Strings.costToString(commitCost));
        }

        Util.setItemTitleLoreAndFlags(item, "§2Commit Changes",
                lore,
                Arrays.asList(ItemFlag.HIDE_ENCHANTS));

        return GuiUtil.setUninteractable(item);
    }

    public static ItemStack getTradeItem(TradeData trade, boolean extractPerms)
    {
        ItemStack item = new ItemStack(Material.PAPER);

        List<String> lore = new ArrayList<>();

        lore.add(Strings.tradeToString(trade.getRecipe(), trade.getBaseAmount()));
        lore.add("§r§fPrice reduced by §6" + trade.getReduction() + "§f for each cure.");
        lore.add("§r§fShift Left Click to purge this trade.");

        if(extractPerms)
        {
            lore.add("§r§fShift Right Click to extract this trade.");
        }

        Util.setItemTitleLoreAndFlags(item, "§aStored Trade",
                lore,
                Arrays.asList(ItemFlag.HIDE_ENCHANTS));

        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.addCompound(Strings.TAG_SERIALIZED_TRADE_DATA);
        trade.serializeToNBT(compound);

        return GuiUtil.setMovable(nbtItem.getItem());
    }

    public static ItemStack convertTradeToExtracted(ItemStack item)
    {
        item.setType(Material.SUGAR_CANE);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aExtracted Trade");
        List<String> lore = new ArrayList<>();
        lore.add(meta.getLore().get(0));
        lore.add(meta.getLore().get(1));
        lore.add("§r§fCommit to receive item.");
        meta.setLore(lore);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        return GuiUtil.setExtracted(item);
    }

    public static ItemStack convertExtractedToFree(ItemStack item)
    {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§aExtracted Trade");
        List<String> lore = new ArrayList<>();
        lore.add(meta.getLore().get(0));
        lore.add(meta.getLore().get(1));
        lore.add("§r§fActs like a regular trade in the Witch Doctor.");
        lore.add("§r§fGets added to scroll if committed.");
        meta.setLore(lore);
        item.setItemMeta(meta);

        return GuiUtil.setFree(item);
    }
}
