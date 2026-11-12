package me.rexsystems.rexVaults.vault;

import org.bukkit.inventory.ItemStack;

public final class ItemBlob {

    private ItemBlob() {
    }

    public static byte[] write(ItemStack[] contents) {
        ItemStack[] safe = new ItemStack[contents == null ? 0 : contents.length];
        for (int index = 0; index < safe.length; index++) {
            ItemStack item = contents[index];
            if (item == null || item.isEmpty()) {
                safe[index] = ItemStack.empty();
            } else {
                safe[index] = item.clone();
            }
        }
        return ItemStack.serializeItemsAsBytes(safe);
    }

    public static ItemStack[] read(byte[] data) {
        if (data == null || data.length == 0) {
            return new ItemStack[0];
        }
        ItemStack[] items = ItemStack.deserializeItemsFromBytes(data);
        return items == null ? new ItemStack[0] : items;
    }
}
