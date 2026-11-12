package me.rexsystems.rexVaults.gui;

import me.rexsystems.rexVaults.RexVaults;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class MenuSounds {

    private final RexVaults plugin;
    private final Set<String> unknown = new HashSet<>();

    public MenuSounds(RexVaults plugin) {
        this.plugin = plugin;
    }

    public void play(Player player, String key) {
        if (player == null || !plugin.getConfigManager().soundEnabled(key)) {
            return;
        }
        String name = plugin.getConfigManager().soundName(key);
        Sound sound = resolve(name);
        if (sound == null) {
            if (name != null && !name.isBlank() && unknown.add(name)) {
                plugin.getLogger().warning("Unknown sound '" + name + "' for sounds." + key + ".");
            }
            return;
        }
        player.playSound(player.getLocation(), sound,
                plugin.getConfigManager().soundVolume(key),
                plugin.getConfigManager().soundPitch(key));
    }

    private static Sound resolve(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String trimmed = name.trim();
        if (trimmed.indexOf(':') >= 0) {
            NamespacedKey key = NamespacedKey.fromString(trimmed.toLowerCase(Locale.ROOT));
            return key == null ? null : Registry.SOUNDS.get(key);
        }
        try {
            return Sound.valueOf(trimmed.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException failure) {
            return Registry.SOUNDS.get(NamespacedKey.minecraft(trimmed.toLowerCase(Locale.ROOT)));
        }
    }
}
