package io.gitlab.arkdirfe.boxedvillagers;

import org.bukkit.plugin.java.JavaPlugin;

public class BoxedVillagers extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        getLogger().info("Loaded!");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("Unloaded!");
    }
}
