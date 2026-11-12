package me.rexsystems.rexVaults.gui;

import me.rexsystems.rexVaults.util.TextParser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class MenuItems {

    private MenuItems() {
    }

    public static ItemStack button(Material material, String name, Map<String, String> tokens) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(TextParser.parse(TextParser.apply(name, tokens)));
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack filler(Material material, String name) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        if (name == null || name.isBlank()) {
            meta.setHideTooltip(true);
        } else {
            meta.displayName(TextParser.parse(name));
        }
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack vault(Material material, String name, List<String> lore, Map<String, String> tokens) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(TextParser.parse(TextParser.apply(name, tokens)));
        if (lore != null && !lore.isEmpty()) {
            List<net.kyori.adventure.text.Component> lines = new ArrayList<>();
            for (String line : lore) {
                if (line == null) {
                    continue;
                }
                lines.add(TextParser.parse(TextParser.apply(line, tokens)));
            }
            meta.lore(lines);
        }
        stack.setItemMeta(meta);
        return stack;
    }

    public static Material icon(String stored, Material fallback) {
        if (stored == null || stored.isBlank()) {
            return fallback;
        }
        Material parsed = Material.matchMaterial(stored.trim());
        if (parsed == null || parsed.isAir() || !parsed.isItem()) {
            return fallback;
        }
        return parsed;
    }

    public static String pretty(Material material) {
        String[] parts = material.name().toLowerCase(Locale.ROOT).split("_");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        return builder.toString();
    }
}
