package studio.trc.bukkit.litecommandeditor.util;

import java.util.Map;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import studio.trc.bukkit.litecommandeditor.Main;
import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationUtil;
import studio.trc.bukkit.litecommandeditor.event.listener.PlayerEventManager;
import studio.trc.bukkit.litecommandeditor.hook.PlaceholderAPIHook;
import studio.trc.bukkit.litecommandeditor.message.JSONComponentManager;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandExecutor;
import studio.trc.bukkit.litecommandeditor.module.CommandLoader;
import studio.trc.bukkit.litecommandeditor.module.CommandManager;
import studio.trc.bukkit.litecommandeditor.module.function.Configurator;
import studio.trc.bukkit.litecommandeditor.thread.LiteCommandEditorThread;

public class PluginControl
{
    @Getter
    @Setter
    private static boolean reloading = false;
    
    public static void loadPlugin() {
        reloading = true;
        Map<String, String> placeholders = reloadConfig();
        MessageUtil.setAdventureAvailable();
        MessageUtil.loadPlaceholders();
        placeholders.put("{language}", MessageUtil.getLangaugeName());
        placeholders.put("{version}", Main.getInstance().getDescription().getVersion());
        CommandManager.reloadCommandConfigurations();
        placeholders.put("{commands}", String.valueOf(CommandLoader.getCache().size()));
        placeholders.putAll(JSONComponentManager.reloadJSONComponents());
        try {
            NMSUtils.initialize();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        reloading = false;
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Initialization", placeholders);
        registerHooks();
        LiteCommandEditorThread.initialize();
        Bukkit.getMessenger().registerOutgoingPluginChannel(Main.getInstance(), "BungeeCord");
    }
    
    public static void reloadPlugin(CommandSender sender) {
        Map<String, String> placeholders = reloadConfig();
        placeholders.putAll(reloadMessages());
        CommandManager.reloadCommandConfigurations();
        placeholders.put("{commands}", String.valueOf(CommandManager.getRegisteredCommands().size()));
        registerHooks();
        LiteCommandEditorThread.initialize();
        CommandExecutor.setDebug(false);
        CommandExecutor.getDebugRecords().clear();
        PlayerEventManager.clearRecords();
        Configurator.initialize();
        MessageUtil.sendCommandMessage(sender, "Reload", placeholders);
    }
    
    public static void registerHooks() {
        try {
            if (ConfigurationType.CONFIG.getRobustConfig().getBoolean("PlaceholderAPI.Enabled")) {
                if (!PlaceholderAPIHook.getInstance().isRegistered()) {
                    PlaceholderAPIHook.getInstance().register();
                }
                MessageUtil.setEnabledPAPI(true);
                LiteCommandEditorProperties.sendOperationMessage("FindThePlaceholderAPI", MessageUtil.getDefaultPlaceholders());
            }
        } catch (Throwable t) {
            MessageUtil.setEnabledPAPI(false);
            LiteCommandEditorProperties.sendOperationMessage("PlaceholderAPINotFound", MessageUtil.getDefaultPlaceholders());
        }
    }
    
    public static boolean enableMetrics() {
        return ConfigurationType.CONFIG.getRobustConfig().getBoolean("Metrics");
    }

    public static boolean enableUpdater() {
        return ConfigurationType.CONFIG.getRobustConfig().getBoolean("Updater");
    }
    
    public static Map<String, String> reloadConfig() {
        Map<String, String> placeholders = ConfigurationUtil.reloadConfig();
        placeholders.putAll(MessageUtil.getDefaultPlaceholders());
        return placeholders;
    }
    
    public static Map<String, String> reloadMessages() {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        MessageUtil.loadPlaceholders();
        placeholders.putAll(JSONComponentManager.reloadJSONComponents());
        return placeholders;
    }
    
    public static void runBukkitTask(Runnable task, long delay) {
        try {
            if (delay == 0) {
                Bukkit.getScheduler().runTask(Main.getInstance(), task);
            } else {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), task, delay);
            }
        } catch (UnsupportedOperationException ex) {
            //Folia suppport (test)
            Consumer runnable = run -> task.run();
            try {
                Object globalRegionScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler").invoke(null);
                if (delay == 0) {
                    globalRegionScheduler.getClass().getMethod("run", Plugin.class, Consumer.class).invoke(globalRegionScheduler, Main.getInstance(), runnable);
                } else {
                    globalRegionScheduler.getClass().getMethod("runDelayed", Plugin.class, Consumer.class, long.class).invoke(globalRegionScheduler, Main.getInstance(), runnable, delay);
                }
            } catch (Exception e) {
                e.printStackTrace();
                task.run();
            }
        }
    }
}
