package me.rexsystems.rexVaults.vault;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VaultPermissionsTest {

    @Test
    void highestNumberWinsAcrossBothNodeForms() {
        int highest = VaultPermissions.highest(Set.of(
                "rexvaults.use",
                "rexvaults.vault.2",
                "rexvaults.vaults.5",
                "rexvaults.vault.4",
                "rexvaults.vault.nope",
                "rexvaults.vault.2.extra",
                "not.a.vault.9",
                "  RexVaults.Vaults.7  "
        ));
        assertEquals(7, highest);
    }

    @Test
    void noNumberedNodeMeansZero() {
        assertEquals(0, VaultPermissions.highest(Set.of("rexvaults.use", "rexvaults.admin")));
        assertEquals(0, VaultPermissions.highest(Set.of()));
        assertEquals(0, VaultPermissions.highest(null));
    }

    @Test
    void zeroDoesNotGrantAVault() {
        assertEquals(0, VaultPermissions.highest(Set.of("rexvaults.vault.0")));
        assertEquals(3, VaultPermissions.highest(Set.of("rexvaults.vaults.3", "rexvaults.vault.0")));
    }
}
