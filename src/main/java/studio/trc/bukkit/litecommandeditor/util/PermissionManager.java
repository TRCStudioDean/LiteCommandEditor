package studio.trc.bukkit.litecommandeditor.util;

import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.configuration.RobustConfiguration;

public class PermissionManager
{
    public static boolean hasPermission(CommandSender sender, ConfigurationType configType, String configPath) {
        return hasPermission(sender, configType.getRobustConfig(), configPath);
    }
    
    public static boolean hasPermission(CommandSender sender, RobustConfiguration config, String configPath) {
        if (config.getBoolean(configPath + ".Default")) return true;
        boolean value = sender.hasPermission(config.getString(configPath + ".Permission"));
        return value;
    }
}
