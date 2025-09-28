package me.rogueliver.staffonsteroids.commands;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import me.rogueliver.staffonsteroids.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {
    
    private final StaffOnSteroids plugin;
    
    public VanishCommand(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staffonsteroids.admin")) {
            sender.sendMessage(MessageUtil.error("errors.no-permission"));
            return true;
        }
        
        Player target;
        
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtil.error("errors.console-specify-player"));
                return true;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(MessageUtil.error("errors.player-not-found"));
                return true;
            }
        }
        
        boolean wasVanished = plugin.getDatabaseManager().getVanishedPlayers().contains(target.getUniqueId());
        
        if (wasVanished) {
            plugin.getDatabaseManager().getVanishedPlayers().remove(target.getUniqueId());
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showPlayer(plugin, target);
            }
            
            target.sendMessage(MessageUtil.info("info.unvanished"));
            if (!sender.equals(target)) {
                sender.sendMessage(MessageUtil.success("success.vanish-disabled", "player", target.getName()));
            }
        } else {
            plugin.getDatabaseManager().getVanishedPlayers().add(target.getUniqueId());
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("staffonsteroids.admin")) {
                    player.hidePlayer(plugin, target);
                }
            }
            
            target.sendMessage(MessageUtil.info("info.vanished"));
            if (!sender.equals(target)) {
                sender.sendMessage(MessageUtil.success("success.vanish-enabled", "player", target.getName()));
            }
        }
        
        plugin.getDatabaseManager().savePlayerData(target.getUniqueId());
        return true;
    }
}