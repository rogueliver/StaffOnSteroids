package me.rogueliver.staffonsteroids.utils;

import me.rogueliver.staffonsteroids.StaffOnSteroids;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageUtil {
    
    private static StaffOnSteroids plugin;
    
    public static void setPlugin(StaffOnSteroids pluginInstance) {
        plugin = pluginInstance;
    }
    
    public static Component success(String key, String... replacements) {
        return LegacyComponentSerializer.legacyAmpersand()
            .deserialize(plugin.getConfigManager().getMessage(key, replacements));
    }
    
    public static Component error(String key, String... replacements) {
        return LegacyComponentSerializer.legacyAmpersand()
            .deserialize(plugin.getConfigManager().getMessage(key, replacements));
    }
    
    public static Component info(String key, String... replacements) {
        return LegacyComponentSerializer.legacyAmpersand()
            .deserialize(plugin.getConfigManager().getMessage(key, replacements));
    }
    
    public static Component staffChat(String playerName, String message) {
        String format = plugin.getConfigManager().getStaffChatFormat()
            .replace("{player}", playerName)
            .replace("{message}", message);
        
        return LegacyComponentSerializer.legacyAmpersand().deserialize(format);
    }
    
    public static String formatReason(String[] args, int startIndex) {
        if (args.length <= startIndex) {
            return plugin.getConfigManager().getMessage("default-reason");
        }
        return String.join(" ", java.util.Arrays.copyOfRange(args, startIndex, args.length));
    }
}