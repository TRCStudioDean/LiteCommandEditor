package studio.trc.bukkit.litecommandeditor.module.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import studio.trc.bukkit.litecommandeditor.event.CommandFunctionEvent;
import studio.trc.bukkit.litecommandeditor.event.CommandTaskEvent;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandCondition;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.CommandExecutor;
import studio.trc.bukkit.litecommandeditor.module.function.ActionBar;
import studio.trc.bukkit.litecommandeditor.module.function.ClientSound;
import studio.trc.bukkit.litecommandeditor.module.function.Command;
import studio.trc.bukkit.litecommandeditor.module.function.Configurator;
import studio.trc.bukkit.litecommandeditor.module.function.PlayerFunction;
import studio.trc.bukkit.litecommandeditor.module.function.RewardItem;
import studio.trc.bukkit.litecommandeditor.module.function.ServerFunction;
import studio.trc.bukkit.litecommandeditor.module.function.ServerTeleport;
import studio.trc.bukkit.litecommandeditor.module.function.TakeItem;
import studio.trc.bukkit.litecommandeditor.module.function.Title;
import studio.trc.bukkit.litecommandeditor.module.function.WorldFunction;
import studio.trc.bukkit.litecommandeditor.module.tool.Function;
import studio.trc.bukkit.litecommandeditor.module.tool.Sortable;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;

