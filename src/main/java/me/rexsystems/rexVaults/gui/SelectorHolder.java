package me.rexsystems.rexVaults.gui;

import me.rexsystems.rexVaults.RexVaults;
import me.rexsystems.rexVaults.config.ConfigManager;
import me.rexsystems.rexVaults.util.Placeholders;
import me.rexsystems.rexVaults.util.TextParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SelectorHolder implements InventoryHolder {

    private final RexVaults plugin;
    private final Player player;
    private final int count;
    private final int size;
    private final int previousSlot;
    private final int nextSlot;
    private final List<Integer> contentSlots = new ArrayList<>();
    private final Map<Integer, Integer> slotToVault = new HashMap<>();
    private final Map<Integer, Integer> iconEpoch = new HashMap<>();
    private Inventory inventory;
    private int page;

    public SelectorHolder(RexVaults plugin, Player player, int count) {
        this.plugin = plugin;
        this.player = player;
        this.count = count;
        ConfigManager config = plugin.getConfigManager();
        this.size = config.selectorSize();
        int previous = config.previousSlot();
        int next = config.nextSlot();
        if (previous < 0 || previous >= size) {
            previous = -1;
        }
        if (next < 0 || next >= size || next == previous) {
            next = -1;
        }
        this.previousSlot = previous;
        this.nextSlot = next;
        for (int slot = 0; slot < size; slot++) {
            if (slot != previousSlot && slot != nextSlot) {
                contentSlots.add(slot);
            }
        }
    }

    public void open() {
        inventory = Bukkit.createInventory(this, size, title());
        render();
        player.openInventory(inventory);
    }

    public void showPage(int nextPage) {
        if (nextPage < 0 || nextPage >= pages()) {
            return;
        }
        this.page = nextPage;
        render();
    }

    public int page() {
        return page;
    }

    public int previousSlot() {
        return previousSlot;
    }

    public int nextSlot() {
        return nextSlot;
    }

    public boolean hasPrevious() {
        return page > 0;
    }

    public boolean hasNext() {
        return page + 1 < pages();
    }

    public Integer vaultAt(int slot) {
        return slotToVault.get(slot);
    }

    public Map<Integer, Integer> vaults() {
        return Map.copyOf(slotToVault);
    }

    public void refresh(int slot, int vault, Material icon) {
        bumpIcon(slot);
        inventory.setItem(slot, vaultItem(vault, icon));
    }

    public int iconEpoch(int slot) {
        return iconEpoch.getOrDefault(slot, 0);
    }

    public boolean sameIconEpoch(int slot, int epoch) {
        return iconEpoch(slot) == epoch;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void render() {
        slotToVault.clear();
        inventory.clear();
        ConfigManager config = plugin.getConfigManager();
        if (config.fillerEnabled()) {
            var filler = MenuItems.filler(config.fillerMaterial(), config.fillerName());
            for (int slot = 0; slot < size; slot++) {
                inventory.setItem(slot, filler);
            }
        }
        int pageSize = Math.max(1, contentSlots.size());
        int start = page * pageSize;
        for (int index = 0; index < contentSlots.size(); index++) {
            int vault = start + index + 1;
            if (vault > count) {
                break;
            }
            int slot = contentSlots.get(index);
            slotToVault.put(slot, vault);
            bumpIcon(slot);
            inventory.setItem(slot, vaultItem(vault, null));
        }
        if (previousSlot >= 0 && hasPrevious()) {
            inventory.setItem(previousSlot, MenuItems.button(config.previousMaterial(), config.previousName(), Map.of()));
        }
        if (nextSlot >= 0 && hasNext()) {
            inventory.setItem(nextSlot, MenuItems.button(config.nextMaterial(), config.nextName(), Map.of()));
        }
    }

    private org.bukkit.inventory.ItemStack vaultItem(int vault, Material override) {
        ConfigManager config = plugin.getConfigManager();
        Material icon = override == null ? config.defaultIcon() : override;
        Map<String, String> tokens = Map.of("vault", Integer.toString(vault));
        return MenuItems.vault(icon, config.selectorVaultName(), config.selectorVaultLore(), tokens);
    }

    public void paintStoredIcon(int slot, int vault, String stored) {
        refresh(slot, vault, MenuItems.icon(stored, plugin.getConfigManager().defaultIcon()));
    }

    private int bumpIcon(int slot) {
        int next = iconEpoch.getOrDefault(slot, 0) + 1;
        iconEpoch.put(slot, next);
        return next;
    }

    private int pages() {
        int pageSize = Math.max(1, contentSlots.size());
        return Math.max(1, (count + pageSize - 1) / pageSize);
    }

    private net.kyori.adventure.text.Component title() {
        String template = Placeholders.apply(player, plugin.getConfigManager().selectorTitle());
        return TextParser.parse(TextParser.apply(template, Map.of(
                "page", Integer.toString(page + 1),
                "count", Integer.toString(count)
        )));
    }
}
