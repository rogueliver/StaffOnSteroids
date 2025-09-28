package me.rogueliver.staffonsteroids.listeners;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import me.rogueliver.staffonsteroids.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    
    private final StaffOnSteroids plugin;
    
    public ChatListener(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (plugin.getDatabaseManager().getMutedPlayers().contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(MessageUtil.error("info.you-are-muted"));
            return;
        }
        
        String ip = player.getAddress().getAddress().getHostAddress();
        if (plugin.getDatabaseManager().getMutedIps().contains(ip)) {
            event.setCancelled(true);
            player.sendMessage(MessageUtil.error("info.ip-is-muted"));
            return;
        }
        
        if (plugin.getDatabaseManager().getStaffChatEnabled().contains(player.getUniqueId())) {
            event.setCancelled(true);
            
            var staffMessage = MessageUtil.staffChat(player.getName(), event.getMessage());
            Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("staffonsteroids.staff"))
                .forEach(p -> p.sendMessage(staffMessage));
            
            Bukkit.getConsoleSender().sendMessage(staffMessage);
        }
    }
}