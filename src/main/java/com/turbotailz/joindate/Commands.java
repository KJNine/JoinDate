package com.turbotailz.joindate;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getLogger;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (cmd.getName().equalsIgnoreCase("joindate")) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    String joinDate = new java.text.SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z").format(new java.util.Date(player.getFirstPlayed()));

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', ("&7&oYou joined the server on &a&o" + joinDate)));
                }
            }
        }

        return true;
    }
}
