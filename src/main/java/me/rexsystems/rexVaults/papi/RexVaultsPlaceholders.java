package me.rexsystems.rexVaults.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.rexsystems.rexVaults.RexVaults;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class RexVaultsPlaceholders extends PlaceholderExpansion {

    private final RexVaults plugin;

    public RexVaultsPlaceholders(RexVaults plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "rexvaults";
    }

    @Override
    public @NotNull String getAuthor() {
        return "RexSystems";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        String key = params.toLowerCase(Locale.ROOT);
        if (!key.equals("count") && !key.equals("vaults")) {
            return null;
        }
        Player online = player == null ? null : player.getPlayer();
        if (online == null) {
            return "0";
        }
        return Integer.toString(plugin.vaultCount(online));
    }
}
