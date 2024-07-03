package com.turbotailz.joindate;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Placeholder extends PlaceholderExpansion {

    private JoinDate plugin;

    public Placeholder(JoinDate plugin) {
        this.plugin = plugin;
    }

    public boolean persist() {
        return true;
    }

    public boolean canRegister() {
        return true;
    }

    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    public String getIdentifier() {
        return "joindate";
    }

    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("join_date")) {
            long longDate = player.getFirstPlayed();

            if (longDate > 0) {
                Date date = new Date(player.getFirstPlayed());
                return getJoinDate(date);
            }
        }
        if(identifier.equals("join_number")) {
            int joinNumber = plugin.getPlayerNumber(player.getUniqueId());
            return Integer.toString(joinNumber);
        }

        return null;
    }

    public String getJoinDate(Date date) {
        return new SimpleDateFormat(plugin.getConfig().getString("date-format"))
                .format(date);
    }
}
