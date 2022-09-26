package com.turbotailz.joindate;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JoinDate extends JavaPlugin implements Listener {

    private static JoinDate instance;
    public static JoinDate getPlugin() {
        return instance;
    }

    public HashMap<UUID, Integer> jnMap;
    private File df = new File(getDataFolder(), "data.bin"); // apparently java doesn't store it in human-readable format
    private BukkitTask autosaveTask;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        createConfig();
        this.getCommand("joindate").setExecutor(new Commands());

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholder(this).register();
        }

        getServer().getPluginManager().registerEvents(this, this);

        if(df.exists()) {
            try (FileInputStream fis = new FileInputStream(df); ObjectInputStream ois = new ObjectInputStream(fis)) {
                jnMap = (HashMap<UUID, Integer>) ois.readObject();
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Exception loading Join-Numbers data file (proceeding with re-calculated data). ", e);
            }
        }
        if(jnMap == null) { // Calculate pre-existing players' join numbers.
            jnMap = new HashMap<>();
            List<UUID> sorted = Stream.of(getServer().getOfflinePlayers())
                    .filter(OfflinePlayer::hasPlayedBefore)
                    .sorted(Comparator.comparingLong(OfflinePlayer::getFirstPlayed))
                    .map(OfflinePlayer::getUniqueId)
                    .distinct()
                    .collect(Collectors.toList());
            sorted.forEach(u -> jnMap.put(u, sorted.indexOf(u)+1));
        }
        autosaveTask = getServer().getScheduler().runTaskTimerAsynchronously(this, this::saveData, 1L, 18000L);
    }

    public void saveData() {
        synchronized (jnMap) {
            try (FileOutputStream fos = new FileOutputStream(df); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(jnMap);
                oos.flush();
            } catch(IOException e) {
                getLogger().log(Level.WARNING, "Exception saving Join-Numbers data file. ", e);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if(!p.hasPlayedBefore()) {
            getServer().getScheduler().runTaskAsynchronously(this, () -> {
                synchronized (jnMap) {
                    if(!jnMap.containsKey(p.getUniqueId())) {
                        jnMap.put(p.getUniqueId(), jnMap.size()+1);
                    }
                }
            });
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        autosaveTask.cancel();
        saveData();
    }

    private void createConfig() {
        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            getPlugin().saveDefaultConfig();
        }
    }
}
