package me.rexsystems.rexVaults.config;

import me.rexsystems.rexVaults.RexVaults;
import me.rexsystems.rexVaults.vault.MaterialBlacklist;
import me.rexsystems.rexVaults.vault.VaultLayout;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;

public class ConfigManager {

    private final RexVaults plugin;
    private FileConfiguration config;
    private MaterialBlacklist blacklist = new MaterialBlacklist(List.of());

    public ConfigManager(RexVaults plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        new ConfigAutoUpdater(plugin).update();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        this.blacklist = new MaterialBlacklist(config.getStringList("blacklist"));
    }

    public void reload() {
        load();
    }

    public String storageType() {
        String type = config.getString("storage.type", "sqlite");
        return type == null ? "" : type.trim();
    }

    public File databaseFile() {
        String name = config.getString("storage.file", "vaults.db");
        if (name == null || name.isBlank()) {
            name = "vaults.db";
        }
        File file = new File(name.trim());
        if (!file.isAbsolute()) {
            file = new File(plugin.getDataFolder(), name.trim());
        }
        return file;
    }

    public int vaultSize() {
        return VaultLayout.sizeFromRows(config.getInt("vault.rows", 6));
    }

    public int selectorSize() {
        return VaultLayout.sizeFromRows(config.getInt("selector.rows", 6));
    }

    public String vaultTitle() {
        return config.getString("vault.title", "<white>Vault {vault}");
    }

    public String selectorTitle() {
        return config.getString("selector.title", "<white>Vaults");
    }

    public String selectorVaultName() {
        return config.getString("selector.vault-name", "<white>Vault {vault}");
    }

    public List<String> selectorVaultLore() {
        List<String> lore = config.getStringList("selector.vault-lore");
        return lore == null ? List.of() : lore;
    }

    public Material defaultIcon() {
        return material(config.getString("selector.default-icon"), Material.CHEST);
    }

    public boolean fillerEnabled() {
        return config.getBoolean("selector.filler.enabled", true);
    }

    public Material fillerMaterial() {
        return material(config.getString("selector.filler.material"), Material.GRAY_STAINED_GLASS_PANE);
    }

    public String fillerName() {
        return config.getString("selector.filler.name", " ");
    }

    public int previousSlot() {
        return config.getInt("selector.previous.slot", 45);
    }

    public Material previousMaterial() {
        return material(config.getString("selector.previous.material"), Material.ARROW);
    }

    public String previousName() {
        return config.getString("selector.previous.name", "<gray>Previous");
    }

    public int nextSlot() {
        return config.getInt("selector.next.slot", 53);
    }

    public Material nextMaterial() {
        return material(config.getString("selector.next.material"), Material.ARROW);
    }

    public String nextName() {
        return config.getString("selector.next.name", "<gray>Next");
    }

    public MaterialBlacklist blacklist() {
        return blacklist;
    }

    public boolean soundEnabled(String key) {
        return config.getBoolean("sounds." + key + ".enabled", false);
    }

    public String soundName(String key) {
        return config.getString("sounds." + key + ".sound", "");
    }

    public float soundVolume(String key) {
        return (float) config.getDouble("sounds." + key + ".volume", 1.0);
    }

    public float soundPitch(String key) {
        return (float) config.getDouble("sounds." + key + ".pitch", 1.0);
    }

    public String message(String path) {
        return config.getString("messages." + path, "");
    }

    public String prefix() {
        String prefix = message("prefix");
        return prefix == null ? "" : prefix;
    }

    public String prefixed(String path) {
        String raw = message(path);
        if (raw == null) {
            return "";
        }
        return raw.replace("<prefix>", prefix()).replace("{prefix}", prefix());
    }

    public List<String> helpLines() {
        List<String> lines = config.getStringList("messages.help");
        return lines == null ? List.of() : lines;
    }

    private static Material material(String name, Material fallback) {
        if (name == null || name.isBlank()) {
            return fallback;
        }
        Material parsed = Material.matchMaterial(name.trim());
        if (parsed == null || parsed.isAir() || !parsed.isItem()) {
            return fallback;
        }
        return parsed;
    }
}
