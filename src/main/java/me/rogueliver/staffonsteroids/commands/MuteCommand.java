package me.rogueliver.staffonsteroids.commands;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import me.rogueliver.staffonsteroids.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand implements CommandExecutor {
    
    private final StaffOnSteroids plugin;
    
    public MuteCommand(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staffonsteroids.staff")) {
            sender.sendMessage(MessageUtil.error("errors.no-permission"));
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.error("errors.usage-mute"));
            return true;
        }
        
        String targetName = args[0];
        String reason = MessageUtil.formatReason(args, 1);
        
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(MessageUtil.error("errors.player-not-found"));
            return true;
        }
        
        if (target.hasPermission("staffonsteroids.admin")) {
            sender.sendMessage(MessageUtil.error("errors.cannot-target-admin"));
            return true;
        }
        
        boolean wasMuted = plugin.getDatabaseManager().getMutedPlayers().contains(target.getUniqueId());
        
        if (wasMuted) {
            plugin.getDatabaseManager().getMutedPlayers().remove(target.getUniqueId());
            target.sendMessage(MessageUtil.info("info.unfrozen"));
            sender.sendMessage(MessageUtil.success("success.unmuted", "player", targetName));
        } else {
            plugin.getDatabaseManager().getMutedPlayers().add(target.getUniqueId());
            target.sendMessage(MessageUtil.info("info.muted", "reason", reason));
            sender.sendMessage(MessageUtil.success("success.muted", "player", targetName, "reason", reason));
        }
        
        plugin.getDatabaseManager().savePlayerData(target.getUniqueId());
        return true;
    }
}