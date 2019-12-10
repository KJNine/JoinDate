package com.turbotailz.joindate;

import org.bukkit.plugin.java.JavaPlugin;

public final class JoinDate extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("joindate").setExecutor(new Commands());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
