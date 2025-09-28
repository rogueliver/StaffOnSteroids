package me.rogueliver.staffonsteroids.commands;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import me.rogueliver.staffonsteroids.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffChatCommand implements CommandExecutor {
    
    private final StaffOnSteroids plugin;
    
    public StaffChatCommand(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtil.error("errors.player-only"));
            return true;
        }
        
        if (!player.hasPermission("staffonsteroids.staff")) {
            player.sendMessage(MessageUtil.error("errors.no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            boolean enabled = plugin.getDatabaseManager().getStaffChatEnabled().contains(player.getUniqueId());
            
            if (enabled) {
                plugin.getDatabaseManager().getStaffChatEnabled().remove(player.getUniqueId());
                player.sendMessage(MessageUtil.info("info.staff-chat-disabled"));
            } else {
                plugin.getDatabaseManager().getStaffChatEnabled().add(player.getUniqueId());
                player.sendMessage(MessageUtil.info("info.staff-chat-enabled"));
            }
            
            plugin.getDatabaseManager().savePlayerData(player.getUniqueId());
        } else {
            String message = String.join(" ", args);
            broadcastStaffMessage(player.getName(), message);
        }
        
        return true;
    }
    
    private void broadcastStaffMessage(String playerName, String message) {
        var staffMessage = MessageUtil.staffChat(playerName, message);
        
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.hasPermission("staffonsteroids.staff"))
            .forEach(p -> p.sendMessage(staffMessage));
        
        Bukkit.getConsoleSender().sendMessage(staffMessage);
    }
}