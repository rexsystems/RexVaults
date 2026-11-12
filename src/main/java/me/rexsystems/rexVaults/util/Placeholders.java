package me.rexsystems.rexVaults.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Placeholders {

    private Placeholders() {
    }

    public static String apply(Player player, String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (player == null || Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return text;
        }
        return PapiBridge.apply(player, text);
    }
}