public final class CommandFunction
    extends Sortable
    implements Function
{
    /*
     Essentials
    */
    @Getter
    private final boolean breakFunction;
    @Getter
    private final int priority;
    @Getter
    private final String fileName;
    @Getter
    private final String configPath;
    @Getter
    private final YamlConfiguration config;
    @Getter
    private final CommandConfiguration commandConfig;
    @Getter
    private final List<CommandCondition.Schedule> conditions;
    
    /*
     Executables
    */
    @Getter
    private final List<String> permission;
    @Getter
    private final List<String> messages;
    @Getter
    private final List<String> broadcastMessages;
    @Getter
    private final List<Command> commands;
    @Getter
    private final List<Title> titles;
    @Getter
    private final List<ActionBar> actionBars;
    @Getter
    private final List<ClientSound> sounds;
    @Getter
    private final List<ServerTeleport> servers;
    @Getter
    private final List<RewardItem> rewardItems;
    @Getter
    private final List<TakeItem> takeItems;
    @Getter
    private final List<Configurator> configuratorFunctions;
    @Getter
    private final List<PlayerFunction> playerFunctions;
    @Getter
    private final List<WorldFunction> worldFunctions;
    @Getter
    private final List<ServerFunction> serverFunctions;
    @Getter
    private final List<CommandFunction> functions;
    @Getter
    private final CommandCompoundFunctionList compoundFunctions;
    @Getter
    private final List<CommandCompoundFunctionType> taskSequence;
    @Getter
    private final Map<String, String> cachePlaceholders;
    
    /*
     Attributes
     */
    
    public CommandFunction(CommandConfiguration commandConfig, String fileName, YamlConfiguration config, String configPath) {
        this.fileName = fileName;
        this.config = config;
        this.configPath = configPath;
        this.commandConfig = commandConfig;
        ConfigurationSection section = config.getConfigurationSection(configPath);
        breakFunction = section.getBoolean("Break", false);
        priority = section.getInt("Priority", 0);
        permission = MessageUtil.getMessageList(config, configPath + ".Permission");
        conditions = getStringList(section, "Conditions") != null ? getStringList(section, "Conditions").stream().map(syntax -> CommandCondition.getCommandConditions(CommandFunction.this, syntax)).collect(Collectors.toList()) : new ArrayList<>();
        messages = MessageUtil.getMessageList(config, configPath + ".Messages");
        broadcastMessages = MessageUtil.getMessageList(config, configPath + ".Broadcast");
        commands = getStringList(section, "Commands") != null ? getStringList(section, "Commands").stream().map(command -> Command.build(command)).collect(Collectors.toList()) : new ArrayList<>();
        titles = getList(section, "Titles") != null ? Title.build((List<Map>) getList(section, "Titles"), fileName, configPath) : new ArrayList<>();
        actionBars = getList(section, "Action-Bars") != null ? ActionBar.build((List<Map>) getList(section, "Action-Bars"), fileName, configPath) : new ArrayList<>();
        sounds = getList(section, "Sounds") != null ? ClientSound.build((List<Map>) getList(section, "Sounds"), fileName, configPath) : new ArrayList<>();
        servers = getStringList(section, "Server-Teleport") != null ? ServerTeleport.build(CommandFunction.this, getStringList(section, "Server-Teleport"), configPath + ".Server-Teleport") : new ArrayList<>();
        rewardItems = getStringList(section, "Reward-Items") != null ? RewardItem.build(CommandFunction.this, getStringList(section, "Reward-Items"), configPath + ".Reward-Items") : new ArrayList<>();
        takeItems = getStringList(section, "Take-Items") != null ? TakeItem.build(CommandFunction.this, getStringList(section, "Take-Items"), configPath + ".Take-Items") : new ArrayList<>();
        cachePlaceholders = section.get("Set-Placeholders") != null ? setPlaceholders(fileName, config, configPath + ".Set-Placeholders") : new HashMap<>();
        configuratorFunctions = getStringList(section, "Configurator") != null ? Configurator.build(CommandFunction.this, getStringList(section, "Configurator"), configPath + ".Configurator") : new ArrayList<>();
        playerFunctions = getStringList(section, "Player-Functions") != null ? PlayerFunction.build(CommandFunction.this, getStringList(section, "Player-Functions"), configPath + ".Player-Functions") : new ArrayList<>();
        worldFunctions = getStringList(section, "World-Functions") != null ? WorldFunction.build(CommandFunction.this, getStringList(section, "World-Functions"), configPath + ".World-Functions") : new ArrayList<>();
        serverFunctions = getStringList(section, "Server-Functions") != null ? ServerFunction.build(CommandFunction.this, getStringList(section, "Server-Functions"), configPath + ".Server-Functions") : new ArrayList<>();
        functions = section.get("Functions") != null ? build(commandConfig, fileName, config, configPath + ".Functions") : new ArrayList<>();
        compoundFunctions = section.get("Compound-Functions") != null ? CommandCompoundFunctionList.build(CommandFunction.this, fileName, config, configPath + ".Compound-Functions") : null;
        taskSequence = section.get("Sequence") != null ? setSequence(fileName, config, configPath + ".Sequence") : null;
    }
    
    public void executeTasks(CommandSender sender, Map<String, String> placeholders, CommandCompoundFunctionType task) {
        CommandTaskEvent event = new CommandTaskEvent(this, sender, placeholders, task);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            switch (task) {
                case ACTION_BAR: {
                    ActionBar.sendActionBarAnimation(actionBars, player, placeholders);
                    break;
                }
                case TITLE: {
                    Title.sendTitleAnimation(titles, player, placeholders);
                    break;
                }
                case SOUND: {
                    ClientSound.sendSounds(sounds, player);
                    break;
                }
                case REWARD_ITEM: {
                    rewardItems.stream().forEach(item -> item.executeTask(sender, placeholders));
                    break;
                }
                case TAKE_ITEM: {
                    takeItems.stream().forEach(item -> item.executeTask(sender, placeholders));
                    break;
                }
            }
        }
        switch (task) {
            case MESSAGE: {
                MessageUtil.sendMessage(sender, messages, placeholders);
                break;
            }
            case BROADCAST: {
                Bukkit.getOnlinePlayers().stream().forEach(player -> MessageUtil.sendMessage(player, broadcastMessages, placeholders));
                MessageUtil.sendMessage(Bukkit.getConsoleSender(), broadcastMessages, placeholders);
                break;
            }
            case COMMAND: {
                commands.stream().forEach(command -> command.executeCommand(sender, placeholders));
                break;
            }
            case SET_PLACEHOLDERS: {
                cachePlaceholders.keySet().stream().forEach(key -> {
                    if (cachePlaceholders.get(key).equals("null")) {
                        MessageUtil.removeDefaultPlaceholder("{" + key + "}");
                        placeholders.remove("{" + key + "}");
                        commandConfig.getCachePlaceholders().remove("{" + key + "}");
                    } else {
                        MessageUtil.addDefaultPlaceholder("{" + key + "}", MessageUtil.replacePlaceholders(sender, cachePlaceholders.get(key), placeholders));
                        placeholders.put("{" + key + "}", MessageUtil.replacePlaceholders(sender, cachePlaceholders.get(key), placeholders));
                        commandConfig.getCachePlaceholders().put("{" + key + "}", MessageUtil.replacePlaceholders(sender, cachePlaceholders.get(key), placeholders));
                    }
                });
                break;
            }
            case CONFIGURATOR: {
                configuratorFunctions.stream().forEach(function -> function.executeTask(sender, placeholders));
                break;
            }
            case SERVER_TELEPORT: {
                servers.stream().forEach(function -> function.executeTask(sender, placeholders));
                break;
            }
            case PLAYER_FUNCTIONS: {
                playerFunctions.stream().forEach(function -> function.executeTask(sender, placeholders));
                break;
            }
            case WORLD_FUNCTIONS: {
                worldFunctions.stream().forEach(function -> function.executeTask(sender, placeholders));
                break;
            }
            case SERVER_FUNCTIONS: {
                serverFunctions.stream().forEach(function -> function.executeTask(sender, placeholders));
                break;
            }
            case COMPOUND_FUNCTION: {
                if (compoundFunctions != null) {
                    compoundFunctions.execute(sender, placeholders);
                }
                break;
            }
            case FUNCTION: {
                for (CommandFunction function : functions) {
                    if (function.executeFunctions(sender, placeholders) && function.breakFunction) {
                        break;
                    }
                }
                break;
            }
        }
    }
    
    public boolean executeFunctions(CommandSender sender, Map<String, String> placeholders) {
        CommandFunctionEvent event = new CommandFunctionEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        //Conditions match
        List results = new ArrayList<>();
        if (!conditions.stream().map(condition -> {
            results.clear();
            for (int i = 0;i < condition.getExpressions().size();i++) {
                if (condition.getExpressions().get(i) instanceof CommandCondition) {
                    results.add(((CommandCondition) condition.getExpressions().get(i)).matchCondition(commandConfig, configPath + ".Conditions", sender, placeholders));
                } else {
                    results.add(condition.getExpressions().get(i));
                }
            }
            return condition.analysis(results);
        }).allMatch(condition -> condition)) {
            return false;
        }
        if (permission.stream().anyMatch(perm -> !sender.hasPermission(perm))) {
            return false;
        }
        //Task execute
        if (taskSequence == null) {
            executeTasks(sender, placeholders, CommandCompoundFunctionType.SET_PLACEHOLDERS);
            executeTasks(sender, placeholders, CommandCompoundFunctionType.CONFIGURATOR);
            executeTasks(sender, placeholders, CommandCompoundFunctionType.MESSAGE);
            executeTasks(sender, placeholders, CommandCompoundFunctionType.BROADCAST);
            executeTasks(sender, placeholders, CommandCompoundFunctionType.COMMAND);
            executeTasks(sender, placeholders, CommandCompoundFunctionType.TITLE);
            executeTasks(sender, placeholders, CommandCompoundFunctionType.ACTION_BAR);
            executeTasks(sender, placeholders, CommandCompoundFunctionType.SOUND);
            executeTasks(sender, placeholders, CommandCompoundFunctionType.REWARD_ITEM);
            executeTasks(sender, placeholders, CommandCompoundFunctionType.TAKE_ITEM);
            executeTasks(sender, placeholders, CommandCompoundFunctionType.SERVER_TELEPORT);
            executeTasks(sender, placeholders, CommandCompoundFunctionType.SERVER_FUNCTIONS);
            executeTasks(sender, placeholders, CommandCompoundFunctionType.PLAYER_FUNCTIONS);
            executeTasks(sender, placeholders, CommandCompoundFunctionType.WORLD_FUNCTIONS);
            executeTasks(sender, placeholders, CommandCompoundFunctionType.COMPOUND_FUNCTION);
            executeTasks(sender, placeholders, CommandCompoundFunctionType.FUNCTION);
        } else {
            taskSequence.stream().forEach(task -> {
                executeTasks(sender, placeholders, task);
            });
        }
        return true;
    }
    
    public String getConditionTypeDisplay(CommandCondition condition) {
        switch (condition.getConditionType()) {
            case COMPARISON: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Comparison");
            case NUMBER: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Number");
            case ITEM: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Item");
            case PERMISSION: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Permission");
            case PLACEHOLDER: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Placeholder");
            case PLAYER: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Player");
            case WORLD: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.World");
            case REGULAR_EXPRESSION: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Regular-Expression");
        }
        return null;
    }
    
    public boolean executeDebug(CommandSender sender, Map<String, String> placeholders, List<CommandExecutor.DebugRecord> record) {
        boolean isReturn = false;
        Map<CommandCondition, Boolean> conditionsInfo = new LinkedHashMap();
        List results = new ArrayList<>();
        for (CommandCondition.Schedule condition : conditions) {
            for (int i = 0;i < condition.getExpressions().size();i++) {
                if (condition.getExpressions().get(i) instanceof CommandCondition) {
                    CommandCondition commandCondition = (CommandCondition) condition.getExpressions().get(i);
                    boolean result = commandCondition.matchCondition(commandConfig, configPath + ".Conditions", sender, placeholders);
                    results.add(result);
                    conditionsInfo.put(commandCondition, result);
                }
            }
            isReturn = condition.analysis(results);
        }
        record.add(new CommandExecutor.DebugRecord(fileName, configPath, commandConfig, new HashMap<>(placeholders), conditionsInfo));
        if (isReturn) {
            return false;
        }
        executeTasks(sender, placeholders, CommandCompoundFunctionType.SET_PLACEHOLDERS);
        for (CommandFunction function : functions) {
            if (function.executeDebug(sender, placeholders, record)) {
                if (function.isBreakFunction()) {
                    break;
                }
            }
        }
        return true;
    }
    
    private List<CommandCompoundFunctionType> setSequence(String fileName, YamlConfiguration config, String configPath) {
        List<CommandCompoundFunctionType> sequence = new ArrayList<>();
        config.getStringList(configPath).stream().forEach(task -> {
            try {
                sequence.add(CommandCompoundFunctionType.valueOf(task.toUpperCase()));
            } catch (Exception ex) {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{task}", task);
                placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                placeholders.put("{configPath}", fileName + ": " + configPath);
                LiteCommandEditorProperties.sendOperationMessage("IllegalTaskName", placeholders);
            }
        });
        return sequence;
    }
    
    private Map<String, String> setPlaceholders(String fileName, YamlConfiguration config, String configPath) {
        Map<String, String> result = new HashMap<>();
        try {
            for (Map<String, Object> maps : (List<Map<String, Object>>) config.getList(configPath)) {
                String placeholder = null;
                String content = null;
                for (String string : maps.keySet()) {
                    placeholder = string;
                    content = maps.get(string) != null ? maps.get(string).toString() : "null";
                    break;
                }
                if (placeholder != null && content != null) result.put(placeholder, content);
            }
        } catch (Exception ex) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
            placeholders.put("{configPath}", fileName + ": " + configPath);
            LiteCommandEditorProperties.sendOperationMessage("LoadingSetPlaceholdersFailed", placeholders);
            ex.printStackTrace();
        }
        return result;
    }
    
    private List getList(ConfigurationSection section, String sectionPath) {
        if (!section.contains(sectionPath)) return null;
        List list = section.getList(sectionPath);
        if (section.contains(sectionPath)) {
            if (list.isEmpty() && !section.getString(sectionPath).equals("[]")) {
                list.add(section.get(sectionPath));
            }
        }
        return list;
    }
    
    private List<String> getStringList(ConfigurationSection section, String sectionPath) {
        if (!section.contains(sectionPath)) return null;
        List<String> list = section.getStringList(sectionPath);
        if (section.contains(sectionPath)) {
            if (list.isEmpty() && !section.getString(sectionPath).equals("[]")) {
                list.add(section.getString(sectionPath));
            }
        }
        return list;
    }

    @Override
    public int compareTo(Sortable sortTarget) {
        if (sortTarget instanceof CommandFunction) {
            CommandFunction target = (CommandFunction) sortTarget;
            return target.priority <= priority ? 1 : -1;
        }
        return -1;
    }
    
    public static List<CommandFunction> build(CommandConfiguration commandConfig, String fileName, YamlConfiguration config, String configPath) {
        List<CommandFunction> commandFunctions = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection(configPath);
        if (section == null) return commandFunctions;
        if (section != null) {
            section.getKeys(false).stream().forEach(function -> commandFunctions.add(new CommandFunction(commandConfig, fileName, config, configPath + "." + function)));
        }
        return sortArray(commandFunctions);
    }
}
