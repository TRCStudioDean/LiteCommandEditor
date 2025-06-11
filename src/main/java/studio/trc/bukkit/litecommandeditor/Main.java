/*
......................我佛慈悲......................
                       _oo0oo_                      
                      o8888888o                     
                      88" . "88                     
                      (| -_- |)                     
                      0\  =  /0                     
                    ___/‘---’\___                   
                  .' \|       |/ '.                 
                 / \\|||  :  |||// \\               
                / _||||| -卍-|||||_ \\              
               | \_|  ''\---/''  |_/ |              
               \  .-\__  '-'  ___/-. /              
             ___'. .'  /--.--\   '. .'___           
          ."" ‘<  ‘.___\_<|>_/___.’   >’ "".        
         | | :  ‘- \‘.;‘\ _ /’;.’/ - ’ : | |        
         \ \    ‘_. \_ __\ /__ _/  .-’   / /        
      =====‘-.___‘.___ \_____/___.-’___.-’=====     
                       ‘=---=’                      
                                                   
.................佛祖开光 - 永无BUG.................
*/

package studio.trc.bukkit.litecommandeditor;

import java.util.Map;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorCommand;
import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommandType;
import studio.trc.bukkit.litecommandeditor.event.listener.PlayerEventManager;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.metrics.Metrics;
import studio.trc.bukkit.litecommandeditor.metrics.SingleLineChart;
import studio.trc.bukkit.litecommandeditor.module.CommandLoader;
import studio.trc.bukkit.litecommandeditor.thread.LiteCommandEditorThread;
import studio.trc.bukkit.litecommandeditor.module.tool.Updater;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;
import studio.trc.bukkit.litecommandeditor.util.PluginControl;

public class Main
    extends JavaPlugin
{
    @Getter
    private static Main instance;
    @Getter
    private static Metrics metrics;
    
    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        instance = this;
        LiteCommandEditorProperties.reloadProperties();
        LiteCommandEditorProperties.sendOperationMessage("PluginEnabling");
        if (!getDescription().getName().equals("LiteCommandEditor")) {
            LiteCommandEditorProperties.sendOperationMessage("PluginNameChange");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        registerCommandExecutor();
        registerEvents();
        LiteCommandEditorProperties.sendOperationMessage("PluginCommandRegistered");
        PluginControl.loadPlugin();
        if (PluginControl.enableMetrics()) {
            metrics = new Metrics(this, 16521);
            metrics.addCustomChart(new SingleLineChart("loaded_custom_commands", () -> CommandLoader.getCache().size()));
        }
        long endTime = System.currentTimeMillis();
        PluginControl.runBukkitTask(() -> Updater.checkUpdate(), 0);
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{time}", String.valueOf(endTime - startTime));
        LiteCommandEditorProperties.sendOperationMessage("PluginSuccessfullyEnabled", placeholders);
    }

    @Override
    public void onDisable() {
        LiteCommandEditorThread.getTaskThread().setRunning(false);
    }

    private void registerCommandExecutor() {
        PluginCommand command = getCommand("litecommandeditor");
        LiteCommandEditorCommand commandExecutor = new LiteCommandEditorCommand();
        command.setExecutor(commandExecutor);
        command.setTabCompleter(commandExecutor);
        for (LiteCommandEditorSubCommandType subCommandType : LiteCommandEditorSubCommandType.values()) {
            LiteCommandEditorCommand.getSubCommands().put(subCommandType.getSubCommandName(), subCommandType.getSubCommand());
        }
    }
    
    private void registerEvents() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new PlayerEventManager(), this);
    }
}
