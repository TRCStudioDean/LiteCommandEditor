package studio.trc.bukkit.litecommandeditor.thread.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.module.function.Command;
import studio.trc.bukkit.litecommandeditor.thread.LiteCommandEditorTask;

public class TimerTask
    extends LiteCommandEditorTask
{
    @Getter
    private final boolean finished = false;
    
    public TimerTask(String timerName, Runnable task, long tickInterval) {
        super(timerName, task, -1, tickInterval);
    }
    
    public static List<TimerTask> loadAllTimer(double delay) {
        List<TimerTask> result = new ArrayList<>();
        YamlConfiguration config = ConfigurationType.TIMER.getConfig();
        ConfigurationType.TIMER.getRobustConfig().getConfigurationSection("Timer").getKeys(false).stream()
            .filter(
                configPath -> config.getBoolean("Timer." + configPath + ".Enabled") && 
                config.contains("Timer." + configPath + ".Commands") && 
                !config.getList("Timer." + configPath + ".Commands").isEmpty()
            )
            .forEach(configPath -> {
                if (config.getBoolean("Timer." + configPath + ".Enabled")) {
                    List<Command> commands = config.getStringList("Timer." + configPath + ".Commands").stream().map(command -> Command.build(command)).collect(Collectors.toList());
                    result.add(new TimerTask(
                        config.getString("Timer." + configPath + ".Name"),
                        () -> commands.stream().forEach(command -> command.executeCommand(Bukkit.getConsoleSender())),
                        (long) Math.floor(config.getDouble("Timer." + configPath + ".Interval", 0) / delay))
                    );
                }
            });
        return result;
    }
}