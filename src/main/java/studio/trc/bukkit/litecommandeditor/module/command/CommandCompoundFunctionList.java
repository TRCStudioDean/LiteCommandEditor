package studio.trc.bukkit.litecommandeditor.module.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.thread.LiteCommandEditorThread;
import studio.trc.bukkit.litecommandeditor.module.function.ActionBar;
import studio.trc.bukkit.litecommandeditor.module.function.Broadcast;
import studio.trc.bukkit.litecommandeditor.module.function.ClientSound;
import studio.trc.bukkit.litecommandeditor.module.function.CommandBatch;
import studio.trc.bukkit.litecommandeditor.module.function.Configurator;
import studio.trc.bukkit.litecommandeditor.module.function.PlayerFunction;
import studio.trc.bukkit.litecommandeditor.module.function.RewardItem;
import studio.trc.bukkit.litecommandeditor.module.function.ServerFunction;
import studio.trc.bukkit.litecommandeditor.module.function.ServerTeleport;
import studio.trc.bukkit.litecommandeditor.module.function.TakeItem;
import studio.trc.bukkit.litecommandeditor.module.function.Title;
import studio.trc.bukkit.litecommandeditor.module.function.WorldFunction;

public class CommandCompoundFunctionList
{
    @Getter
    private final CommandFunction function;
    @Getter
    private final YamlConfiguration config;
    @Getter
    private final String fileName;
    @Getter
    private final String configPath;
    @Getter
    private final List<CommandCompoundFunction> compoundFunctions = new ArrayList<>();
    
    public CommandCompoundFunctionList(CommandFunction function, String fileName, YamlConfiguration config, String configPath) {
        this.function = function;
        this.fileName = fileName;
        this.config = config;
        this.configPath = configPath;
        List<Map> maps = (List<Map>) config.getList(configPath);
        maps.stream().filter(map -> map.get("Type") != null && Arrays.stream(CommandCompoundFunctionType.values()).anyMatch(task -> task.name().equalsIgnoreCase(map.get("Type").toString()))).forEach(map -> {
            switch (CommandCompoundFunctionType.valueOf(map.get("Type").toString().toUpperCase())) {
                case ACTION_BAR: {
                    ActionBar actionBar = ActionBar.build(map, fileName, configPath);
                    if (actionBar != null) {
                        compoundFunctions.add(actionBar);
                    }
                    break;
                }
                case BROADCAST: {
                    Broadcast broadcast = Broadcast.build(map);
                    if (broadcast != null) {
                        compoundFunctions.add(broadcast);
                    }
                    break;
                }
                case COMMAND: {
                    CommandBatch commands = CommandBatch.build(map);
                    if (commands != null) {
                        compoundFunctions.add(commands);
                    }
                    break;
                }
                case DELAY: {
                    CommandFunctionDelay delay = CommandFunctionDelay.build(map);
                    if (delay != null) {
                        compoundFunctions.add(delay);
                    }
                    break;
                }
                case MESSAGE: {
                    compoundFunctions.add((CommandFunctionTask) (CommandSender sender, Map<String, String> placeholders) -> {
                        if (map.get("Messages") != null) {
                            if (map.get("Messages") instanceof Collection) {
                                ((Collection<? extends String>) map.get("Messages")).stream().forEach(message -> MessageUtil.sendMessage(sender, message, placeholders));
                            } else {
                                MessageUtil.sendMessage(sender, map.get("Messages").toString(), placeholders);
                            }
                        }
                    });
                    break;
                }
                case PLAYER_FUNCTIONS: {
                    PlayerFunction playerFunction = PlayerFunction.build(function, map, configPath);
                    if (playerFunction != null) {
                        compoundFunctions.add(playerFunction);
                    }
                    break;
                }
                case WORLD_FUNCTIONS: {
                    WorldFunction worldFunction = WorldFunction.build(function, map, configPath);
                    if (worldFunction != null) {
                        compoundFunctions.add(worldFunction);
                    }
                }
                case SERVER_FUNCTIONS: {
                    ServerFunction serverFunction = ServerFunction.build(function, map, configPath);
                    if (serverFunction != null) {
                        compoundFunctions.add(serverFunction);
                    }
                }
                case REWARD_ITEM: {
                    RewardItem item = RewardItem.build(function, map, configPath);
                    if (item != null) {
                        compoundFunctions.add(item);
                    }
                    break;
                }
                case TAKE_ITEM: {
                    TakeItem item = TakeItem.build(function, map, configPath);
                    if (item != null) {
                        compoundFunctions.add(item);
                    }
                    break;
                }
                case SERVER_TELEPORT: {
                    ServerTeleport serverName = ServerTeleport.build(function, map, configPath);
                    if (serverName != null) {
                        compoundFunctions.add(serverName);
                    }
                    break;
                }
                case CONFIGURATOR: {
                    Configurator configurator = Configurator.build(function, map, configPath);
                    if (configurator != null) {
                        compoundFunctions.add(configurator);
                    }
                    break;
                }
                case SOUND: {
                    ClientSound sound = ClientSound.build(map, fileName, configPath);
                    if (sound != null) {
                        compoundFunctions.add(sound);
                    }
                    break;
                }
                case TITLE: {
                    Title title = Title.build(map, fileName, configPath);
                    if (title != null) {
                        compoundFunctions.add(title);
                    }
                    break;
                }
            }
        });
    }
    
    public void execute(CommandSender sender, Map<String, String> placeholders) {
        if (LiteCommandEditorThread.isRemoveDuplicateDelayedTasks()) {
            LiteCommandEditorThread.getTaskThread().getTasks().removeIf(task -> task.getIdentifier() != null && task.getIdentifier().equals("CompoundFunction:FileName=" + fileName + ",ConfigPath=" + configPath + ",Sender=" + sender.getName()));
        }
        long tick = 0;
        Map<Long, List<CommandFunctionTask>> tasks = new LinkedHashMap();
        for (CommandCompoundFunction compoundFunction : compoundFunctions) {
            if (compoundFunction instanceof CommandFunctionDelay) {
                tick += ((CommandFunctionDelay) compoundFunction).getDelay();
            } else {
                if (tasks.get(tick) == null) {
                    tasks.put(tick, new ArrayList<>());
                }
                List<CommandFunctionTask> taskList = tasks.get(tick);
                taskList.add((CommandFunctionTask) compoundFunction);
            }
        }
        tasks.keySet().stream().forEach(delay -> {
            List<CommandFunctionTask> taskList = tasks.get(delay);
            taskList.stream().forEach(functionTask -> {
                //If delay is 0, execute task synchronously.
                if (delay == 0) {
                    functionTask.executeTask(sender, placeholders);
                } else {
                    LiteCommandEditorThread.runTask(() -> functionTask.executeTask(sender, placeholders), delay, "CompoundFunction:FileName=" + fileName + ",ConfigPath=" + configPath + ",Sender=" + sender.getName());
                }
            });
        });
    }
    
    public static CommandCompoundFunctionList build(CommandFunction function, String fileName, YamlConfiguration config, String configPath) {
        return new CommandCompoundFunctionList(function, fileName, config, configPath);
    }
}
