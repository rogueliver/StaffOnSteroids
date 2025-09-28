package me.rogueliver.staffonsteroids;

import lombok.Getter;
import me.rogueliver.staffonsteroids.commands.*;
import me.rogueliver.staffonsteroids.config.ConfigManager;
import me.rogueliver.staffonsteroids.database.DatabaseManager;
import me.rogueliver.staffonsteroids.listeners.ChatListener;
import me.rogueliver.staffonsteroids.listeners.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class StaffOnSteroids extends JavaPlugin {
    
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    
    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.databaseManager = new DatabaseManager(this);
        
        if (!databaseManager.initialize()) {
            getLogger().severe("Failed to initialize database! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        registerCommands();
        registerListeners();

        getLogger().info("StaffOnSteroids has been enabled!");
        getLogger().info("imagine cool ascii art here");
        getLogger().info("Plugin by rogueliver, github.com/rogueliver");
    }
    
    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("StaffOnSteroids has been disabled!");
    }
    
    private void registerCommands() {
        getCommand("staffchat").setExecutor(new StaffChatCommand(this));
        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("ipban").setExecutor(new IpBanCommand(this));
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("ipmute").setExecutor(new IpMuteCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("ipkick").setExecutor(new IpKickCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand(this));
        getCommand("freeze").setExecutor(new FreezeCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }
}