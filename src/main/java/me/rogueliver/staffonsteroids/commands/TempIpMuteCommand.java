package me.rogueliver.staffonsteroids.commands;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import me.rogueliver.staffonsteroids.utils.MessageUtil;
import me.rogueliver.staffonsteroids.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TempIpMuteCommand implements CommandExecutor {

    private final StaffOnSteroids plugin;

    public TempIpMuteCommand(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staffonsteroids.staff")) {
            sender.sendMessage(MessageUtil.error("errors.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtil.error("errors.usage-tempipmute"));
            return true;
        }

        String targetName = args[0];
        String duration = args[1];
        String reason = MessageUtil.formatReason(args, 2);

        long durationMillis = TimeUtil.parseDuration(duration);
        if (durationMillis <= 0) {
            sender.sendMessage(MessageUtil.error("errors.invalid-duration"));
            return true;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(MessageUtil.error("errors.player-not-found"));
            return true;
        }

        if (target.hasPermission("staffonsteroids.admin")) {
            sender.sendMessage(MessageUtil.error("errors.cannot-target-admin"));
            return true;
        }

        String ip = target.getAddress().getAddress().getHostAddress();
        plugin.getDatabaseManager().getMutedIps().add(ip);
        plugin.getDatabaseManager().saveTempIpMute(ip, reason, sender.getName(), System.currentTimeMillis() + durationMillis);

        target.sendMessage(MessageUtil.info("info.ip-muted", "reason", reason));
        sender.sendMessage(MessageUtil.success("success.temp-ip-muted", "player", targetName, "duration", TimeUtil.formatDuration(duration), "reason", reason));

        return true;
    }
}