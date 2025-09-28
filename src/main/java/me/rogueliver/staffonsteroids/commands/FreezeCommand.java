package me.rogueliver.staffonsteroids.commands;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import me.rogueliver.staffonsteroids.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand implements CommandExecutor {
    
    private final StaffOnSteroids plugin;
    
    public FreezeCommand(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staffonsteroids.admin")) {
            sender.sendMessage(MessageUtil.error("errors.no-permission"));
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.error("errors.usage-freeze"));
            return true;
        }
        
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            sender.sendMessage(MessageUtil.error("errors.player-not-found"));
            return true;
        }
        
        if (target.hasPermission("staffonsteroids.admin")) {
            sender.sendMessage(MessageUtil.error("errors.cannot-target-admin"));
            return true;
        }
        
        boolean wasFrozen = plugin.getDatabaseManager().getFrozenPlayers().contains(target.getUniqueId());
        
        if (wasFrozen) {
            plugin.getDatabaseManager().getFrozenPlayers().remove(target.getUniqueId());
            target.sendMessage(MessageUtil.info("info.unfrozen"));
            sender.sendMessage(MessageUtil.success("success.unfrozen", "player", targetName));
        } else {
            plugin.getDatabaseManager().getFrozenPlayers().add(target.getUniqueId());
            target.sendMessage(MessageUtil.info("info.frozen"));
            sender.sendMessage(MessageUtil.success("success.frozen", "player", targetName));
        }
        
        plugin.getDatabaseManager().savePlayerData(target.getUniqueId());
        return true;
    }
}