package me.rexsystems.rexVaults.config;

import me.rexsystems.rexVaults.RexVaults;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfigAutoUpdater {

    public static final int CURRENT_CONFIG_VERSION = 1;

    private final RexVaults plugin;

    public ConfigAutoUpdater(RexVaults plugin) {
        this.plugin = plugin;
    }

    public void update() {
        try {
            FileConfiguration userCfg = plugin.getConfig();
            InputStream resource = plugin.getResource("config.yml");
            if (resource == null) {
                plugin.getLogger().warning("Default config.yml resource not found; skipping auto-update.");
                return;
            }

            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(resource, StandardCharsets.UTF_8));

            int fromVersion = userCfg.getInt("config-version", 0);
            int missing = mergeMissingKeys(userCfg, defaults);
            if (missing <= 0 && fromVersion >= CURRENT_CONFIG_VERSION) {
                return;
            }

            if (userCfg.getInt("config-version", 0) < CURRENT_CONFIG_VERSION) {
                userCfg.set("config-version", CURRENT_CONFIG_VERSION);
            }

            backupConfig();
            plugin.saveConfig();
            plugin.getLogger().info("Config updated from v" + fromVersion + " to v" + CURRENT_CONFIG_VERSION
                    + (missing > 0 ? " (added " + missing + " missing key(s))." : "."));
        } catch (Throwable failure) {
            plugin.getLogger().warning("Config auto-update failed: " + failure.getMessage());
        }
    }

    private int mergeMissingKeys(FileConfiguration userCfg, YamlConfiguration defaults) {
        int count = 0;
        for (String key : defaults.getKeys(true)) {
            if (defaults.isConfigurationSection(key) || userCfg.isSet(key)) {
                continue;
            }
            userCfg.set(key, defaults.get(key));
            count++;
        }
        return count;
    }

    private void backupConfig() {
        try {
            File file = new File(plugin.getDataFolder(), "config.yml");
            if (!file.exists()) {
                return;
            }
            String stamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            Path backup = new File(plugin.getDataFolder(), "config.yml.bak." + stamp).toPath();
            Files.copy(file.toPath(), backup, StandardCopyOption.REPLACE_EXISTING);
        } catch (Throwable ignored) {
        }
    }
}
