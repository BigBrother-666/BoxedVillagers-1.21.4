package io.gitlab.arkdirfe.boxedvillagers.data;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.BoxedVillagers;
import io.gitlab.arkdirfe.boxedvillagers.util.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VillagerData
{
    private int cures;
    private List<TradeData> trades;
    private final String profession;
    private final int rank;
    private int tradeSlots;
    private long lastRestocked; // In days
    private ItemStack linkedItem;
    private String name;
    
    /**
     * Creates VillagerData from a villager and an unbound scroll.
     *
     * @param fromVillager  The villager.
     * @param unboundScroll The unbound scroll.
     */
    public VillagerData(@NotNull final Villager fromVillager, @NotNull final NBTItem unboundScroll)
    {
        cures = 0;
        trades = new ArrayList<>();
        
        for(MerchantRecipe r : fromVillager.getRecipes())
        {
            trades.add(new TradeData((r.getPriceMultiplier() > 0.1 ?
                    20 :
                    5), r.getIngredients().get(0).getAmount(), r));
        }
        
        profession = fromVillager.getProfession().name();
        rank = fromVillager.getVillagerLevel();
        lastRestocked = Util.getDay(Util.getTotalTime());
        tradeSlots = Math.max(BoxedVillagers.getMinTradeSlots(), trades.size());
        linkedItem = unboundScroll.getItem();
        name = fromVillager.getName();
        attemptRestock();
    }
    
    /**
     * Creates VillagerData from an item. Used when reading from an existing bound scroll.
     *
     * @param fromItem Item to extract from.
     */
    public VillagerData(@NotNull final NBTItem fromItem)
    {
        trades = new ArrayList<>();
        
        NBTCompound compound = fromItem.getCompound(Strings.TAG_DATA_COMPOUND);
        cures = compound.getInteger(Strings.TAG_CURES);
        profession = compound.getString(Strings.TAG_PROFESSION);
        rank = compound.getInteger(Strings.TAG_RANK);
        lastRestocked = compound.getLong(Strings.TAG_TIMESTAMP);
        tradeSlots = compound.getInteger(Strings.TAG_TRADE_SLOTS);
        name = compound.getString(Strings.TAG_NAME);
        
        for(int i = 0; i < compound.getInteger(Strings.TAG_TRADE_COUNT); i++)
        {
            NBTCompound recipeCompound = compound.getCompound("" + i);
            
            trades.add(new TradeData(recipeCompound, cures));
        }
        
        linkedItem = fromItem.getItem();
    }
    
    // --- Getters
    
    public int getCures()
    {
        return cures;
    }
    
    @NotNull
    public List<TradeData> getTrades()
    {
        return trades;
    }
    
    public int getTradeSlots()
    {
        return tradeSlots;
    }
    
    @NotNull
    public String getCuresAsString()
    {
        return cures != 7 ? "" + cures : "<static>" + cures;
    }
    
    @NotNull
    public String getTradeSlotsAsString()
    {
        return tradeSlots != BoxedVillagers.getMaxTradeSlots() ?
                String.format(Strings.get(StringRef.TT_DYN_SLOTS_AS_STRING_NOT_FULL), tradeSlots, BoxedVillagers.getMaxTradeSlots()) :
                String.format(Strings.get(StringRef.TT_DYN_SLOTS_AS_STRING_FULL), BoxedVillagers.getMaxTradeSlots(), BoxedVillagers.getMaxTradeSlots());
    }
    
    // --- Setters/Accessors
    
    public void setTrades(@NotNull final List<TradeData> trades)
    {
        this.trades = trades;
    }
    
    // --- Serialization
    
    /**
     * Updates the bound scroll and returns it in item stack form.
     *
     * @return The bound scroll.
     */
    @NotNull
    public ItemStack getItem()
    {
        NBTItem nbtItem = new NBTItem(linkedItem);
        
        if(nbtItem.hasKey(Strings.TAG_NONLETHAL))
        {
            nbtItem.removeKey(Strings.TAG_NONLETHAL);
        }
        
        nbtItem.setBoolean(Strings.TAG_IS_BOUND, true);
        NBTCompound compound = nbtItem.getOrCreateCompound(Strings.TAG_DATA_COMPOUND);
        compound.setInteger(Strings.TAG_CURES, cures);
        compound.setString(Strings.TAG_PROFESSION, profession);
        compound.setInteger(Strings.TAG_RANK, rank);
        compound.setInteger(Strings.TAG_TRADE_COUNT, trades.size());
        compound.setLong(Strings.TAG_TIMESTAMP, lastRestocked);
        compound.setInteger(Strings.TAG_TRADE_SLOTS, tradeSlots);
        compound.setString(Strings.TAG_NAME, name);
        
        for(int i = 0; i < trades.size(); i++)
        {
            NBTCompound entry = compound.getOrCreateCompound("" + i);
            trades.get(i).serializeToNBT(entry);
        }
        
        linkedItem = nbtItem.getItem();
        linkedItem.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        
        updateTitleAndLore();
        
        return linkedItem;
    }
    
    // --- General Methods
    
    /**
     * Returns a list of MerchantRecipe, needed due to TradeData objects holding the individual entries.
     *
     * @return The list.
     */
    @NotNull
    public List<MerchantRecipe> getMerchantRecipes()
    {
        List<MerchantRecipe> recipes = new ArrayList<>();
        for(TradeData t : trades)
        {
            recipes.add(t.getRecipe());
        }
        return recipes;
    }
    
    /**
     * Cures the villager.
     *
     * @param times How many times to cure.
     */
    public void cure(final int times)
    {
        NBTItem nbtItem = new NBTItem(linkedItem);
        NBTCompound compound = nbtItem.getOrCreateCompound(Strings.TAG_DATA_COMPOUND);
        cures = Math.min(7, cures + times);
        compound.setInteger(Strings.TAG_CURES, cures);
        linkedItem = nbtItem.getItem();
    }
    
    /**
     * Adds trade slots to the villager.
     *
     * @param slots How many slots.
     */
    public void addTradeSlots(final int slots)
    {
        NBTItem nbtItem = new NBTItem(linkedItem);
        NBTCompound compound = nbtItem.getOrCreateCompound(Strings.TAG_DATA_COMPOUND);
        tradeSlots = Math.min(BoxedVillagers.getMaxTradeSlots(), tradeSlots + slots);
        compound.setInteger(Strings.TAG_TRADE_SLOTS, tradeSlots);
        linkedItem = nbtItem.getItem();
    }
    
    /**
     * Renames the villager.
     *
     * @param newName The new name, should not be empty or blank.
     */
    public void rename(@NotNull final String newName)
    {
        if(!newName.isBlank() && !newName.isEmpty() && newName.length() < 50)
        {
            name = newName;
        }
    }
    
    /**
     * Updates how many uses the trades have left.
     *
     * @param merchant The merchant generated for the trade GUI.
     */
    public void updateUses(@NotNull final Merchant merchant)
    {
        for(int i = 0; i < trades.size(); i++)
        {
            MerchantRecipe recipe = merchant.getRecipe(i);
            trades.get(i).getRecipe().setUses(recipe.getUses());
        }
    }
    
    /**
     * Attempts to restock the villager, succeeds or fails based on time passed since last restock.
     */
    public void attemptRestock()
    {
        long time = Util.getTotalTime();
        long days = Util.getDay(time);
        long dayTime = Util.getDayTime(time);
        
        boolean permitted = false;
        
        if(days < lastRestocked)
        {
            Util.logWarning(Strings.LOG_RESTOCK_TIME_RAN_BACKWARDS);
            permitted = true;
        }
        
        if(!permitted && Math.abs(days - lastRestocked) > 1) // Guaranteed to have passed noon
        {
            permitted = true;
        }
        
        if(!permitted && Math.abs(days - lastRestocked) == 1 && dayTime >= 6000) // Past noon on day after last restock
        {
            permitted = true;
        }
        
        if(permitted)
        {
            lastRestocked = days;
            
            for(TradeData data : trades)
            {
                data.getRecipe().setUses(0);
            }
        }
    }
    
    /**
     * Updates item title and lore.
     */
    private void updateTitleAndLore()
    {
        ItemUtil.setItemTitleLoreAndFlags(linkedItem, StringFormatter.formatLine(Strings.get(StringRef.TT_BOUND_SCROLL_TITLE)), StringFormatter.splitAndFormatLines(String.format(Strings.get(StringRef.TT_DYN_BOUND_SCROLL_LORE), name, getCuresAsString(), getTradeSlotsAsString())), Collections.singletonList(ItemFlag.HIDE_ENCHANTS));
    }
}