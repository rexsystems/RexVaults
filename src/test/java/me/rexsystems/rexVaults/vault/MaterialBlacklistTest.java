package me.rexsystems.rexVaults.vault;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaterialBlacklistTest {

    @Test
    void listedMaterialsAreBlockedRegardlessOfCase() {
        MaterialBlacklist blacklist = new MaterialBlacklist(List.of("bedrock", " BARRIER ", "", "  "));
        assertTrue(blacklist.blocked("BEDROCK"));
        assertTrue(blacklist.blocked("barrier"));
        assertTrue(blacklist.blocked(" Barrier "));
    }

    @Test
    void everythingElseCanBeStored() {
        MaterialBlacklist blacklist = new MaterialBlacklist(List.of("BEDROCK"));
        assertFalse(blacklist.blocked("CHEST"));
        assertFalse(blacklist.blocked("AIR"));
        assertFalse(blacklist.blocked(null));
        assertFalse(blacklist.blocked(""));
        assertFalse(blacklist.blocked("   "));
    }
}
