package io.gitlab.arkdirfe.boxedvillagers;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class VillagerData
{
    public static String TAG_DATA_COMPOUND = "VillagerData";
    public static String TAG_CURES = "Cures";
    public static String TAG_INPUT_1 = "Input1";
    public static String TAG_INPUT_2 = "Input2";
    public static String TAG_OUTPUT = "Output";
    public static String TAG_BASE_AMOUNT = "BaseAmount";
    public static String TAG_MAX_USES = "MaxUses";
    public static String TAG_USES = "Uses";
    public static String TAG_REDUCTION = "CureReduction";
    public static String TAG_PROFESSION = "Profession";
    public static String TAG_RANK = "Rank";
    public static String TAG_TRADE_COUNT = "TradeCount";
    public static String TAG_TIMESTAMP = "LastRestocked";

    public int cures;
    public List<MerchantRecipe> trades;
    private final String profession;
    private final int rank;
    private long lastRestocked; // In days

    public VillagerData(Villager fromVillager)
    {
        cures = 0;
        trades = fromVillager.getRecipes();
        profession = fromVillager.getProfession().name();
        rank = fromVillager.getVillagerLevel();
        lastRestocked = Util.getDay(Util.getTotalTime());
        attemptRestock();
    }

    public VillagerData(NBTItem fromItem)
    {
        trades = new ArrayList<MerchantRecipe>();

        NBTCompound compound = fromItem.getCompound(TAG_DATA_COMPOUND);
        cures = compound.getInteger(TAG_CURES);
        profession = compound.getString(TAG_PROFESSION);
        rank = compound.getInteger(TAG_RANK);
        lastRestocked = compound.getLong(TAG_TIMESTAMP);

        for(int i = 0; i < compound.getInteger(TAG_TRADE_COUNT); i++)
        {
            NBTCompound recipeCompound = compound.getCompound("" + i);
            MerchantRecipe recipe = new MerchantRecipe(recipeCompound.getItemStack(TAG_OUTPUT), recipeCompound.getInteger(TAG_MAX_USES));
            int reduction = recipeCompound.getInteger(TAG_REDUCTION);
            recipe.setPriceMultiplier(reduction == 5 ? 0.05f : 0.2f);
            recipe.setMaxUses(recipeCompound.getInteger(TAG_MAX_USES));
            recipe.setUses(recipeCompound.getInteger(TAG_USES));
            ItemStack i1 = recipeCompound.getItemStack(TAG_INPUT_1);
            i1.setAmount(Math.max(recipeCompound.getInteger(TAG_BASE_AMOUNT) - reduction * cures, 1));
            ItemStack i2 = recipeCompound.getItemStack(TAG_INPUT_2);
            recipe.addIngredient(i1);
            recipe.addIngredient(i2);

            trades.add(recipe);
        }
    }

    public ItemStack writeToItem(NBTItem item, boolean setBaseValue)
    {
        item.setBoolean(BoxedVillagers.TAG_IS_BOUND, true);
        NBTCompound compound = item.getOrCreateCompound(TAG_DATA_COMPOUND);
        compound.setInteger(TAG_CURES, cures);
        compound.setString(TAG_PROFESSION, profession);
        compound.setInteger(TAG_RANK, rank);
        compound.setInteger(TAG_TRADE_COUNT, trades.size());
        compound.setLong(TAG_TIMESTAMP, lastRestocked);

        for(int i = 0; i < trades.size(); i++)
        {
            MerchantRecipe recipe = trades.get(i);
            ItemStack i1 = recipe.getIngredients().get(0);
            ItemStack i2 = recipe.getIngredients().get(1);

            NBTCompound entry = compound.getOrCreateCompound("" + i);
            entry.setItemStack(TAG_INPUT_1, i1);
            entry.setItemStack(TAG_INPUT_2, i2);
            entry.setItemStack(TAG_OUTPUT, recipe.getResult());
            entry.setInteger(TAG_MAX_USES, recipe.getMaxUses());
            entry.setInteger(TAG_USES, recipe.getUses());
            entry.setInteger(TAG_REDUCTION, recipe.getPriceMultiplier() > 0.1 ? 20 : 5);

            if(setBaseValue)
            {
                entry.setInteger(TAG_BASE_AMOUNT, i1.getAmount());
            }
        }

        return item.getItem();
    }

    public void cure(NBTItem item, int times)
    {
        NBTCompound compound = item.getOrCreateCompound(TAG_DATA_COMPOUND);
        cures = Math.min(7, cures + times);
        compound.setInteger(TAG_CURES, cures);
    }

    public void updateUses(Merchant merchant)
    {
        for(int i = 0; i < trades.size(); i++)
        {
            MerchantRecipe recipe = merchant.getRecipe(i);
            trades.get(i).setUses(recipe.getUses());
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

            for(MerchantRecipe recipe : trades)
            {
                recipe.setUses(0);
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
        return profession.substring(0, 1).toUpperCase() + profession.substring(1).toLowerCase();
    }

    public String curesAsString()
    {
        return cures != 7 ? "" + cures : "§6" + cures;
    }
}