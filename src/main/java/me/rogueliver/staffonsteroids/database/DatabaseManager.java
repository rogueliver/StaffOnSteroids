package me.rogueliver.staffonsteroids.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import me.rogueliver.staffonsteroids.StaffOnSteroids;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter
public class DatabaseManager {

    private final StaffOnSteroids plugin;
    private HikariDataSource dataSource;

    private final Set<UUID> staffChatEnabled = new HashSet<>();
    private final Set<UUID> vanishedPlayers = new HashSet<>();
    private final Set<UUID> frozenPlayers = new HashSet<>();
    private final Set<UUID> mutedPlayers = new HashSet<>();
    private final Set<String> mutedIps = new HashSet<>();

    public DatabaseManager(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }

    public boolean initialize() {
        try {
            setupDataSource();
            createTables();
            loadData();
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
            return false;
        }
    }

    private void setupDataSource() {
        HikariConfig config = new HikariConfig();

        String host = plugin.getConfigManager().getDatabaseHost();
        int port = plugin.getConfigManager().getDatabasePort();
        String database = plugin.getConfigManager().getDatabaseName();
        String username = plugin.getConfigManager().getDatabaseUsername();
        String password = plugin.getConfigManager().getDatabasePassword();

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&serverTimezone=UTC");
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(plugin.getConfigManager().getPoolSize());
        config.setConnectionTimeout(plugin.getConfigManager().getConnectionTimeout());
        config.setIdleTimeout(plugin.getConfigManager().getIdleTimeout());
        config.setMaxLifetime(plugin.getConfigManager().getMaxLifetime());
        config.setLeakDetectionThreshold(60000);

        dataSource = new HikariDataSource(config);
    }

    private void createTables() throws SQLException {
        String[] tables = {
                """
            CREATE TABLE IF NOT EXISTS staff_data (
                uuid VARCHAR(36) PRIMARY KEY,
                staff_chat BOOLEAN DEFAULT FALSE,
                vanished BOOLEAN DEFAULT FALSE,
                frozen BOOLEAN DEFAULT FALSE,
                muted BOOLEAN DEFAULT FALSE,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS ip_mutes (
                ip VARCHAR(45) PRIMARY KEY,
                reason TEXT,
                muted_by VARCHAR(36),
                muted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                expires_at BIGINT DEFAULT NULL
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS temp_mutes (
                uuid VARCHAR(36) PRIMARY KEY,
                reason TEXT,
                muted_by VARCHAR(36),
                muted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                expires_at BIGINT NOT NULL
            )
            """
        };

        try (Connection connection = dataSource.getConnection()) {
            for (String table : tables) {
                try (PreparedStatement statement = connection.prepareStatement(table)) {
                    statement.executeUpdate();
                }
            }
        }
    }

    private void loadData() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT uuid, staff_chat, vanished, frozen, muted FROM staff_data")) {

                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    UUID uuid = UUID.fromString(result.getString("uuid"));

                    if (result.getBoolean("staff_chat")) {
                        staffChatEnabled.add(uuid);
                    }
                    if (result.getBoolean("vanished")) {
                        vanishedPlayers.add(uuid);
                    }
                    if (result.getBoolean("frozen")) {
                        frozenPlayers.add(uuid);
                    }
                    if (result.getBoolean("muted")) {
                        mutedPlayers.add(uuid);
                    }
                }
            }

            try (PreparedStatement statement = connection.prepareStatement("SELECT ip FROM ip_mutes")) {
                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    mutedIps.add(result.getString("ip"));
                }
            }
        }
    }

    public CompletableFuture<Void> savePlayerData(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        """
                        INSERT INTO staff_data (uuid, staff_chat, vanished, frozen, muted) 
                        VALUES (?, ?, ?, ?, ?) 
                        ON DUPLICATE KEY UPDATE 
                        staff_chat = VALUES(staff_chat),
                        vanished = VALUES(vanished),
                        frozen = VALUES(frozen),
                        muted = VALUES(muted)
                        """)) {

                    statement.setString(1, uuid.toString());
                    statement.setBoolean(2, staffChatEnabled.contains(uuid));
                    statement.setBoolean(3, vanishedPlayers.contains(uuid));
                    statement.setBoolean(4, frozenPlayers.contains(uuid));
                    statement.setBoolean(5, mutedPlayers.contains(uuid));

                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save player data: " + e.getMessage());
            }
        });
    }

    public CompletableFuture<Void> saveIpMute(String ip, String reason, String mutedBy) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO ip_mutes (ip, reason, muted_by, expires_at) VALUES (?, ?, ?, NULL) ON DUPLICATE KEY UPDATE reason = VALUES(reason), expires_at = NULL")) {

                    statement.setString(1, ip);
                    statement.setString(2, reason);
                    statement.setString(3, mutedBy);

                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save IP mute: " + e.getMessage());
            }
        });
    }

    public CompletableFuture<Void> saveTempMute(UUID uuid, String reason, String mutedBy, long expiresAt) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO temp_mutes (uuid, reason, muted_by, expires_at) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE reason = VALUES(reason), expires_at = VALUES(expires_at)")) {

                    statement.setString(1, uuid.toString());
                    statement.setString(2, reason);
                    statement.setString(3, mutedBy);
                    statement.setLong(4, expiresAt);

                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save temp mute: " + e.getMessage());
            }
        });
    }

    public CompletableFuture<Void> saveTempIpMute(String ip, String reason, String mutedBy, long expiresAt) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO ip_mutes (ip, reason, muted_by, expires_at) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE reason = VALUES(reason), expires_at = VALUES(expires_at)")) {

                    statement.setString(1, ip);
                    statement.setString(2, reason);
                    statement.setString(3, mutedBy);
                    statement.setLong(4, expiresAt);

                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save temp IP mute: " + e.getMessage());
            }
        });
    }

    public void checkExpiredMutes() {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                long currentTime = System.currentTimeMillis();

                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT uuid FROM temp_mutes WHERE expires_at <= ?")) {
                    statement.setLong(1, currentTime);
                    ResultSet result = statement.executeQuery();

                    while (result.next()) {
                        UUID uuid = UUID.fromString(result.getString("uuid"));
                        mutedPlayers.remove(uuid);
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM temp_mutes WHERE expires_at <= ?")) {
                    statement.setLong(1, currentTime);
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT ip FROM ip_mutes WHERE expires_at IS NOT NULL AND expires_at <= ?")) {
                    statement.setLong(1, currentTime);
                    ResultSet result = statement.executeQuery();

                    while (result.next()) {
                        String ip = result.getString("ip");
                        mutedIps.remove(ip);
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM ip_mutes WHERE expires_at IS NOT NULL AND expires_at <= ?")) {
                    statement.setLong(1, currentTime);
                    statement.executeUpdate();
                }

            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to check expired mutes: " + e.getMessage());
            }
        });
    }

    public CompletableFuture<Void> removeTempMute(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM temp_mutes WHERE uuid = ?")) {
                    statement.setString(1, uuid.toString());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to remove temp mute: " + e.getMessage());
            }
        });
    }

    public CompletableFuture<Void> removeIpMute(String ip) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM ip_mutes WHERE ip = ?")) {
                    statement.setString(1, ip);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to remove IP mute: " + e.getMessage());
            }
        });
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}