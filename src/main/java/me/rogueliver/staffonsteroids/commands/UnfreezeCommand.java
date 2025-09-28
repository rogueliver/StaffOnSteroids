package me.rogueliver.staffonsteroids.commands;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import me.rogueliver.staffonsteroids.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnfreezeCommand implements CommandExecutor {

    private final StaffOnSteroids plugin;

    public UnfreezeCommand(StaffOnSteroids plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("staffonsteroids.admin")) {
            sender.sendMessage(MessageUtil.error("errors.no-permission"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(MessageUtil.error("errors.usage-unfreeze"));
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sender.sendMessage(MessageUtil.error("errors.player-not-found"));
            return true;
        }

        if (!plugin.getDatabaseManager().getFrozenPlayers().contains(target.getUniqueId())) {
            sender.sendMessage(MessageUtil.error("errors.player-not-frozen"));
            return true;
        }

        plugin.getDatabaseManager().getFrozenPlayers().remove(target.getUniqueId());
        plugin.getDatabaseManager().savePlayerData(target.getUniqueId());

        target.sendMessage(MessageUtil.info("info.unfrozen"));
        sender.sendMessage(MessageUtil.success("success.unfrozen", "player", targetName));

        return true;
    }
}