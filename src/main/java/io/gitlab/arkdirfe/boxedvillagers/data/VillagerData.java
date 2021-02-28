package io.gitlab.arkdirfe.boxedvillagers.data;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import io.gitlab.arkdirfe.boxedvillagers.util.Util;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class VillagerData
{
    public int cures;
    public List<TradeData> trades;
    private final String profession;
    private final int rank;
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

        for(int i = 0; i < compound.getInteger(Strings.TAG_TRADE_COUNT); i++)
        {
            NBTCompound recipeCompound = compound.getCompound("" + i);
            MerchantRecipe recipe = new MerchantRecipe(recipeCompound.getItemStack(Strings.TAG_OUTPUT), recipeCompound.getInteger(Strings.TAG_MAX_USES));
            int reduction = recipeCompound.getInteger(Strings.TAG_REDUCTION);
            recipe.setMaxUses(recipeCompound.getInteger(Strings.TAG_MAX_USES));
            recipe.setUses(recipeCompound.getInteger(Strings.TAG_USES));
            ItemStack i1 = recipeCompound.getItemStack(Strings.TAG_INPUT_1);
            int baseAmount = recipeCompound.getInteger(Strings.TAG_BASE_AMOUNT);
            i1.setAmount(Math.max(baseAmount - reduction * cures, 1));
            ItemStack i2 = recipeCompound.getItemStack(Strings.TAG_INPUT_2);
            recipe.addIngredient(i1);
            recipe.addIngredient(i2);

            trades.add(new TradeData(reduction, baseAmount, recipe));
        }
    }

    public ItemStack writeToItem(NBTItem item)
    {
        item.setBoolean(Strings.TAG_IS_BOUND, true);
        NBTCompound compound = item.getOrCreateCompound(Strings.TAG_DATA_COMPOUND);
        compound.setInteger(Strings.TAG_CURES, cures);
        compound.setString(Strings.TAG_PROFESSION, profession);
        compound.setInteger(Strings.TAG_RANK, rank);
        compound.setInteger(Strings.TAG_TRADE_COUNT, trades.size());
        compound.setLong(Strings.TAG_TIMESTAMP, lastRestocked);

        for(int i = 0; i < trades.size(); i++)
        {
            TradeData trade = trades.get(i);
            MerchantRecipe recipe = trade.recipe;
            ItemStack i1 = recipe.getIngredients().get(0);
            ItemStack i2 = recipe.getIngredients().get(1);

            NBTCompound entry = compound.getOrCreateCompound("" + i);
            entry.setItemStack(Strings.TAG_INPUT_1, i1);
            entry.setItemStack(Strings.TAG_INPUT_2, i2);
            entry.setItemStack(Strings.TAG_OUTPUT, recipe.getResult());
            entry.setInteger(Strings.TAG_MAX_USES, recipe.getMaxUses());
            entry.setInteger(Strings.TAG_USES, recipe.getUses());
            entry.setInteger(Strings.TAG_REDUCTION, trade.reduction);
            entry.setInteger(Strings.TAG_BASE_AMOUNT, trade.baseAmount);
        }

        return item.getItem();
    }

    public List<MerchantRecipe> getMerchantRecipes()
    {
        List<MerchantRecipe> recipes = new ArrayList<>();
        for(TradeData t : trades)
        {
            recipes.add(t.recipe);
        }
        return recipes;
    }

    public void cure(NBTItem item, int times)
    {
        NBTCompound compound = item.getOrCreateCompound(Strings.TAG_DATA_COMPOUND);
        cures = Math.min(7, cures + times);
        compound.setInteger(Strings.TAG_CURES, cures);
    }

    public void updateUses(Merchant merchant)
    {
        for(int i = 0; i < trades.size(); i++)
        {
            MerchantRecipe recipe = merchant.getRecipe(i);
            trades.get(i).recipe.setUses(recipe.getUses());
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
            Util.logWarning("Restock attempted with lower world time than last restocked time. Restock permitted.");
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
                data.recipe.setUses(0);
            }
        }
    }

    public String rankAsString()
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

    public String professionAsString()
    {
        return Strings.capitalize(profession, " ");
    }

    public String curesAsString()
    {
        return cures != 7 ? "" + cures : "§6" + cures;
    }
}