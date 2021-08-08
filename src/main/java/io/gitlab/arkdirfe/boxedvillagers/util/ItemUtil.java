package io.gitlab.arkdirfe.boxedvillagers.util;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.data.CostData;
import io.gitlab.arkdirfe.boxedvillagers.data.TradeData;
import io.gitlab.arkdirfe.boxedvillagers.data.VillagerData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class ItemUtil
{
    private ItemUtil()
    {
    }
    
    // Check and Update Methods
    
    /**
     * Checks if an item is null or AIR, if it is either it is dangerous to use.
     *
     * @param item The item to check.
     *
     * @return True if either null or air, false otherwise.
     */
    public static boolean isNullOrAir(@Nullable final ItemStack item)
    {
        return (item == null || item.getType() == Material.AIR);
    }
    
    /**
     * Checks if an item is an unbound scroll.
     *
     * @param item Item to check.
     *
     * @return True if it item is unbound scroll, false if item is not or is null.
     */
    @Nullable
    public static NBTItem validateUnboundItem(@Nullable final ItemStack item)
    {
        if(isNullOrAir(item))
        {
            return null;
        }
        
        NBTItem nbtItem = new NBTItem(item);
        
        if(nbtItem.hasKey(Strings.get(StringRef.TAG_BOXED_VILLAGER_ITEM)))
        {
            return nbtItem;
        }
        
        return null;
    }
    
    /**
     * Checks if an item is a bound scroll.
     *
     * @param item Item to check.
     *
     * @return True if it item is bound scroll, false if item is not or is null.
     */
    @Nullable
    public static NBTItem validateBoundItem(@Nullable final ItemStack item)
    {
        if(isNullOrAir(item))
        {
            return null;
        }
        
        NBTItem nbtItem = new NBTItem(item);
        
        if(nbtItem.hasKey(Strings.get(StringRef.TAG_BOXED_VILLAGER_ITEM)) && nbtItem.getBoolean(Strings.get(StringRef.TAG_IS_BOUND)))
        {
            return nbtItem;
        }
        
        return null;
    }
    
    /**
     * Sets title, lore and flags of an item.
     *
     * @param item  Item to change.
     * @param title New title.
     * @param lore  New lore.
     * @param flags New flags.
     */
    public static void setItemTitleLoreAndFlags(@NotNull final ItemStack item, @NotNull final String title, @Nullable final List<String> lore, @Nullable final List<ItemFlag> flags)
    {
        if(ItemUtil.isNullOrAir(item))
        {
            return;
        }
        
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
    
    // Given Items
    
    /**
     * Returns an unbound scroll.
     *
     * @param nonlethal Whether the scroll should be nonlethal (does not kill villager, admin only).
     *
     * @return The unbound scroll as an ItemStack.
     */
    @NotNull
    public static ItemStack getUnboundScroll(final boolean nonlethal)
    {
        ItemStack scroll = new ItemStack(Material.PAPER);
        
        List<String> lore = new ArrayList<>(StringFormatter.split(Strings.get(StringRef.TT_UNBOUND_SCROLL_LORE)));
        
        if(nonlethal)
        {
            List<String> strings = StringFormatter.split(Strings.get(StringRef.TT_NONLETHAL_ADMIN_ITEM));
            lore.addAll(strings);
        }
        
        setItemTitleLoreAndFlags(scroll, StringFormatter.formatLine(Strings.get(StringRef.TT_UNBOUND_SCROLL_TITLE)), StringFormatter.formatAll(lore), null);
        
        NBTItem nbtscoll = new NBTItem(scroll);
        nbtscoll.setUUID(Strings.get(StringRef.TAG_BOXED_VILLAGER_ITEM), UUID.randomUUID());
        nbtscoll.setBoolean(Strings.get(StringRef.TAG_IS_BOUND), false);
        if(nonlethal)
        {
            nbtscoll.setBoolean(Strings.get(StringRef.TAG_NONLETHAL), true);
        }
        return nbtscoll.getItem();
    }
    
    /**
     * Generates a trade item for the /boxedvillagers give trade command.
     *
     * @param player The player the item is for.
     * @param args   Arguments passed from the command, includes the hotbar slots the items should be used as templates, the trade capacity and the cure reduction.
     *
     * @return Item as ItemStack.
     */
    @Nullable
    public static ItemStack getGeneratedTradeItem(@NotNull Player player, String[] args)
    {
        int slot1, slot2, slot3, uses, reduction;
        
        try
        {
            slot1 = Integer.parseInt(args[2]);
            slot2 = Integer.parseInt(args[3]);
            slot3 = Integer.parseInt(args[4]);
            uses = Integer.parseInt(args[5]);
            reduction = Integer.parseInt(args[6]);
        }
        catch(NumberFormatException e)
        {
            return null;
        }
        
        if(!(slot1 >= 0 && slot1 <= 8 && slot2 >= -1 && slot2 <= 8 && slot3 >= 0 && slot3 <= 8 && uses > 0 && reduction >= 0))
        {
            return null;
        }
        
        ItemStack input1 = player.getInventory().getItem(slot1);
        ItemStack input2 = slot2 == -1 ? new ItemStack(Material.AIR) : player.getInventory().getItem(slot2);
        ItemStack output = player.getInventory().getItem(slot3);
        
        if(!isNullOrAir(input1) && !isNullOrAir(output) && (slot2 == -1 || !isNullOrAir(input2)))
        {
            MerchantRecipe trade = new MerchantRecipe(output, uses);
            trade.addIngredient(input1);
            
            if(slot2 != -1)
            {
                trade.addIngredient(input2);
            }
            else
            {
                trade.addIngredient(new ItemStack(Material.AIR));
            }
            
            TradeData data = new TradeData(reduction, input1.getAmount(), trade);
            
            return convertExtractedToFree(convertTradeToExtracted(getTradeItem(data, true))); // Yup
        }
        
        return null;
    }
    
    // UI Items for witchdoctor GUI
    
    /**
     * Returns uninteractable item.
     *
     * @param material Material to use for the item.
     *
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack getUIFillerItem(@NotNull final Material material)
    {
        ItemStack item = new ItemStack(material);
        
        setItemTitleLoreAndFlags(item, " ", null, null);
        
        return GuiUtil.setUninteractable(item);
    }
    
    /**
     * Returns the help item for when there is no scroll present.
     *
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack getNoScrollHelpItem()
    {
        ItemStack item = new ItemStack(Material.PAPER);
        
        setItemTitleLoreAndFlags(item, StringFormatter.formatLine(Strings.get(StringRef.TT_HELP_TITLE)), StringFormatter.splitAndFormatLines(Strings.get(StringRef.TT_HELP_NO_SCROLL)), Collections.singletonList(ItemFlag.HIDE_ENCHANTS));
        
        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        
        return GuiUtil.setUninteractable(item);
    }
    
    /**
     * Returns the help item for when there is a scroll present.
     *
     * @param advancedPerms Whether the user can manipulate trades.
     *
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack getScrollHelpItem(final boolean advancedPerms)
    {
        ItemStack item = new ItemStack(Material.PAPER);
        
        List<String> lore = new ArrayList<>(StringFormatter.splitAndFormatLines(Strings.get(StringRef.TT_HELP_HAS_SCROLL)));
        
        if(advancedPerms)
        {
            lore.addAll(StringFormatter.splitAndFormatLines(Strings.get(StringRef.TT_HELP_HAS_SCROLL_ADVANCED)));
        }
        
        setItemTitleLoreAndFlags(item, StringFormatter.formatLine(Strings.get(StringRef.TT_HELP_TITLE)), lore, Collections.singletonList(ItemFlag.HIDE_ENCHANTS));
        
        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        
        return GuiUtil.setUninteractable(item);
    }
    
    /**
     * Returns the item that acts as the slot extension button.
     *
     * @param villagerData Data about the villager on the scroll.
     * @param slotCost     The cost of the slot.
     *
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack getSlotExtensionItem(@NotNull final VillagerData villagerData, @Nullable CostData slotCost)
    {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        
        List<String> lore = new ArrayList<>();
        
        if(slotCost != null)
        {
            lore.addAll(StringFormatter.split(String.format(Strings.get(StringRef.TT_DYN_SLOT_EXTENSION_SLOTS), BoxedVillagers.getMaxTradeSlots(), villagerData.getTradeSlots())));
            lore.add(Strings.get(StringRef.TT_APPLIES_INSTANTLY));
            
            if(slotCost.hasCost())
            {
                lore.addAll(StringUtil.costToString(slotCost));
            }
        }
        else
        {
            lore.add(Strings.get(StringRef.TT_SLOT_EXTENSION_FULL));
            item.setType(Material.BOOK);
        }
        
        setItemTitleLoreAndFlags(item, StringFormatter.formatLine(Strings.get(StringRef.TT_SLOT_EXTENSION_TITLE)), StringFormatter.formatAll(lore), null);
        
        return GuiUtil.setUninteractable(item);
    }
    
    /**
     * Returns item that acts as a button to buy unbound scrolls.
     *
     * @param scrollCost Cost of a scroll.
     *
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack getBuyScrollItem(@NotNull final CostData scrollCost)
    {
        ItemStack item = new ItemStack(Material.PAPER);
        
        List<String> lore = new ArrayList<>();
        lore.add(Strings.get(StringRef.TT_BUY_LORE));
        
        if(scrollCost.hasCost())
        {
            lore.addAll(StringUtil.costToString(scrollCost));
        }
        
        setItemTitleLoreAndFlags(item, StringFormatter.formatLine(Strings.get(StringRef.TT_BUY_TITLE)), StringFormatter.formatAll(lore), null);
        
        return GuiUtil.setUninteractable(item);
    }
    
    /**
     * Returns item that acts as a cure button.
     *
     * @param cureCost Cost of curing.
     *
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack getCureItem(@Nullable final CostData cureCost)
    {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        
        List<String> lore = new ArrayList<>();
        
        if(cureCost != null)
        {
            lore.addAll(StringFormatter.split(Strings.get(StringRef.TT_CURE_LORE)));
            lore.add(Strings.get(StringRef.TT_APPLIES_INSTANTLY));
            
            if(cureCost.hasCost())
            {
                lore.addAll(StringUtil.costToString(cureCost));
            }
        }
        else
        {
            lore.addAll(StringFormatter.split(Strings.get(StringRef.TT_CURE_FULL)));
            item.setType(Material.APPLE);
        }
        
        setItemTitleLoreAndFlags(item, StringFormatter.formatLine(Strings.get(StringRef.TT_CURE_TITLE)), StringFormatter.formatAll(lore), null);
        
        return GuiUtil.setUninteractable(item);
    }
    
    /**
     * Returns item that acts as a commit button.
     *
     * @param free            How many free (newly inserted) trades are in the GUI.
     * @param tradesMoved     Whether trades were moved.
     * @param tradesPurged    How many trades were purged.
     * @param tradesExtracted How many trades were extracted.
     * @param commitCost      Cost of committing.
     *
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack getCommitItem(final int free, final boolean tradesMoved, final int tradesPurged, final int tradesExtracted, @NotNull final CostData commitCost)
    {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        
        List<String> lore = new ArrayList<>();
        
        if(!tradesMoved && tradesPurged == 0 && tradesExtracted == 0 && free == 0)
        {
            lore.add(Strings.get(StringRef.TT_COMMIT_NO_CHANGES));
        }
        else
        {
            lore.add(Strings.get(StringRef.TT_COMMIT_CHANGES));
            item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        }
        
        if(tradesMoved)
        {
            lore.add(Strings.get(StringRef.TT_COMMIT_MOVED));
        }
        if(tradesPurged > 0)
        {
            lore.addAll(StringFormatter.split(String.format(Strings.get(StringRef.TT_DYN_COMMIT_PURGED), tradesPurged)));
        }
        
        if(tradesExtracted > 0)
        {
            lore.addAll(StringFormatter.split(String.format(Strings.get(StringRef.TT_DYN_COMMIT_EXTRACTED), tradesExtracted)));
        }
        
        if(free > 0)
        {
            lore.addAll(StringFormatter.split(String.format(Strings.get(StringRef.TT_DYN_COMMIT_ADDED), free)));
        }
        
        if(commitCost.hasCost())
        {
            lore.addAll(StringUtil.costToString(commitCost));
        }
        
        setItemTitleLoreAndFlags(item, StringFormatter.formatLine(Strings.get(StringRef.TT_COMMIT_TITLE)), StringFormatter.formatAll(lore), Collections.singletonList(ItemFlag.HIDE_ENCHANTS));
        
        return GuiUtil.setUninteractable(item);
    }
    
    /**
     * Returns item that represents a trade.
     *
     * @param trade        Which trade the item represents.
     * @param extractPerms Whether the player has permission to extract trades.
     *
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack getTradeItem(@NotNull final TradeData trade, final boolean extractPerms)
    {
        ItemStack item = new ItemStack(Material.PAPER);
        
        List<String> lore = new ArrayList<>();
        
        lore.addAll(StringFormatter.split(StringUtil.tradeToString(trade.getRecipe(), trade.getBaseAmount())));
        lore.addAll(StringFormatter.split(String.format(Strings.get(StringRef.TT_DYN_TRADE_REDUCTION), trade.getReduction())));
        lore.addAll(StringFormatter.split(Strings.get(StringRef.TT_TRADE_PURGE)));
        
        if(extractPerms)
        {
            lore.addAll(StringFormatter.split(Strings.get(StringRef.TT_TRADE_EXTRACT)));
        }
        
        setItemTitleLoreAndFlags(item, StringFormatter.formatLine(Strings.get(StringRef.TT_TRADE_TITLE)), StringFormatter.formatAll(lore), Collections.singletonList(ItemFlag.HIDE_ENCHANTS));
        
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.addCompound(Strings.get(StringRef.TAG_SERIALIZED_TRADE_DATA));
        trade.serializeToNBT(compound);
        
        return GuiUtil.setMovable(nbtItem.getItem());
    }
    
    /**
     * Converts a trade item to an extracted one.
     *
     * @param item The trade  item to convert.
     *
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack convertTradeToExtracted(@NotNull final ItemStack item)
    {
        item.setType(Material.SUGAR_CANE);
        
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(StringFormatter.formatLine(Strings.get(StringRef.TT_CONVERT_EXTRACTED_TITLE)));
        List<String> lore = new ArrayList<>();
        if(meta.getLore().size() > 1)
        {
            lore.add(meta.getLore().get(0));
            lore.add(meta.getLore().get(1));
        }
        lore.addAll(StringFormatter.split(Strings.get(StringRef.TT_CONVERT_EXTRACTED_LORE)));
        meta.setLore(StringFormatter.formatAll(lore));
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        
        return GuiUtil.setExtracted(item);
    }
    
    /**
     * Converts an extracted trade item to a free one.
     *
     * @param item The trade item to convert.
     *
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack convertExtractedToFree(@NotNull final ItemStack item)
    {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(StringFormatter.formatLine(Strings.get(StringRef.TT_CONVERT_FREE_TITLE)));
        List<String> lore = new ArrayList<>();
        if(meta.getLore().size() > 1)
        {
            lore.add(meta.getLore().get(0));
            lore.add(meta.getLore().get(1));
        }
        lore.addAll(StringFormatter.split(Strings.get(StringRef.TT_CONVERT_FREE_LORE)));
        meta.setLore(StringFormatter.formatAll(lore));
        item.setItemMeta(meta);
        
        return GuiUtil.setFree(item);
    }
}
