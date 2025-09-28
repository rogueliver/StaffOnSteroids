package me.rogueliver.staffonsteroids.commands;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import me.rogueliver.staffonsteroids.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnvanishCommand implements CommandExecutor {

    private final StaffOnSteroids plugin;

    public UnvanishCommand(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staffonsteroids.admin")) {
            sender.sendMessage(MessageUtil.error("errors.no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.error("errors.usage-unvanish"));
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sender.sendMessage(MessageUtil.error("errors.player-not-found"));
            return true;
        }

        if (!plugin.getDatabaseManager().getVanishedPlayers().contains(target.getUniqueId())) {
            sender.sendMessage(MessageUtil.error("errors.player-not-vanished"));
            return true;
        }

        plugin.getDatabaseManager().getVanishedPlayers().remove(target.getUniqueId());
        plugin.getDatabaseManager().savePlayerData(target.getUniqueId());

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showPlayer(plugin, target);
        }

        target.sendMessage(MessageUtil.info("info.unvanished"));
        sender.sendMessage(MessageUtil.success("success.vanish-disabled", "player", targetName));

        return true;
    }
}