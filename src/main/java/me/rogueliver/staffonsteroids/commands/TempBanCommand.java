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

public class TempBanCommand implements CommandExecutor {

    private final StaffOnSteroids plugin;

    public TempBanCommand(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staffonsteroids.staff")) {
            sender.sendMessage(MessageUtil.error("errors.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtil.error("errors.usage-tempban"));
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

        Bukkit.getBanList(BanList.Type.NAME).addBan(targetName, reason, expiration, sender.getName());

        if (target != null) {
            target.kick(MessageUtil.error("ban-messages.temp-banned", "duration", TimeUtil.formatDuration(duration), "reason", reason));
        }

        sender.sendMessage(MessageUtil.success("success.temp-banned", "player", targetName, "duration", TimeUtil.formatDuration(duration), "reason", reason));

        return true;
    }
}