package com.turbotailz.joindate;

import com.google.gson.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JoinDate extends JavaPlugin implements Listener {

    private static JoinDate instance;
    public static JoinDate getPlugin() {
        return instance;
    }

    @Deprecated
    public final Map<UUID, Integer> jnMap = Collections.synchronizedMap(new HashMap<>());
    private final File df = new File(getDataFolder(), "data.json");
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

        synchronized (jnMap) {
            if(df.exists()) {
                try (FileReader fr = new FileReader(df)) {
                    JsonObject json = new JsonParser().parse(fr).getAsJsonObject();
                    jnMap.putAll(json.entrySet().stream()
                            .collect(Collectors.<Map.Entry<String, JsonElement>, UUID, Integer>toMap(
                                    e -> UUID.fromString(e.getKey()),
                                    e -> e.getValue().getAsInt())));
                } catch (Exception e) {
                    getLogger().log(Level.WARNING, "Exception loading Join-Numbers data file (proceeding with re-calculated data). ", e);
                }
            } else { // Calculate pre-existing players' join numbers.
                List<UUID> sorted = Stream.of(getServer().getOfflinePlayers())
                        .filter(OfflinePlayer::hasPlayedBefore)
                        .sorted(Comparator.comparingLong(OfflinePlayer::getFirstPlayed))
                        .map(OfflinePlayer::getUniqueId)
                        .distinct()
                        .collect(Collectors.toList());
                int num = 1;
                for(UUID u : sorted) {
                    jnMap.put(u, num);
                    num++;
                }
            }
        }
        autosaveTask = getServer().getScheduler().runTaskTimerAsynchronously(this, this::saveData, 1L, 18000L);
    }

    private void saveData() {
        synchronized (jnMap) {
            try (FileWriter fw = new FileWriter(df)) {
                Gson gson = new Gson();
                JsonObject jo = new JsonObject();
                jnMap.forEach((u, i) -> jo.add(u.toString(), new JsonPrimitive(i)));
                gson.toJson(jo, fw);
            } catch(IOException e) {
                getLogger().log(Level.WARNING, "Exception saving Join-Numbers data file. ", e);
            }
        }
    }

    public int getPlayerNumber(UUID uuid) {
        synchronized (jnMap) {
            return jnMap.get(uuid);
        }
    }

    public Optional<UUID> getByNumber(int playerNumber) {
        synchronized (jnMap) {
            return jnMap.entrySet().stream().filter(e -> e.getValue() == playerNumber).map(Map.Entry::getKey).findFirst();
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
                    String msg = getConfig().getString("first-join");
                    if(msg != null && !msg.isEmpty()) {
                        msg = msg.replace("%p", p.getDisplayName());
                        msg = msg.replace("%n", Integer.toString(jnMap.get(p.getUniqueId())));
                        msg = ChatColor.translateAlternateColorCodes('&', msg);
                        getServer().broadcastMessage(msg);
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
