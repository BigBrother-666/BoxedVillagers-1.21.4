package io.gitlab.arkdirfe.boxedvillagers.data;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import io.gitlab.arkdirfe.boxedvillagers.util.Util;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class VillagerData
{
    public static int maxTradeSlots = 27;
    public static int minTradeSlots = 10;

    private int cures;
    private List<TradeData> trades;
    private final String profession;
    private final int rank;
    private int tradeSlots;
    private long lastRestocked; // In days

    public VillagerData(Villager fromVillager)
    {
        cures = 0;
        trades = new ArrayList<TradeData>();

        for(MerchantRecipe r : fromVillager.getRecipes())
        {
            trades.add(new TradeData((r.getPriceMultiplier() > 0.1 ? 20 : 5), r.getIngredients().get(0).getAmount(), r));
        }

        profession = fromVillager.getProfession().name();
        rank = fromVillager.getVillagerLevel();
        lastRestocked = Util.getDay(Util.getTotalTime());
        tradeSlots = Math.max(minTradeSlots, trades.size());
        attemptRestock();
    }

    public VillagerData(NBTItem fromItem)
    {
        trades = new ArrayList<TradeData>();

        NBTCompound compound = fromItem.getCompound(Strings.TAG_DATA_COMPOUND);
        cures = compound.getInteger(Strings.TAG_CURES);
        profession = compound.getString(Strings.TAG_PROFESSION);
        rank = compound.getInteger(Strings.TAG_RANK);
        lastRestocked = compound.getLong(Strings.TAG_TIMESTAMP);
        tradeSlots = compound.getInteger(Strings.TAG_TRADE_SLOTS);

        for(int i = 0; i < compound.getInteger(Strings.TAG_TRADE_COUNT); i++)
        {
            NBTCompound recipeCompound = compound.getCompound("" + i);

            trades.add(new TradeData(recipeCompound, cures));
        }
    }

    // --- Getters

    public int getCures()
    {
        return cures;
    }

    public List<TradeData> getTrades()
    {
        return trades;
    }

    public int getTradeSlots()
    {
        return tradeSlots;
    }

    public String getRankAsString()
    {
        switch (rank)
        {
            case 1:
                return "§7Novice";
            case 2:
                return "§eApprentice";
            case 3:
                return "§6Journeyman";
            case 4:
                return "§aExpert";
            case 5:
                return "§bMaster";
        }

        return "Invalid!";
    }

    public String getProfessionAsString()
    {
        return Strings.capitalize(profession, " ");
    }

    public String getCuresAsString()
    {
        return cures != 7 ? "" + cures : "§6" + cures;
    }

    public String getTradeSlotsAsString()
    {
        return tradeSlots != maxTradeSlots ? String.format("§e%d§f/§6%d", tradeSlots, maxTradeSlots) : String.format("§6%d/%d", maxTradeSlots, maxTradeSlots);
    }

    // --- Setters/Accessors

    public void setTrades(List<TradeData> trades)
    {
        this.trades = trades;
    }

    // --- Serialization

    public ItemStack writeToItem(NBTItem item)
    {
        if(item.hasKey(Strings.TAG_NONLETHAL))
        {
            item.removeKey(Strings.TAG_NONLETHAL);
        }

        item.setBoolean(Strings.TAG_IS_BOUND, true);
        NBTCompound compound = item.getOrCreateCompound(Strings.TAG_DATA_COMPOUND);
        compound.setInteger(Strings.TAG_CURES, cures);
        compound.setString(Strings.TAG_PROFESSION, profession);
        compound.setInteger(Strings.TAG_RANK, rank);
        compound.setInteger(Strings.TAG_TRADE_COUNT, trades.size());
        compound.setLong(Strings.TAG_TIMESTAMP, lastRestocked);
        compound.setInteger(Strings.TAG_TRADE_SLOTS, tradeSlots);

        for(int i = 0; i < trades.size(); i++)
        {
            NBTCompound entry = compound.getOrCreateCompound("" + i);
            trades.get(i).serializeToNBT(entry);
        }

        ItemStack itemStack = item.getItem();
        itemStack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        return itemStack;
    }

    // --- General Methods

    public List<MerchantRecipe> getMerchantRecipes()
    {
        List<MerchantRecipe> recipes = new ArrayList<>();
        for(TradeData t : trades)
        {
            recipes.add(t.getRecipe());
        }
        return recipes;
    }

    public void cure(NBTItem item, int times)
    {
        NBTCompound compound = item.getOrCreateCompound(Strings.TAG_DATA_COMPOUND);
        cures = Math.min(7, cures + times);
        compound.setInteger(Strings.TAG_CURES, cures);
    }

    public void addTradeSlots(NBTItem item, int slots)
    {
        NBTCompound compound = item.getOrCreateCompound(Strings.TAG_DATA_COMPOUND);
        tradeSlots = Math.min(maxTradeSlots, tradeSlots + slots);
        compound.setInteger(Strings.TAG_TRADE_SLOTS, tradeSlots);
    }

    public void updateUses(Merchant merchant)
    {
        for(int i = 0; i < trades.size(); i++)
        {
            MerchantRecipe recipe = merchant.getRecipe(i);
            trades.get(i).getRecipe().setUses(recipe.getUses());
        }
    }

    public void attemptRestock()
    {
        long time = Util.getTotalTime();
        long days = Util.getDay(time);
        long dayTime = Util.getDayTime(time);

        boolean permitted = false;

        if(days < lastRestocked)
        {
            Util.logWarning("Restock attempted with lower world time than last restocked time. If you see this message once it's nothing to worry about, if you see it often you might want to look into things.");
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


}