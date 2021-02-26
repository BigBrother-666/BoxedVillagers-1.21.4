package io.gitlab.arkdirfe.boxedvillagers;

import org.bukkit.plugin.java.JavaPlugin;

public class BoxedVillagers extends JavaPlugin
{
    public static String TAG_BOXED_VILLAGER_ITEM = "BoxedVillagerItem";
    public static String TAG_IS_BOUND = "IsBound";

    @Override
    public void onEnable()
    {
        this.getCommand("boxedvillagers").setExecutor(new BoxedVillagersCommandExecutor(this));
        new InteractionListener(this);
        getLogger().info("Loaded!");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("Unloaded!");
    }
}
