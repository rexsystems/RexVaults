package me.rexsystems.rexVaults.vault;

import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VaultPermissions {

    private static final Pattern NODE = Pattern.compile("rexvaults\\.vaults?\\.(\\d+)");

    private VaultPermissions() {
    }

    public static int highest(Collection<String> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return 0;
        }
        int highest = 0;
        for (String node : nodes) {
            int number = number(node);
            if (number > highest) {
                highest = number;
            }
        }
        return highest;
    }

    private static int number(String node) {
        if (node == null) {
            return 0;
        }
        Matcher matcher = NODE.matcher(node.trim().toLowerCase(Locale.ROOT));
        if (!matcher.matches()) {
            return 0;
        }
        try {
            int parsed = Integer.parseInt(matcher.group(1));
            return Math.max(parsed, 0);
        } catch (NumberFormatException failure) {
            return 0;
        }
    }
}
