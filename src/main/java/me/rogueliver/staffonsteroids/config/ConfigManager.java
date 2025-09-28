package me.rogueliver.staffonsteroids.config;

import lombok.Getter;
import me.rogueliver.staffonsteroids.StaffOnSteroids;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ConfigManager {
    
    private final StaffOnSteroids plugin;
    private FileConfiguration config;
    private FileConfiguration messages;
    private final Map<String, String> messageCache = new HashMap<>();
    
    public ConfigManager(StaffOnSteroids plugin) {
        this.plugin = plugin;
        loadConfigs();
        cacheMessages();
    }
    
    private void loadConfigs() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    private void cacheMessages() {
        cacheSection("", messages);
    }
    
    private void cacheSection(String prefix, org.bukkit.configuration.ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            
            if (section.isConfigurationSection(key)) {
                cacheSection(fullKey, section.getConfigurationSection(key));
            } else {
                String message = section.getString(key);
                if (message != null) {
                    messageCache.put(fullKey, ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        }
    }
    
    public String getMessage(String key) {
        return messageCache.getOrDefault(key, "Message not found: " + key);
    }
    
    public String getMessage(String key, String... replacements) {
        String message = getMessage(key);
        
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
            }
        }
        
        return message;
    }
    
    public String getPrefix() {
        return getMessage("prefix");
    }
    
    public String getDatabaseHost() {
        return config.getString("database.host", "localhost");
    }
    
    public int getDatabasePort() {
        return config.getInt("database.port", 3306);
    }
    
    public String getDatabaseName() {
        return config.getString("database.database", "staffonsteroids");
    }
    
    public String getDatabaseUsername() {
        return config.getString("database.username", "root");
    }
    
    public String getDatabasePassword() {
        return config.getString("database.password", "password");
    }
    
    public int getPoolSize() {
        return config.getInt("database.pool-size", 10);
    }
    
    public long getConnectionTimeout() {
        return config.getLong("database.connection-timeout", 30000);
    }
    
    public long getIdleTimeout() {
        return config.getLong("database.idle-timeout", 600000);
    }
    
    public long getMaxLifetime() {
        return config.getLong("database.max-lifetime", 1800000);
    }
    
    public boolean isAutoSave() {
        return config.getBoolean("settings.auto-save", true);
    }
    
    public int getSaveInterval() {
        return config.getInt("settings.save-interval", 300);
    }
    
    public boolean isDebug() {
        return config.getBoolean("settings.debug", false);
    }
    
    public String getStaffChatFormat() {
        return ChatColor.translateAlternateColorCodes('&', 
            config.getString("staff-chat.format", "&8[&cStaff&8] &f{player}: {message}"));
    }
    
    public boolean shouldHideFromTab() {
        return config.getBoolean("vanish.hide-from-tab", true);
    }
    
    public boolean isSilentJoinQuit() {
        return config.getBoolean("vanish.silent-join-quit", true);
    }
    
    public boolean shouldBanOnQuit() {
        return config.getBoolean("freeze.ban-on-quit", true);
    }
    
    public int getMovementMessageInterval() {
        return config.getInt("freeze.movement-message-interval", 5);
    }
    
    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        
        messageCache.clear();
        cacheMessages();
    }
}