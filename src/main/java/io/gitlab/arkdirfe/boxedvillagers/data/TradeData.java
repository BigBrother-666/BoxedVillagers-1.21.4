package io.gitlab.arkdirfe.boxedvillagers.data;

import de.tr7zw.nbtapi.NBTCompound;
import io.gitlab.arkdirfe.boxedvillagers.util.Strings;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TradeData
{
    private final int reduction;
    private final int baseAmount;
    private final MerchantRecipe recipe;
    
    /**
     * From MerchantRecipe
     *
     * @param reduction  By how much each cure reduces the price of the first ingredient.
     * @param baseAmount Stack size of the first ingredient before reduction.
     * @param recipe     A merchant recipe representing the trade.
     */
    public TradeData(final int reduction, final int baseAmount, @NotNull final MerchantRecipe recipe)
    {
        this.reduction = reduction;
        this.baseAmount = baseAmount;
        this.recipe = recipe;
    }
    
    /**
     * From NBTCompound
     *
     * @param recipeCompound The NBTCompound the trade is stored in.
     * @param cures          How often the villager has been cured.
     */
    public TradeData(@NotNull final NBTCompound recipeCompound, final int cures)
    {
        recipe = new MerchantRecipe(recipeCompound.getItemStack(Strings.TAG_OUTPUT), recipeCompound.getInteger(Strings.TAG_MAX_USES));
        reduction = recipeCompound.getInteger(Strings.TAG_REDUCTION);
        baseAmount = recipeCompound.getInteger(Strings.TAG_BASE_AMOUNT);
        
        recipe.setMaxUses(recipeCompound.getInteger(Strings.TAG_MAX_USES));
        recipe.setUses(recipeCompound.getInteger(Strings.TAG_USES));
        ItemStack i1 = recipeCompound.getItemStack(Strings.TAG_INPUT_1);
        if (i1 != null) {
            i1.setAmount(Math.max(baseAmount - reduction * cures, 1));
            recipe.addIngredient(i1);
        }
        ItemStack i2 = recipeCompound.getItemStack(Strings.TAG_INPUT_2);
        if (i2 != null) {
            recipe.addIngredient(i2);
        }
    }
    
    // Getters
    
    public int getReduction()
    {
        return reduction;
    }
    
    public int getBaseAmount()
    {
        return baseAmount;
    }
    
    @NotNull
    public MerchantRecipe getRecipe()
    {
        return recipe;
    }
    
    // --- Serialization
    
    /**
     * Adds a serialized representation to an NBTCompound.
     *
     * @param entry The compound to add to.
     */
    public void serializeToNBT(@NotNull final NBTCompound entry)
    {
        List<ItemStack> ingredients = recipe.getIngredients();
        ItemStack i1 = !ingredients.isEmpty() ? ingredients.get(0) : null;
        ItemStack i2 = ingredients.size() > 1 ? ingredients.get(1) : null;

        if (i1 != null) {
            entry.setItemStack(Strings.TAG_INPUT_1, i1);
        }
        if (i2 != null) {
            entry.setItemStack(Strings.TAG_INPUT_2, i2);
        }
        entry.setItemStack(Strings.TAG_OUTPUT, recipe.getResult());
        entry.setInteger(Strings.TAG_MAX_USES, recipe.getMaxUses());
        entry.setInteger(Strings.TAG_USES, recipe.getUses());
        entry.setInteger(Strings.TAG_REDUCTION, reduction);
        entry.setInteger(Strings.TAG_BASE_AMOUNT, baseAmount);
    }
}
