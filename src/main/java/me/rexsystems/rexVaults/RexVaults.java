package me.rexsystems.rexVaults;

import me.rexsystems.rexVaults.command.VaultsCommand;
import me.rexsystems.rexVaults.config.ConfigManager;
import me.rexsystems.rexVaults.gui.MenuListener;
import me.rexsystems.rexVaults.gui.MenuSounds;
import me.rexsystems.rexVaults.gui.SelectorMenu;
import me.rexsystems.rexVaults.gui.VaultMenu;
import me.rexsystems.rexVaults.gui.VaultSessions;
import me.rexsystems.rexVaults.papi.RexVaultsPlaceholders;
import me.rexsystems.rexVaults.storage.SqliteVaultRepository;
import me.rexsystems.rexVaults.util.MessageService;
import me.rexsystems.rexVaults.util.Placeholders;
import me.rexsystems.rexVaults.vault.VaultPermissions;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class RexVaults extends JavaPlugin {

    private ConfigManager configManager;
    private SqliteVaultRepository repository;
    private VaultSessions sessions;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.configManager.load();
        warnStorageType();

        this.repository = new SqliteVaultRepository(this);
        if (!this.repository.open(this.configManager.databaseFile())) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.sessions = new VaultSessions(this);

        MenuSounds sounds = new MenuSounds(this);
        VaultMenu vaults = new VaultMenu(this, sounds);
        SelectorMenu selector = new SelectorMenu(this, sounds, vaults);
        getServer().getPluginManager().registerEvents(new MenuListener(this, selector, sounds), this);

        VaultsCommand command = new VaultsCommand(this, selector, vaults);
        PluginCommand pluginCommand = getCommand("rexvaults");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new RexVaultsPlaceholders(this).register();
            getLogger().info("PlaceholderAPI expansion registered.");
        }

        getLogger().info("RexVaults enabled.");
    }

    @Override
    public void onDisable() {
        if (sessions != null) {
            sessions.saveAndCloseAll();
        }
        if (repository != null) {
            repository.close();
        }
        getLogger().info("RexVaults disabled.");
    }

    public void reloadPlugin() {
        configManager.reload();
        warnStorageType();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public SqliteVaultRepository repository() {
        return repository;
    }

    public VaultSessions sessions() {
        return sessions;
    }

    public int vaultCount(Player player) {
        if (player == null) {
            return 0;
        }
        Set<String> nodes = new HashSet<>();
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (info.getValue() && info.getPermission() != null) {
                nodes.add(info.getPermission());
            }
        }
        return VaultPermissions.highest(nodes);
    }

    public void send(CommandSender sender, String path, Map<String, String> tokens) {
        String raw = configManager.prefixed(path);
        if (sender instanceof Player player) {
            raw = Placeholders.apply(player, raw);
        }
        MessageService.send(sender, raw, tokens);
    }

    public void sendRaw(CommandSender sender, String raw) {
        if (sender instanceof Player player) {
            raw = Placeholders.apply(player, raw);
        }
        MessageService.send(sender, raw);
    }

    private void warnStorageType() {
        String type = configManager.storageType();
        if (type.isEmpty() || type.equalsIgnoreCase("sqlite")) {
            return;
        }
        getLogger().severe("Unknown storage.type '" + type + "'. Using sqlite.");
    }
}
