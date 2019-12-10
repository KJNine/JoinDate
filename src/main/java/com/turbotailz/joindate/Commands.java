package com.turbotailz.joindate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (cmd.getName().equalsIgnoreCase("joindate")) {
            Player player = null;

            if (args.length == 0) {
                if (sender instanceof Player) {
                    player = (Player) sender;
                } else {
                    sender.sendMessage("Only in-game players can check their join date!");

                    return true;
                }
            } else if (args.length == 1) {
                String playerName = args[0];

                player = Bukkit.getPlayer(playerName);

                if (player == null) {
                    Bukkit.getScheduler().runTaskAsynchronously(JoinDate.getPlugin(), () -> {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

                        sendJoinDate(sender, offlinePlayer);
                    });
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
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&7That player has not joined the server before.")));

                return true;
            }

            date = new Date(offlineDate);
        }



        String joinDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z")
                .format(date);

        String string;

        if (sender == player) {
            string = ChatColor.translateAlternateColorCodes('&', ("&7You joined the server on &a" + joinDate));
        } else {
            String playerName = null;

            if (player instanceof Player) {
                playerName = ((Player) player).getName();
            } else if (player instanceof OfflinePlayer) {
                playerName = ((OfflinePlayer) player).getName();
            }

            string = ChatColor.translateAlternateColorCodes('&', ("&7" + playerName + " joined the server on &a" + joinDate));
        }

        sender.sendMessage(string);
        return true;
    }
}
