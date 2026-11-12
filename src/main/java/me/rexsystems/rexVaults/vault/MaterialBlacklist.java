package me.rexsystems.rexVaults.vault;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class MaterialBlacklist {

    private final Set<String> blocked;

    public MaterialBlacklist(Collection<String> names) {
        Set<String> parsed = new HashSet<>();
        if (names != null) {
            for (String name : names) {
                if (name == null || name.isBlank()) {
                    continue;
                }
                parsed.add(name.trim().toUpperCase(Locale.ROOT));
            }
        }
        this.blocked = Set.copyOf(parsed);
    }

    public boolean blocked(String materialName) {
        if (materialName == null || materialName.isBlank()) {
            return false;
        }
        return blocked.contains(materialName.trim().toUpperCase(Locale.ROOT));
    }
}
