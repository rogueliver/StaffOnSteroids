package me.rogueliver.staffonsteroids.commands;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import me.rogueliver.staffonsteroids.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnIpMuteCommand implements CommandExecutor {

    private final StaffOnSteroids plugin;

    public UnIpMuteCommand(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staffonsteroids.staff")) {
            sender.sendMessage(MessageUtil.error("errors.no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.error("errors.usage-unipmute"));
            return true;
        }

        String input = args[0];
        String ip;
        String displayName;

        if (input.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            ip = input;
            displayName = ip;
        } else {
            Player target = Bukkit.getPlayer(input);
            if (target == null) {
                sender.sendMessage(MessageUtil.error("errors.player-not-online"));
                return true;
            }
            ip = target.getAddress().getAddress().getHostAddress();
            displayName = target.getName();
        }

        if (!plugin.getDatabaseManager().getMutedIps().contains(ip)) {
            sender.sendMessage(MessageUtil.error("errors.ip-not-muted"));
            return true;
        }

        plugin.getDatabaseManager().getMutedIps().remove(ip);
        plugin.getDatabaseManager().removeIpMute(ip);

        sender.sendMessage(MessageUtil.success("success.ip-unmuted", "player", displayName));

        return true;
    }
}