package me.rogueliver.staffonsteroids.commands;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import me.rogueliver.staffonsteroids.utils.MessageUtil;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnbanCommand implements CommandExecutor {

    private final StaffOnSteroids plugin;

    public UnbanCommand(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staffonsteroids.staff")) {
            sender.sendMessage(MessageUtil.error("errors.no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.error("errors.usage-unban"));
            return true;
        }

        String targetName = args[0];

        if (!Bukkit.getBanList(BanList.Type.NAME).isBanned(targetName)) {
            sender.sendMessage(MessageUtil.error("errors.player-not-banned"));
            return true;
        }

        Bukkit.getBanList(BanList.Type.NAME).pardon(targetName);
        sender.sendMessage(MessageUtil.success("success.unbanned", "player", targetName));

        return true;
    }
}