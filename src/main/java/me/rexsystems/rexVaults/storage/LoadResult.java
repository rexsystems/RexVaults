package me.rexsystems.rexVaults.storage;

public record LoadResult(boolean ok, String icon, byte[] contents) {

    public static LoadResult failed() {
        return new LoadResult(false, null, null);
    }

    public static LoadResult missing() {
        return new LoadResult(true, null, null);
    }
}
