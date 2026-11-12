package me.rexsystems.rexVaults.gui;

import me.rexsystems.rexVaults.RexVaults;
import me.rexsystems.rexVaults.util.SchedulerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;

public final class SelectorMenu {

    private final RexVaults plugin;
    private final MenuSounds sounds;
    private final VaultMenu vaults;

    public SelectorMenu(RexVaults plugin, MenuSounds sounds, VaultMenu vaults) {
        this.plugin = plugin;
        this.sounds = sounds;
        this.vaults = vaults;
    }

    public void open(Player player) {
        int count = plugin.vaultCount(player);
        if (count <= 0) {
            plugin.send(player, "no-vaults", Map.of());
            return;
        }
        SelectorHolder holder = new SelectorHolder(plugin, player, count);
        holder.open();
        loadIcons(player, holder);
        sounds.play(player, "open");
    }

    public void loadIcons(Player player, SelectorHolder holder) {
        holder.vaults().forEach((slot, vault) -> {
            int epoch = holder.iconEpoch(slot);
            plugin.repository().loadIcon(player.getUniqueId(), vault, icon ->
                    SchedulerUtils.runForPlayer(plugin, player, () -> applyIcon(player, holder, slot, vault, icon, epoch)));
        });
    }

    public void setIcon(Player player, SelectorHolder holder, int slot, int vault, Material material) {
        holder.refresh(slot, vault, material);
        plugin.repository().saveIcon(player.getUniqueId(), vault, material.name());
        plugin.send(player, "icon-set", Map.of(
                "vault", Integer.toString(vault),
                "icon", MenuItems.pretty(material)
        ));
        sounds.play(player, "icon");
    }

    public void openVault(Player player, int vault) {
        vaults.openOwn(player, vault);
    }

    private void applyIcon(Player player, SelectorHolder holder, int slot, int vault, String stored, int epoch) {
        if (!player.isOnline() || holder.getInventory() == null || !holder.sameIconEpoch(slot, epoch)) {
            return;
        }
        if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof SelectorHolder open) || open != holder) {
            return;
        }
        if (!Objects.equals(holder.vaultAt(slot), vault)) {
            return;
        }
        holder.paintStoredIcon(slot, vault, stored);
    }
}
