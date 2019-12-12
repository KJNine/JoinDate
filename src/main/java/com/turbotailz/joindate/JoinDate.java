package com.turbotailz.joindate;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class JoinDate extends JavaPlugin {

    private static JoinDate instance;

    public static JoinDate getPlugin() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        createConfig();
        this.getCommand("joindate").setExecutor(new Commands());

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholder(this).register();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void createConfig() {
        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            getPlugin().saveDefaultConfig();
        }
    }
}
