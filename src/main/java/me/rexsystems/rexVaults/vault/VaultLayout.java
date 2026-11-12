package me.rexsystems.rexVaults.vault;

public final class VaultLayout {

    public static final int MAX_SLOTS = 54;
    public static final int ROW = 9;

    private VaultLayout() {
    }

    public static int snapSize(int requested) {
        if (requested <= ROW) {
            return ROW;
        }
        int capped = Math.min(requested, MAX_SLOTS);
        int snapped = (capped / ROW) * ROW;
        return Math.max(ROW, snapped);
    }

    public static int sizeFromRows(int rows) {
        if (rows <= 0) {
            return snapSize(0);
        }
        long slots = (long) rows * ROW;
        if (slots > Integer.MAX_VALUE) {
            return MAX_SLOTS;
        }
        return snapSize((int) slots);
    }
}
