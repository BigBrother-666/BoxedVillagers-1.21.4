package io.gitlab.arkdirfe.boxedvillagers.data;

import org.bukkit.inventory.MerchantRecipe;

public class TradeData
{
    public int reduction;
    public int baseAmount;
    public MerchantRecipe recipe;

    public TradeData(int reduction, int baseAmount, MerchantRecipe recipe)
    {
        this.reduction = reduction;
        this.baseAmount = baseAmount;
        this.recipe = recipe;
    }
}
