package me.rogueliver.staffonsteroids.commands;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import me.rogueliver.staffonsteroids.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class IpKickCommand implements CommandExecutor {
    
    private final StaffOnSteroids plugin;
    
    public IpKickCommand(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staffonsteroids.staff")) {
            sender.sendMessage(MessageUtil.error("errors.no-permission"));
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.error("errors.usage-ipkick"));
            return true;
        }
        
        String targetName = args[0];
        String reason = MessageUtil.formatReason(args, 1);
        
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(MessageUtil.error("errors.player-not-found"));
            return true;
        }
        
        String ip = target.getAddress().getAddress().getHostAddress();
        List<Player> playersToKick = new ArrayList<>();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getAddress().getAddress().getHostAddress().equals(ip)) {
                if (!player.hasPermission("staffonsteroids.admin")) {
                    playersToKick.add(player);
                }
            }
        }
        
        if (playersToKick.isEmpty()) {
            sender.sendMessage(MessageUtil.error("errors.no-kickable-players"));
            return true;
        }
        
        playersToKick.forEach(player -> 
            player.kick(MessageUtil.error("kick-messages.ip-kicked", "reason", reason)));
        
        sender.sendMessage(MessageUtil.success("success.ip-kicked", 
            "count", String.valueOf(playersToKick.size()), "reason", reason));
        
        return true;
    }
}