package com.turbotailz.joindate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.swing.text.html.Option;
import java.text.SimpleDateFormat;
import java.util.*;

public class Commands implements CommandExecutor {

    private JoinDate plugin = JoinDate.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (cmd.getName().equalsIgnoreCase("joindate")) {
            if (sender instanceof Player && !sender.hasPermission("joindate.check")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("no-permission")));

                return true;
            }

            Player player = null;

            if (args.length == 0) {
                if (sender instanceof Player) {
                    player = (Player) sender;
                } else {
                    sender.sendMessage(plugin.getConfig().getString("console-warning"));

                    return true;
                }
            } else if (args.length == 1) {
                if (args[0].equals("reload")) {
                    if (sender instanceof Player && !sender.hasPermission("joindate.reload")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("no-permission-reload")));

                        return true;
                    }

                    plugin.reloadConfig();

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("reload-success")));

                    return true;
                } else {
                    if (sender instanceof Player && !sender.hasPermission("joindate.check.others")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("no-permission-others")));

                        return true;
                    }

                    String playerName = args[0];

                    if(playerName.startsWith("#") && (sender.hasPermission("joindate.bynumber") || !(sender instanceof Player))) {
                        // Lookup who is number x.
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            try {
                                int n = Integer.parseInt(playerName.substring(1));
                                Optional<UUID> uuid = plugin.getByNumber(n);
                                if(uuid.isPresent()) {
                                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid.get());
                                    sendJoinDate(sender, offlinePlayer);
                                } else {
                                    sender.sendMessage("Nobody has that player number.");
                                }
                            } catch(NumberFormatException e) {
                                sender.sendMessage("Invalid number '" + playerName + "'");
                            }
                        });
                        return true;
                    }

                    player = Bukkit.getPlayer(playerName);

                    if (player == null) {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

                            sendJoinDate(sender, offlinePlayer);
                        });
                    }
                }
            }

            if (player == null) return true;

            return sendJoinDate(sender, player);
        }

        return true;
    }

    private boolean sendJoinDate(CommandSender sender, Object player) {
        Date date = null;

        if (player instanceof Player) {
            date = new Date(((Player) player).getFirstPlayed());
        } else if (player instanceof OfflinePlayer) {
            long offlineDate = ((OfflinePlayer) player).getFirstPlayed();

            if (offlineDate == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("player-has-not-joined")));

                return true;
            }

            date = new Date(offlineDate);
        }

        String joinDate = new SimpleDateFormat(plugin.getConfig().getString("date-format"))
                .format(date);

        String string;
        String joinNumber;

        if (sender == player) {
            string = plugin.getConfig().getString("check-self");
            joinNumber = Integer.toString(plugin.getPlayerNumber(((Player) player).getUniqueId()));
        } else {
            String playerName = null;

            if (player instanceof Player) {
                playerName = ((Player) player).getName();
            } else if (player instanceof OfflinePlayer) {
                playerName = ((OfflinePlayer) player).getName();
            }

            if(playerName == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("player-has-not-joined")));
                return true;
            }

            string = plugin.getConfig().getString("check-other");
            string = string.replace("%p", playerName);
            joinNumber = Integer.toString(plugin.getPlayerNumber(((OfflinePlayer) player).getUniqueId()));
        }

        string = string.replace("%d", joinDate);
        string = string.replace("%n", joinNumber);
        string = ChatColor.translateAlternateColorCodes('&', string);

        sender.sendMessage(string);
        return true;
    }
}
