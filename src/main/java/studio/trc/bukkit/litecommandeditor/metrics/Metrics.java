package studio.trc.bukkit.litecommandeditor.metrics;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import studio.trc.bukkit.litecommandeditor.util.PluginControl;

public class Metrics {

    private final Plugin plugin;

    private final MetricsBase metricsBase;

    public Metrics(JavaPlugin plugin, int serviceId) {
        this.plugin = plugin;
        // Get the config file
        File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats");
        File configFile = new File(bStatsFolder, "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if (!config.isSet("serverUuid")) {
            config.addDefault("enabled", true);
            config.addDefault("serverUuid", UUID.randomUUID().toString());
            config.addDefault("logFailedRequests", false);
            config.addDefault("logSentData", false);
            config.addDefault("logResponseStatusText", false);
            // Inform the server owners about bStats
            config.options().header(
                  "bStats (https://bStats.org) collects some basic information for plugin authors, like how\n"
                      + "many people use their plugin and their total player count. It's recommended to keep bStats\n"
                      + "enabled, but if you're not comfortable with this, you can turn this setting off. There is no\n"
                      + "performance penalty associated with having metrics enabled, and data sent to bStats is fully\n"
                      + "anonymous.")
                .copyDefaults(true);
            try {
                config.save(configFile);
            } catch (IOException ignored) {}
        }
        // Load the data
        boolean enabled = config.getBoolean("enabled", true);
        String serverUUID = config.getString("serverUuid");
        boolean logErrors = config.getBoolean("logFailedRequests", false);
        boolean logSentData = config.getBoolean("logSentData", false);
        boolean logResponseStatusText = config.getBoolean("logResponseStatusText", false);
        metricsBase =
            new MetricsBase(
                "bukkit",
                serverUUID,
                serviceId,
                enabled,
                this::appendPlatformData,
                this::appendServiceData,
                submitDataTask -> PluginControl.runBukkitTask(submitDataTask, 0),
                plugin::isEnabled,
                (message, error) -> this.plugin.getLogger().log(Level.WARNING, message, error),
                (message) -> this.plugin.getLogger().log(Level.INFO, message),
                logErrors,
                logSentData,
                logResponseStatusText);
    }

    /**
     * Adds a custom chart.
     *
     * @param chart The chart to add.
     */
    public void addCustomChart(CustomChart chart) {
        metricsBase.addCustomChart(chart);
    }

    private void appendPlatformData(JsonObjectBuilder builder) {
        builder.appendField("playerAmount", getPlayerAmount());
        builder.appendField("onlineMode", Bukkit.getOnlineMode() ? 1 : 0);
        builder.appendField("bukkitVersion", Bukkit.getVersion());
        builder.appendField("bukkitName", Bukkit.getName());
        builder.appendField("javaVersion", System.getProperty("java.version"));
        builder.appendField("osName", System.getProperty("os.name"));
        builder.appendField("osArch", System.getProperty("os.arch"));
        builder.appendField("osVersion", System.getProperty("os.version"));
        builder.appendField("coreCount", Runtime.getRuntime().availableProcessors());
    }

    private void appendServiceData(JsonObjectBuilder builder) {
        builder.appendField("pluginVersion", plugin.getDescription().getVersion());
    }

    private int getPlayerAmount() {
        try {
            // Around MC 1.8 the return type was changed from an array to a collection,
            // This fixes java.lang.NoSuchMethodError:
            // org.bukkit.Bukkit.getOnlinePlayers()Ljava/util/Collection;
            Method onlinePlayersMethod = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers");
            return onlinePlayersMethod.getReturnType().equals(Collection.class)
                ? ((Collection<?>) onlinePlayersMethod.invoke(Bukkit.getServer())).size()
                : ((Player[]) onlinePlayersMethod.invoke(Bukkit.getServer())).length;
        } catch (Exception e) {
            // Just use the new method if the reflection failed
            return Bukkit.getOnlinePlayers().size();
        }
    }
}