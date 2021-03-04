package io.gitlab.arkdirfe.boxedvillagers.util;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
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

import java.util.*;

public final class ItemUtil
{
    // Check and Update Methods

    /**
     * Checks if an item is null or AIR, if it is either it is dangerous to use.
     * @param item The item to check.
     * @return True if either null or air, false otherwise.
     */
    public static boolean isNullOrAir(@Nullable final ItemStack item)
    {
        return (item == null || item.getType() == Material.AIR);
    }

    /**
     * Checks if an item is an unbound scroll.
     * @param item Item to check.
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

        if(nbtItem.hasKey(Strings.TAG_BOXED_VILLAGER_ITEM))
        {
            return nbtItem;
        }

        return null;
    }

    /**
     * Checks if an item is a bound scroll.
     * @param item Item to check.
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

        if(nbtItem.hasKey(Strings.TAG_BOXED_VILLAGER_ITEM) && nbtItem.getBoolean(Strings.TAG_IS_BOUND))
        {
            return nbtItem;
        }

        return null;
    }

    /**
     * Sets title, lore and flags of an item.
     * @param item Item to change.
     * @param title New title.
     * @param lore New lore.
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
     * @param nonlethal Whether the scroll should be nonlethal (does not kill villager, admin only).
     * @return The unbound scroll as an ItemStack.
     */
    @NotNull
    public static ItemStack getUnboundScroll(final boolean nonlethal)
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

        setItemTitleLoreAndFlags(scroll, "§aUnbound Villager Scroll", lore, null);

