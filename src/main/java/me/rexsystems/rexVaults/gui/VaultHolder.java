package me.rexsystems.rexVaults.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public final class VaultHolder implements InventoryHolder {

    private final UUID owner;
    private final int number;
    private Inventory inventory;

    public VaultHolder(UUID owner, int number) {
        this.owner = owner;
        this.number = number;
    }

    public UUID owner() {
        return owner;
    }

    public int number() {
        return number;
    }

    public void attach(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
