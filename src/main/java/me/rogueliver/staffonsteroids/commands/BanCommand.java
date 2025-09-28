package me.rogueliver.staffonsteroids.commands;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import me.rogueliver.staffonsteroids.utils.MessageUtil;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand implements CommandExecutor {
    
    private final StaffOnSteroids plugin;
    
    public BanCommand(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staffonsteroids.staff")) {
            sender.sendMessage(MessageUtil.error("errors.no-permission"));
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.error("errors.usage-ban"));
            return true;
        }
        
        String targetName = args[0];
        String reason = MessageUtil.formatReason(args, 1);
        
        Player target = Bukkit.getPlayer(targetName);
        if (target != null && target.hasPermission("staffonsteroids.admin")) {
            sender.sendMessage(MessageUtil.error("errors.cannot-target-admin"));
            return true;
        }
        
        Bukkit.getBanList(BanList.Type.NAME).addBan(targetName, reason, null, sender.getName());
        
        if (target != null) {
            target.kick(MessageUtil.error("ban-messages.banned", "reason", reason));
        }
        
        sender.sendMessage(MessageUtil.success("success.banned", "player", targetName, "reason", reason));
        
        return true;
    }
}