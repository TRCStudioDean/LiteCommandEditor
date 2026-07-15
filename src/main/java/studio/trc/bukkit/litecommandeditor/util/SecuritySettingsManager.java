package studio.trc.bukkit.litecommandeditor.util;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.configuration.RobustConfiguration;

public class SecuritySettingsManager 
{
    public static boolean PARSE_COLOR_FROM_SUB_COMMAND_PLACEHOLDER = false;
    public static boolean PARSE_PLACEHOLDER_FROM_SUB_COMMAND_PLACEHOLDER = false;
    public static int MAX_PLACEHOLDER_NESTINGS = -1;
    
    public static void reload() {
        RobustConfiguration config = ConfigurationType.CONFIG.getRobustConfig();
        PARSE_COLOR_FROM_SUB_COMMAND_PLACEHOLDER = config.getBoolean("Security-Settings.Parse-Color-From-Sub-Command-Placeholder");
        PARSE_PLACEHOLDER_FROM_SUB_COMMAND_PLACEHOLDER = config.getBoolean("Security-Settings.Parse-Placeholder-From-Sub-Command-Placeholder");
        MAX_PLACEHOLDER_NESTINGS = config.getInt("Security-Settings.Maximum-Placeholder-Nestings");
    }
}
