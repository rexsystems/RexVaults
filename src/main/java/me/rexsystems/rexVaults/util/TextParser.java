package me.rexsystems.rexVaults.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextParser {

    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private static final Pattern HEX_AMP = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern HEX_HASH = Pattern.compile("(?<![<:])#([A-Fa-f0-9]{6})");

    private TextParser() {
    }

    public static String apply(String input, Map<String, String> placeholders) {
        String value = input == null ? "" : input;
        if (placeholders == null || placeholders.isEmpty()) {
            return value;
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String key = entry.getKey();
            if (key == null || key.isEmpty()) {
                continue;
            }
            String replacement = escapeValue(entry.getValue());
            value = value.replace("{" + key + "}", replacement);
            value = value.replace("%" + key + "%", replacement);
        }
        return value;
    }

    public static Component parse(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }
        String normalized = normalize(input);
        try {
            return MINI.deserialize(normalized);
        } catch (Exception ignored) {
            return LEGACY.deserialize(input.replace('§', '&'));
        }
    }

    public static String escapeValue(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return MINI.escapeTags(value).replace('&', '＆').replace('§', ' ');
    }

    static String normalize(String input) {
        String value = input.replace('§', '&');

        Matcher amp = HEX_AMP.matcher(value);
        StringBuilder ampOut = new StringBuilder();
        while (amp.find()) {
            amp.appendReplacement(ampOut, "<#" + amp.group(1) + ">");
        }
        amp.appendTail(ampOut);
        value = ampOut.toString();

        Matcher hash = HEX_HASH.matcher(value);
        StringBuilder hashOut = new StringBuilder();
        while (hash.find()) {
            hash.appendReplacement(hashOut, "<#" + hash.group(1) + ">");
        }
        hash.appendTail(hashOut);
        value = hashOut.toString();

        return value
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&l", "<bold>")
                .replace("&o", "<italic>")
                .replace("&n", "<underlined>")
                .replace("&m", "<strikethrough>")
                .replace("&k", "<obfuscated>")
                .replace("&r", "<reset>");
    }
}
