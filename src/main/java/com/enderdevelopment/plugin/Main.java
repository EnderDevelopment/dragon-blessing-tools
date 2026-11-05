package com.enderdevelopment.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        getCommand("dragonblessing").setExecutor(new DragonBlessingCommand(this));
        getCommand("dragontools").setExecutor(new DragonToolsCommand(this));
        getServer().getPluginManager().registerEvents(new DragonToolListener(this), this);
        getLogger().info("DragonBlessing enabled - the ancient dragons have blessed the tools!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DragonBlessing disabled.");
    }

    public static Main getInstance() {
        return instance;
    }
}
