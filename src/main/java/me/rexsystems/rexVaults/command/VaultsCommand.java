package me.rexsystems.rexVaults.command;

import me.rexsystems.rexVaults.RexVaults;
import me.rexsystems.rexVaults.gui.SelectorMenu;
import me.rexsystems.rexVaults.gui.VaultMenu;
import me.rexsystems.rexVaults.util.SchedulerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class VaultsCommand implements CommandExecutor, TabCompleter {

    private final RexVaults plugin;
    private final SelectorMenu selector;
    private final VaultMenu vaults;

    public VaultsCommand(RexVaults plugin, SelectorMenu selector, VaultMenu vaults) {
        this.plugin = plugin;
        this.selector = selector;
        this.vaults = vaults;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            return help(sender);
        }
        return switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> reload(sender);
            case "open" -> open(sender, args);
            case "list" -> list(sender);
            case "admin" -> admin(sender, args);
            default -> help(sender);
        };
    }

    private boolean help(CommandSender sender) {
        plugin.send(sender, "help-header", Map.of());
        for (String line : plugin.getConfigManager().helpLines()) {
            plugin.sendRaw(sender, line);
        }
        return true;
    }

    private boolean reload(CommandSender sender) {
        if (!sender.hasPermission("rexvaults.reload")) {
            plugin.send(sender, "no-permission", Map.of());
            return true;
        }
        plugin.reloadPlugin();
        plugin.send(sender, "reload", Map.of());
        return true;
    }

    private boolean open(CommandSender sender, String[] args) {
        if (!sender.hasPermission("rexvaults.use")) {
            plugin.send(sender, "no-permission", Map.of());
            return true;
        }
        if (!(sender instanceof Player player)) {
            plugin.send(sender, "players-only", Map.of());
            return true;
        }
        if (args.length < 2) {
            selector.open(player);
            return true;
        }
        Integer number = vaultNumber(args[1]);
        if (number == null) {
            plugin.send(sender, "invalid-vault", Map.of());
            return true;
        }
        vaults.openOwn(player, number);
        return true;
    }

    private boolean list(CommandSender sender) {
        if (!sender.hasPermission("rexvaults.use")) {
            plugin.send(sender, "no-permission", Map.of());
            return true;
        }
        if (!(sender instanceof Player player)) {
            plugin.send(sender, "players-only", Map.of());
            return true;
        }
        int count = plugin.vaultCount(player);
        if (count <= 0) {
            plugin.send(sender, "no-vaults", Map.of());
            return true;
        }
        plugin.send(sender, "list", Map.of("count", Integer.toString(count)));
        return true;
    }

    private boolean admin(CommandSender sender, String[] args) {
        if (!sender.hasPermission("rexvaults.admin")) {
            plugin.send(sender, "no-permission", Map.of());
            return true;
        }
        if (args.length < 4 || !args[1].equalsIgnoreCase("open")) {
            plugin.send(sender, "admin-usage", Map.of());
            return true;
        }
        if (!(sender instanceof Player admin)) {
            plugin.send(sender, "players-only", Map.of());
            return true;
        }
        Integer number = vaultNumber(args[3]);
        if (number == null) {
            plugin.send(sender, "invalid-vault", Map.of());
            return true;
        }
        resolve(admin, args[2], offline -> {
            if (!admin.isOnline()) {
                return;
            }
            if (offline == null || (!offline.isOnline() && !offline.hasPlayedBefore())) {
                plugin.send(admin, "unknown-player", Map.of("player", args[2]));
                return;
            }
            String name = offline.getName() == null ? args[2] : offline.getName();
            vaults.open(admin, offline.getUniqueId(), number, name, true);
        });
        return true;
    }

    private void resolve(Player admin, String name, java.util.function.Consumer<OfflinePlayer> callback) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getName().equalsIgnoreCase(name)) {
                callback.accept(online);
                return;
            }
        }
        OfflinePlayer cached = Bukkit.getOfflinePlayerIfCached(name);
        if (cached != null) {
            callback.accept(cached);
            return;
        }
        Bukkit.getAsyncScheduler().runNow(plugin, scheduled -> {
            OfflinePlayer offline;
            try {
                offline = Bukkit.getOfflinePlayer(name);
            } catch (RuntimeException failure) {
                offline = null;
            }
            OfflinePlayer resolved = offline;
            SchedulerUtils.runForPlayer(plugin, admin, () -> callback.accept(resolved));
        });
    }

    private static Integer vaultNumber(String text) {
        try {
            int number = Integer.parseInt(text);
            return number >= 1 ? number : null;
        } catch (NumberFormatException failure) {
            return null;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>(List.of("help", "open", "list", "reload"));
            if (sender.hasPermission("rexvaults.admin")) {
                options.add("admin");
            }
            return filter(options, args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("open") && sender instanceof Player player) {
            int count = Math.min(plugin.vaultCount(player), 54);
            List<String> numbers = new ArrayList<>();
            for (int number = 1; number <= count; number++) {
                numbers.add(Integer.toString(number));
            }
            return filter(numbers, args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("admin") && sender.hasPermission("rexvaults.admin")) {
            return filter(List.of("open"), args[1]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("open") && sender.hasPermission("rexvaults.admin")) {
            List<String> names = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                names.add(player.getName());
            }
            return filter(names, args[2]);
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("open") && sender.hasPermission("rexvaults.admin")) {
            return filter(List.of("1", "2", "3"), args[3]);
        }
        return List.of();
    }

    private static List<String> filter(List<String> options, String prefix) {
        String needle = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase(Locale.ROOT).startsWith(needle)) {
                matches.add(option);
            }
        }
        return matches;
    }
}
