package io.gitlab.arkdirfe.boxedvillagers.data;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class CostData
{
    /**
     * Returns a new CostData which holds the sum of all the provided CostData objects.
     *
     * @param data The objects to sum up.
     * @return The sum.
     */
    public static CostData sum(final CostData... data)
    {
        CostData result = new CostData();
        double money = 0;

        for(CostData d : data)
        {
            money += d.getMoney();

            for(Map.Entry<Material, Integer> entry : d.getResources().entrySet())
            {
                result.addResource(entry.getKey(), entry.getValue());
            }
        }

        result.money = money;

        return result;
    }

    private double money = 0;
    private final EnumMap<Material, Integer> resources;

    /**
     * Container for any cost, includes basic items, money and crystals.
     */
    public CostData()
    {
        resources = new EnumMap<>(Material.class);
    }

    public boolean hasCost()
    {
        return money != 0 || !resources.isEmpty();
    }

    public double getMoney()
    {
        return money;
    }

    public void setMoney(double money)
    {
        this.money = Math.max(0, money);
    }

    public Map<Material, Integer> getResources()
    {
        return resources;
    }

    /**
     * Returns a CostData object which is multiplied by a number. Costs cannot go below 1 for each entry.
     *
     * @param multiplier The number the costs are multiplied with.
     * @return The multiplied CostData.
     */
    @NotNull
    public CostData getMultiplied(final float multiplier)
    {
        CostData data = new CostData();

        if(multiplier == 0)
        {
            return data;
        }

        for(Map.Entry<Material, Integer> entry : resources.entrySet())
        {
            data.addResource(entry.getKey(), (int) Math.ceil(entry.getValue() * multiplier));
        }

        data.setMoney((int) Math.ceil(money * multiplier));

        return data;
    }

    /**
     * Adds resources, creates new entries if needed.
     *
     * @param mat   The material of the resource.
     * @param count The count of the resource.
     */
    public void addResource(@NotNull final Material mat, final int count)
    {
        resources.put(mat, Math.max(0, count) + resources.getOrDefault(mat, 0));
    }
}
