package me.rogueliver.staffonsteroids.commands;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import me.rogueliver.staffonsteroids.utils.MessageUtil;
import me.rogueliver.staffonsteroids.utils.TimeUtil;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class TempIpBanCommand implements CommandExecutor {

    private final StaffOnSteroids plugin;

    public TempIpBanCommand(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staffonsteroids.staff")) {
            sender.sendMessage(MessageUtil.error("errors.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtil.error("errors.usage-tempipban"));
            return true;
        }

        String targetName = args[0];
        String duration = args[1];
        String reason = MessageUtil.formatReason(args, 2);

        Date expiration = TimeUtil.getExpirationDate(duration);
        if (expiration == null) {
            sender.sendMessage(MessageUtil.error("errors.invalid-duration"));
            return true;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target != null && target.hasPermission("staffonsteroids.admin")) {
            sender.sendMessage(MessageUtil.error("errors.cannot-target-admin"));
            return true;
        }

        if (target != null) {
            String ip = target.getAddress().getAddress().getHostAddress();
            Bukkit.getBanList(BanList.Type.IP).addBan(ip, reason, expiration, sender.getName());
            target.kick(MessageUtil.error("ban-messages.temp-ip-banned", "duration", TimeUtil.formatDuration(duration), "reason", reason));
            sender.sendMessage(MessageUtil.success("success.temp-ip-banned", "player", targetName, "duration", TimeUtil.formatDuration(duration), "reason", reason));
        } else {
            sender.sendMessage(MessageUtil.error("errors.player-not-online"));
        }

        return true;
    }
}