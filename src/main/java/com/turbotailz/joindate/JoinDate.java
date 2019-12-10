package com.turbotailz.joindate;

import org.bukkit.plugin.java.JavaPlugin;

public final class JoinDate extends JavaPlugin {

    private static JoinDate instance;

    public static JoinDate getPlugin() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        this.getCommand("joindate").setExecutor(new Commands());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
