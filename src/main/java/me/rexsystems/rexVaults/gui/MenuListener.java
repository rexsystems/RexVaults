package me.rexsystems.rexVaults.gui;

import me.rexsystems.rexVaults.RexVaults;
import me.rexsystems.rexVaults.util.SchedulerUtils;
import me.rexsystems.rexVaults.vault.MaterialBlacklist;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class MenuListener implements Listener {

    private final RexVaults plugin;
    private final SelectorMenu selector;
    private final MenuSounds sounds;

    public MenuListener(RexVaults plugin, SelectorMenu selector, MenuSounds sounds) {
        this.plugin = plugin;
        this.selector = selector;
        this.sounds = sounds;
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (holder instanceof SelectorHolder menu) {
            handleSelector(event, player, menu);
        } else if (holder instanceof VaultHolder vault) {
            handleVault(event, player, vault);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        Inventory top = event.getView().getTopInventory();
        InventoryHolder holder = top.getHolder();
        if (!(holder instanceof SelectorHolder) && !(holder instanceof VaultHolder)) {
            return;
        }
        if (!touchesTop(event, top.getSize())) {
            return;
        }
        if (holder instanceof SelectorHolder) {
            event.setCancelled(true);
            return;
        }
        ItemStack dragged = event.getOldCursor();
        if (blocked(dragged)) {
            event.setCancelled(true);
            deny(player);
            return;
        }
        VaultHolder vault = (VaultHolder) holder;
        VaultSessions.OpenVault session = plugin.sessions().get(vault.owner(), vault.number());
        if (session != null) {
            saveSoon(player, session);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        if (!(event.getInventory().getHolder() instanceof VaultHolder vault)) {
            return;
        }
        VaultSessions.OpenVault session = plugin.sessions().get(vault.owner(), vault.number());
        if (session == null || session.isClosing() || !session.viewer().equals(player.getUniqueId())) {
            return;
        }
        session.markClosing();
        byte[] data = session.capture();
        plugin.sessions().unlock(vault.owner(), vault.number(), player.getUniqueId());
        if (data == null) {
            plugin.getLogger().warning("Vault " + vault.number() + " for " + vault.owner() + " closed without a new save.");
        } else {
            plugin.repository().saveContentsAndWait(vault.owner(), vault.number(), data);
        }
        sounds.play(player, "close");
    }

    private void handleSelector(InventoryClickEvent event, Player player, SelectorHolder menu) {
        Inventory clicked = event.getClickedInventory();
        if (clicked == null) {
            return;
        }
        if (clicked.getHolder() != event.getView().getTopInventory().getHolder()) {
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(true);
            }
            return;
        }
        event.setCancelled(true);
        int slot = event.getSlot();
        if (slot == menu.previousSlot() && menu.hasPrevious()) {
            menu.showPage(menu.page() - 1);
            selector.loadIcons(player, menu);
            return;
        }
        if (slot == menu.nextSlot() && menu.hasNext()) {
            menu.showPage(menu.page() + 1);
            selector.loadIcons(player, menu);
            return;
        }
        Integer vault = menu.vaultAt(slot);
        if (vault == null) {
            return;
        }
        ItemStack cursor = event.getCursor();
        if (cursor != null && !cursor.getType().isAir()) {
            selector.setIcon(player, menu, slot, vault, cursor.getType());
            return;
        }
        if (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
            ItemStack hotbar = hotbarItem(player, event);
            if (hotbar != null && !hotbar.getType().isAir()) {
                selector.setIcon(player, menu, slot, vault, hotbar.getType());
            }
            return;
        }
        ClickType click = event.getClick();
        if (click == ClickType.LEFT || click == ClickType.RIGHT || click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT) {
            selector.openVault(player, vault);
        }
    }

    private void handleVault(InventoryClickEvent event, Player player, VaultHolder vault) {
        VaultSessions.OpenVault session = plugin.sessions().get(vault.owner(), vault.number());
        if (session == null || session.isClosing()) {
            return;
        }
        ItemStack incoming = incoming(event, player);
        if (blocked(incoming)) {
            event.setCancelled(true);
            deny(player);
            return;
        }
        if (changesVault(event)) {
            saveSoon(player, session);
        }
    }

    private void saveSoon(Player player, VaultSessions.OpenVault session) {
        SchedulerUtils.runForPlayer(plugin, player, () -> {
            if (session.isClosing()) {
                return;
            }
            byte[] data = session.capture();
            if (data == null) {
                plugin.getLogger().warning("Could not serialize vault " + session.number() + " for " + session.owner() + ".");
                return;
            }
            plugin.repository().saveContents(session.owner(), session.number(), data);
        });
    }

    private void deny(Player player) {
        plugin.send(player, "blacklist", Map.of());
        sounds.play(player, "denied");
    }

    private boolean blocked(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }
        MaterialBlacklist blacklist = plugin.getConfigManager().blacklist();
        return blacklist.blocked(item.getType().name());
    }

    private ItemStack incoming(InventoryClickEvent event, Player player) {
        Inventory clicked = event.getClickedInventory();
        if (clicked == null) {
            return null;
        }
        Inventory top = event.getView().getTopInventory();
        boolean topClick = clicked.getHolder() == top.getHolder();
        InventoryAction action = event.getAction();
        if (topClick) {
            return switch (action) {
                case PLACE_ALL, PLACE_ONE, PLACE_SOME, SWAP_WITH_CURSOR -> event.getCursor();
                case HOTBAR_SWAP, HOTBAR_MOVE_AND_READD -> hotbarItem(player, event);
                default -> null;
            };
        }
        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            return event.getCurrentItem();
        }
        return null;
    }

    private boolean changesVault(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        if (clicked == null) {
            return false;
        }
        Inventory top = event.getView().getTopInventory();
        if (clicked.getHolder() == top.getHolder()) {
            return true;
        }
        InventoryAction action = event.getAction();
        return action == InventoryAction.MOVE_TO_OTHER_INVENTORY || action == InventoryAction.COLLECT_TO_CURSOR;
    }

    private static boolean touchesTop(InventoryDragEvent event, int topSize) {
        for (int raw : event.getRawSlots()) {
            if (raw >= 0 && raw < topSize) {
                return true;
            }
        }
        return false;
    }

    private static ItemStack hotbarItem(Player player, InventoryClickEvent event) {
        int slot = event.getHotbarButton();
        if (slot >= 0 && slot < 9) {
            return player.getInventory().getItem(slot);
        }
        if (slot == 40 || event.getClick() == ClickType.SWAP_OFFHAND) {
            return player.getInventory().getItemInOffHand();
        }
        return null;
    }
}
