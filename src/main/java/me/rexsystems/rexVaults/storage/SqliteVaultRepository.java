package me.rexsystems.rexVaults.storage;

import me.rexsystems.rexVaults.RexVaults;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class SqliteVaultRepository {

    private final RexVaults plugin;
    private final ExecutorService io = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "RexVaults-SQLite");
        thread.setDaemon(true);
        return thread;
    });
    private Connection connection;

    public SqliteVaultRepository(RexVaults plugin) {
        this.plugin = plugin;
    }

    public boolean open(File file) {
        try {
            loadDriver();
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA journal_mode=WAL");
                statement.execute("PRAGMA synchronous=NORMAL");
                statement.execute("PRAGMA busy_timeout=5000");
                statement.execute("""
                        CREATE TABLE IF NOT EXISTS vaults (
                          owner TEXT NOT NULL,
                          vault_number INTEGER NOT NULL,
                          icon TEXT,
                          contents BLOB,
                          PRIMARY KEY (owner, vault_number)
                        )
                        """);
            }
            plugin.getLogger().info("Using sqlite database " + file.getName() + ".");
            return true;
        } catch (Exception failure) {
            plugin.getLogger().severe("Could not open vaults database: " + failure.getMessage());
            return false;
        }
    }

    public void loadIcon(UUID owner, int number, Consumer<String> callback) {
        io.execute(() -> {
            try {
                callback.accept(readIcon(owner, number));
            } catch (SQLException failure) {
                plugin.getLogger().warning("Could not load icon for vault " + number + " of " + owner + ": " + failure.getMessage());
            }
        });
    }

    public void load(UUID owner, int number, Consumer<LoadResult> callback) {
        io.execute(() -> {
            LoadResult result;
            try {
                result = read(owner, number);
            } catch (SQLException failure) {
                plugin.getLogger().warning("Could not load vault " + number + " for " + owner + ": " + failure.getMessage());
                result = LoadResult.failed();
            }
            callback.accept(result);
        });
    }

    public void saveContents(UUID owner, int number, byte[] contents) {
        if (contents == null) {
            return;
        }
        io.execute(() -> writeContents(owner, number, contents));
    }

    public void saveContentsAndWait(UUID owner, int number, byte[] contents) {
        if (contents == null) {
            return;
        }
        Future<?> future = io.submit(() -> writeContents(owner, number, contents));
        await(future, "save vault " + number);
    }

    public void saveIcon(UUID owner, int number, String icon) {
        if (icon == null || icon.isBlank()) {
            return;
        }
        io.execute(() -> writeIcon(owner, number, icon));
    }

    public void close() {
        io.shutdown();
        try {
            if (!io.awaitTermination(15, TimeUnit.SECONDS)) {
                io.shutdownNow();
            }
        } catch (InterruptedException interrupted) {
            io.shutdownNow();
            Thread.currentThread().interrupt();
        }
        if (connection == null) {
            return;
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA wal_checkpoint(FULL)");
        } catch (SQLException failure) {
            plugin.getLogger().warning("Could not checkpoint vaults database: " + failure.getMessage());
        }
        try {
            connection.close();
        } catch (SQLException failure) {
            plugin.getLogger().warning("Could not close vaults database: " + failure.getMessage());
        }
        connection = null;
    }

    private String readIcon(UUID owner, int number) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT icon FROM vaults WHERE owner = ? AND vault_number = ?")) {
            statement.setString(1, owner.toString());
            statement.setInt(2, number);
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    return null;
                }
                return rows.getString("icon");
            }
        }
    }

    private LoadResult read(UUID owner, int number) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT icon, contents FROM vaults WHERE owner = ? AND vault_number = ?")) {
            statement.setString(1, owner.toString());
            statement.setInt(2, number);
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    return LoadResult.missing();
                }
                return new LoadResult(true, rows.getString("icon"), rows.getBytes("contents"));
            }
        }
    }

    private void writeContents(UUID owner, int number, byte[] contents) {
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO vaults (owner, vault_number, icon, contents)
                VALUES (?, ?, NULL, ?)
                ON CONFLICT(owner, vault_number) DO UPDATE SET contents = excluded.contents
                """)) {
            statement.setString(1, owner.toString());
            statement.setInt(2, number);
            statement.setBytes(3, contents);
            statement.executeUpdate();
        } catch (SQLException failure) {
            plugin.getLogger().warning("Could not save vault " + number + " for " + owner + ": " + failure.getMessage());
        }
    }

    private void writeIcon(UUID owner, int number, String icon) {
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO vaults (owner, vault_number, icon, contents)
                VALUES (?, ?, ?, NULL)
                ON CONFLICT(owner, vault_number) DO UPDATE SET icon = excluded.icon
                """)) {
            statement.setString(1, owner.toString());
            statement.setInt(2, number);
            statement.setString(3, icon);
            statement.executeUpdate();
        } catch (SQLException failure) {
            plugin.getLogger().warning("Could not save icon for vault " + number + " of " + owner + ": " + failure.getMessage());
        }
    }

    private void await(Future<?> future, String action) {
        try {
            future.get(15, TimeUnit.SECONDS);
        } catch (Exception failure) {
            plugin.getLogger().warning("Could not " + action + ": " + failure.getMessage());
        }
    }

    private static void loadDriver() throws ClassNotFoundException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException relocated) {
            Class.forName("me.rexsystems.rexVaults.libs.JDBC");
        }
    }
}
