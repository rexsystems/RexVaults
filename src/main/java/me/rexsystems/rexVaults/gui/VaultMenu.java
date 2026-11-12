package me.rexsystems.rexVaults.gui;

import me.rexsystems.rexVaults.RexVaults;
import me.rexsystems.rexVaults.storage.LoadResult;
import me.rexsystems.rexVaults.util.Placeholders;
import me.rexsystems.rexVaults.util.SchedulerUtils;
import me.rexsystems.rexVaults.util.TextParser;
import me.rexsystems.rexVaults.vault.ItemBlob;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public final class VaultMenu {

    private final RexVaults plugin;
    private final MenuSounds sounds;

    public VaultMenu(RexVaults plugin, MenuSounds sounds) {
        this.plugin = plugin;
        this.sounds = sounds;
    }

    public void openOwn(Player player, int number) {
        int count = plugin.vaultCount(player);
        if (count <= 0) {
            plugin.send(player, "no-vaults", Map.of());
            return;
        }
        if (number > count) {
            plugin.send(player, "vault-missing", Map.of("vault", Integer.toString(number)));
            return;
        }
        open(player, player.getUniqueId(), number, player.getName(), false);
    }

    public void open(Player viewer, UUID owner, int number, String ownerName, boolean admin) {
        if (!viewer.isOnline()) {
            return;
        }
        if (number < 1) {
            plugin.send(viewer, "invalid-vault", Map.of());
            return;
        }
        if (!plugin.sessions().tryLock(owner, number, viewer.getUniqueId())) {
            plugin.send(viewer, "in-use", Map.of(
                    "vault", Integer.toString(number),
                    "player", ownerName == null ? "" : ownerName
            ));
            return;
        }
        plugin.repository().load(owner, number, result -> SchedulerUtils.runForPlayer(plugin, viewer, () ->
                show(viewer, owner, number, ownerName, admin, result)));
    }

    private void show(Player viewer, UUID owner, int number, String ownerName, boolean admin, LoadResult result) {
        if (!plugin.isEnabled() || !viewer.isOnline() || !plugin.sessions().heldBy(owner, number, viewer.getUniqueId())) {
            plugin.sessions().unlock(owner, number, viewer.getUniqueId());
            return;
        }
        if (!result.ok()) {
            plugin.sessions().unlock(owner, number, viewer.getUniqueId());
            plugin.send(viewer, "load-failed", Map.of("vault", Integer.toString(number)));
            return;
        }
        ItemStack[] stored;
        try {
            stored = ItemBlob.read(result.contents());
        } catch (RuntimeException failure) {
            plugin.getLogger().warning("Could not read vault " + number + " for " + owner + ": " + failure.getMessage());
            plugin.sessions().unlock(owner, number, viewer.getUniqueId());
            plugin.send(viewer, "load-failed", Map.of("vault", Integer.toString(number)));
            return;
        }
        int size = plugin.getConfigManager().vaultSize();
        ItemStack[] shown = new ItemStack[size];
        int fit = Math.min(size, stored.length);
        if (fit > 0) {
            System.arraycopy(stored, 0, shown, 0, fit);
        }
        ItemStack[] overflow = stored.length > size
                ? Arrays.copyOfRange(stored, size, stored.length)
                : new ItemStack[0];

        VaultHolder holder = new VaultHolder(owner, number);
        String template = Placeholders.apply(viewer, plugin.getConfigManager().vaultTitle());
        Inventory inventory = Bukkit.createInventory(holder, size, TextParser.parse(TextParser.apply(template, Map.of(
                "vault", Integer.toString(number),
                "player", ownerName == null ? "" : ownerName
        ))));
        holder.attach(inventory);
        inventory.setContents(shown);

        VaultSessions.OpenVault session = plugin.sessions().get(owner, number);
        if (session == null) {
            return;
        }
        session.attach(inventory, overflow);
        viewer.openInventory(inventory);
        sounds.play(viewer, "open");
        if (admin) {
            plugin.send(viewer, "admin-opened", Map.of(
                    "vault", Integer.toString(number),
                    "player", ownerName == null ? "" : ownerName
            ));
        }
    }
}