        NBTItem nbtscoll = new NBTItem(scroll);
        nbtscoll.setUUID(Strings.TAG_BOXED_VILLAGER_ITEM, UUID.randomUUID());
        nbtscoll.setBoolean(Strings.TAG_IS_BOUND, false);
        if(nonlethal)
        {
            nbtscoll.setBoolean(Strings.TAG_NONLETHAL, true);
        }
        return nbtscoll.getItem();
    }

    /**
     * Generates a trade item for the /boxedvillagers give trade command.
     * @param player The player the item is for.
     * @param args Arguments passed from the command, includes the hotbar slots the items should be used as templates, the trade capacity and the cure reduction.
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
            player.sendMessage(Strings.ERROR_GIVE_TRADE_INVALID_SLOT);
            return null;
        }

        if(!(slot1 >= 0 && slot1 <= 8 && slot2 >= -1 && slot2 <= 8 && slot3 >= 0 && slot3 <= 8 && uses > 0 && reduction >= 0))
        {
            player.sendMessage(Strings.ERROR_GIVE_TRADE_INVALID_SLOT);
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

            TradeData data = new TradeData(reduction, input1.getAmount(), trade);

            return convertExtractedToFree(convertTradeToExtracted(getTradeItem(data, true))); // Yup
        }

        return null;
    }

    // UI Items for witchdoctor GUI

    /**
     * Returns uninteractable item.
     * @param material Material to use for the item.
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
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack getNoScrollHelpItem()
    {
        ItemStack item = new ItemStack(Material.PAPER);

        setItemTitleLoreAndFlags(item, "§2Help", Arrays.asList("§r§fPlace your bound scroll below to begin the process.", "§r§fYou can purchase scrolls at the right."), Collections.singletonList(ItemFlag.HIDE_ENCHANTS));

        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        return GuiUtil.setUninteractable(item);
    }

    /**
     * Returns the help item for when there is a scroll present.
     * @param advancedPerms Whether the user can manipulate trades.
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack getScrollHelpItem(final boolean advancedPerms)
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

        setItemTitleLoreAndFlags(item, "§2Help", lore, Collections.singletonList(ItemFlag.HIDE_ENCHANTS));

        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        return GuiUtil.setUninteractable(item);
    }

    /**
     * Returns the item that acts as the slot extension button.
     * @param villagerData Data about the villager on the scroll.
     * @param slotCost The cost of the slot.
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack getSlotExtensionItem(@NotNull final VillagerData villagerData, @NotNull CostData slotCost)
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
                lore.addAll(StringUtil.costToString(slotCost));
            }
        }
        else
        {
            lore.add("§r§fYour villager has full trade slots.");
            item.setType(Material.BOOK);
        }

        setItemTitleLoreAndFlags(item, "§2Extend Trade Slots", lore, null);

        return GuiUtil.setUninteractable(item);
    }

    /**
     * Returns item that acts as a button to buy unbound scrolls.
     * @param scrollCost Cost of a scroll.
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack getBuyScrollItem(@NotNull final CostData scrollCost)
    {
        ItemStack item = new ItemStack(Material.PAPER);

        List<String> lore = new ArrayList<>();
        lore.add("§r§fUse it to capture villagers.");

        if(scrollCost.hasCost())
        {
            lore.addAll(StringUtil.costToString(scrollCost));
        }

        setItemTitleLoreAndFlags(item, "§2Buy Villager Scroll", lore, null);

        return GuiUtil.setUninteractable(item);
    }

    /**
     * Returns item that acts as a cure button.
     * @param villagerData Data about the villager on the scroll.
     * @param cureCost Cost of curing.
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack getCureItem(@NotNull final VillagerData villagerData, @NotNull final CostData cureCost)
    {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);

        List<String> lore = new ArrayList<>();

        if(villagerData.getCures() != 7)
        {
            lore.add("§r§fReduces all prices but never below 1.");
            lore.add("§r§4Applies instantly, irreversible.");

            if(cureCost.hasCost())
            {
                lore.addAll(StringUtil.costToString(cureCost));
            }
        }
        else
        {
            lore.add("§r§fVillager is at max cures!");
            item.setType(Material.APPLE);
        }

        setItemTitleLoreAndFlags(item, "§2Cure Villager", lore, null);

        return GuiUtil.setUninteractable(item);
    }

    /**
     * Returns item that acts as a commit button.
     * @param free How many free (newly inserted) trades are in the GUI.
     * @param tradesMoved Whether trades were moved.
     * @param tradesPurged How many trades were purged.
     * @param tradesExtracted How many trades were extracted.
     * @param commitCost Cost of committing.
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack getCommitItem(final int free, final boolean tradesMoved, final int tradesPurged, final int tradesExtracted, @NotNull final CostData commitCost)
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
            lore.addAll(StringUtil.costToString(commitCost));
        }

        setItemTitleLoreAndFlags(item, "§2Commit Changes", lore, Collections.singletonList(ItemFlag.HIDE_ENCHANTS));

        return GuiUtil.setUninteractable(item);
    }

    /**
     * Returns item that represents a trade.
     * @param trade Which trade the item represents.
     * @param extractPerms Whether the player has permission to extract trades.
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack getTradeItem(@NotNull final TradeData trade, final boolean extractPerms)
    {
        ItemStack item = new ItemStack(Material.PAPER);

        List<String> lore = new ArrayList<>();

        lore.add(StringUtil.tradeToString(trade.getRecipe(), trade.getBaseAmount()));
        lore.add("§r§fPrice reduced by §6" + trade.getReduction() + "§f for each cure.");
        lore.add("§r§fShift Left Click to purge this trade.");

        if(extractPerms)
        {
            lore.add("§r§fShift Right Click to extract this trade.");
        }

        setItemTitleLoreAndFlags(item, "§aStored Trade", lore, Collections.singletonList(ItemFlag.HIDE_ENCHANTS));

        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.addCompound(Strings.TAG_SERIALIZED_TRADE_DATA);
        trade.serializeToNBT(compound);

        return GuiUtil.setMovable(nbtItem.getItem());
    }

    /**
     * Converts a trade item to an extracted one.
     * @param item The trade  item to convert.
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack convertTradeToExtracted(@NotNull final ItemStack item)
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

    /**
     * Converts an extracted trade item to a free one.
     * @param item The trade item to convert.
     * @return Item as ItemStack.
     */
    @NotNull
    public static ItemStack convertExtractedToFree(@NotNull final ItemStack item)
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
