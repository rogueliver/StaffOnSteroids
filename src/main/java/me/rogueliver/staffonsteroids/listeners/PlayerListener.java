package me.rogueliver.staffonsteroids.listeners;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import me.rogueliver.staffonsteroids.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final StaffOnSteroids plugin;

    public PlayerListener(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        for (Player vanished : Bukkit.getOnlinePlayers()) {
            if (plugin.getDatabaseManager().getVanishedPlayers().contains(vanished.getUniqueId())
                    && !player.hasPermission("staffonsteroids.admin")) {
                player.hidePlayer(plugin, vanished);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (plugin.getDatabaseManager().getFrozenPlayers().contains(player.getUniqueId())) {
            if (event.getFrom().getX() != event.getTo().getX()
                    || event.getFrom().getZ() != event.getTo().getZ()) {
                event.setCancelled(true);
                player.sendMessage(MessageUtil.error("info.you-are-frozen"));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getDatabaseManager().getFrozenPlayers().contains(player.getUniqueId())) {
            if (plugin.getConfigManager().shouldBanOnQuit()) {
                java.util.Date expiration = new java.util.Date(System.currentTimeMillis() + java.util.concurrent.TimeUnit.HOURS.toMillis(1));
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(
                        player.getName(),
                        plugin.getConfigManager().getMessage("ban-messages.freeze-quit"),
                        expiration,
                        "StaffOnSteroids"
                );
            }
        }

        plugin.getDatabaseManager().getStaffChatEnabled().remove(player.getUniqueId());
        plugin.getDatabaseManager().getVanishedPlayers().remove(player.getUniqueId());
        plugin.getDatabaseManager().getFrozenPlayers().remove(player.getUniqueId());

        plugin.getDatabaseManager().savePlayerData(player.getUniqueId());
    }
}