package me.rogueliver.staffonsteroids.commands;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import me.rogueliver.staffonsteroids.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {
    
    private final StaffOnSteroids plugin;
    
    public KickCommand(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staffonsteroids.staff")) {
            sender.sendMessage(MessageUtil.error("errors.no-permission"));
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.error("errors.usage-kick"));
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
        
        target.kick(MessageUtil.error("kick-messages.kicked", "reason", reason));
        sender.sendMessage(MessageUtil.success("success.kicked", "player", targetName, "reason", reason));
        
        return true;
    }
}