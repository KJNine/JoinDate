package com.turbotailz.DiversitySMP;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by turbotailz on 28/10/2016.
 */
public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Fired when the server enables the plugin

        // Register "joindate" command (set an instance of the command class as executor)
        this.getCommand("joindate").setExecutor(new Commands());
    }

    @Override
    public void onDisable() {
        // Fired when the server stops and disables all plugins

    }

}
