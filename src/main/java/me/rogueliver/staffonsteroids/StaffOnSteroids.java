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

        getServer().getScheduler().runTaskTimerAsynchronously(this,
                databaseManager::checkExpiredMutes, 20L * 60L, 20L * 60L);

        getLogger().info("\n------------------------------------");
        getLogger().info("[SOS] Imagine cool ASCii art here");
        getLogger().info("[SOS] StaffOnSteroids has been enabled!");
        getLogger().info(" SOS v" + getDescription().getVersion() + " by RL");
        getLogger().info("------------------------------------\n");
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
        getCommand("tempban").setExecutor(new TempBanCommand(this));
        getCommand("ipban").setExecutor(new IpBanCommand(this));
        getCommand("tempipban").setExecutor(new TempIpBanCommand(this));
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("tempmute").setExecutor(new TempMuteCommand(this));
        getCommand("ipmute").setExecutor(new IpMuteCommand(this));
        getCommand("tempipmute").setExecutor(new TempIpMuteCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("ipkick").setExecutor(new IpKickCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand(this));
        getCommand("freeze").setExecutor(new FreezeCommand(this));
        getCommand("unban").setExecutor(new UnbanCommand(this));
        getCommand("unipban").setExecutor(new UnIpBanCommand(this));
        getCommand("unmute").setExecutor(new UnmuteCommand(this));
        getCommand("unipmute").setExecutor(new UnIpMuteCommand(this));
        getCommand("unfreeze").setExecutor(new UnfreezeCommand(this));
        getCommand("unvanish").setExecutor(new UnvanishCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }
}