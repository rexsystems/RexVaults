package me.rexsystems.rexVaults.vault;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VaultLayoutTest {

    @Test
    void sizeSnapsDownToAMultipleOfNineAndCapsAtFiftyFour() {
        assertEquals(9, VaultLayout.snapSize(0));
        assertEquals(9, VaultLayout.snapSize(1));
        assertEquals(9, VaultLayout.snapSize(9));
        assertEquals(9, VaultLayout.snapSize(10));
        assertEquals(18, VaultLayout.snapSize(20));
        assertEquals(27, VaultLayout.snapSize(27));
        assertEquals(45, VaultLayout.snapSize(53));
        assertEquals(54, VaultLayout.snapSize(54));
        assertEquals(54, VaultLayout.snapSize(60));
        assertEquals(54, VaultLayout.snapSize(999));
    }

    @Test
    void rowsBecomeSlotCount() {
        assertEquals(9, VaultLayout.sizeFromRows(0));
        assertEquals(9, VaultLayout.sizeFromRows(1));
        assertEquals(27, VaultLayout.sizeFromRows(3));
        assertEquals(54, VaultLayout.sizeFromRows(6));
        assertEquals(54, VaultLayout.sizeFromRows(8));
    }
}
