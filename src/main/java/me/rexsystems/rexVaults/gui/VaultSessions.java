package me.rexsystems.rexVaults.gui;

import me.rexsystems.rexVaults.RexVaults;
import me.rexsystems.rexVaults.vault.ItemBlob;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class VaultSessions {

    private final RexVaults plugin;
    private final ConcurrentHashMap<Key, OpenVault> open = new ConcurrentHashMap<>();

    public VaultSessions(RexVaults plugin) {
        this.plugin = plugin;
    }

    public boolean tryLock(UUID owner, int number, UUID viewer) {
        OpenVault session = new OpenVault(owner, number, viewer);
        return open.putIfAbsent(new Key(owner, number), session) == null;
    }

    public OpenVault get(UUID owner, int number) {
        return open.get(new Key(owner, number));
    }

    public boolean heldBy(UUID owner, int number, UUID viewer) {
        OpenVault session = get(owner, number);
        return session != null && session.viewer.equals(viewer);
    }

    public void unlock(UUID owner, int number, UUID viewer) {
        OpenVault session = get(owner, number);
        if (session != null && session.viewer.equals(viewer)) {
            open.remove(new Key(owner, number), session);
        }
    }

    public Collection<OpenVault> all() {
        return open.values();
    }

    public void clear() {
        open.clear();
    }

    public void saveAndCloseAll() {
        for (OpenVault session : open.values()) {
            session.closing = true;
            byte[] data = session.capture();
            if (data != null) {
                plugin.repository().saveContentsAndWait(session.owner, session.number, data);
            }
            org.bukkit.entity.Player viewer = plugin.getServer().getPlayer(session.viewer);
            if (viewer != null) {
                try {
                    viewer.closeInventory();
                } catch (Throwable ignored) {
                }
            }
        }
        open.clear();
    }

    public record Key(UUID owner, int number) {
    }

    public static final class OpenVault {
        private final UUID owner;
        private final UUID viewer;
        private final int number;
        private Inventory inventory;
        private ItemStack[] overflow = new ItemStack[0];
        private volatile boolean closing;

        private OpenVault(UUID owner, int number, UUID viewer) {
            this.owner = owner;
            this.number = number;
            this.viewer = viewer;
        }

        public UUID owner() {
            return owner;
        }

        public UUID viewer() {
            return viewer;
        }

        public int number() {
            return number;
        }

        public boolean isClosing() {
            return closing;
        }

        public void markClosing() {
            this.closing = true;
        }

        public void attach(Inventory inventory, ItemStack[] overflow) {
            this.inventory = inventory;
            this.overflow = overflow == null ? new ItemStack[0] : overflow;
        }

        public byte[] capture() {
            if (inventory == null) {
                return null;
            }
            try {
                return ItemBlob.write(combined());
            } catch (RuntimeException failure) {
                return null;
            }
        }

        private ItemStack[] combined() {
            ItemStack[] shown = inventory.getContents();
            ItemStack[] all = new ItemStack[shown.length + overflow.length];
            System.arraycopy(shown, 0, all, 0, shown.length);
            System.arraycopy(overflow, 0, all, shown.length, overflow.length);
            return all;
        }
    }
}
