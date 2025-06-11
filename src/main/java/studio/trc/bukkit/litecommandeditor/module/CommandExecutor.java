package studio.trc.bukkit.litecommandeditor.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorCommand;
import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunction;
import studio.trc.bukkit.litecommandeditor.module.tab.TabFunction;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;
import studio.trc.bukkit.litecommandeditor.util.PermissionManager;

public class CommandExecutor
    extends Command
{
    @Getter
    private static final Map<String, List<DebugRecord>> debugRecords = new HashMap<>();
    
    @Getter
    @Setter
    private static boolean debug = false;
    
    @Getter
    private final CommandConfiguration commandConfig;
    
    public CommandExecutor(CommandConfiguration commandConfig) {
        super(commandConfig.getCommandName());
        this.commandConfig = commandConfig;
        commandConfig.setExecutor(CommandExecutor.this);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (commandConfig.getPermission() != null && !sender.hasPermission(commandConfig.getPermission())) {
            LiteCommandEditorUtils.noPermission(sender);
            return false;
        }
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        putPlaceholders(placeholders, sender, label, args);
        if (!debug) {
            for (CommandFunction function : commandConfig.getCommandFunctions()) {
                if (function.executeFunctions(sender, placeholders) && function.isBreakFunction()) {
                    break;
                }
            }
        } else {
            List<DebugRecord> record = new ArrayList<>();
            for (CommandFunction function : commandConfig.getCommandFunctions()) {
                if (function.executeDebug(sender, placeholders, record) && function.isBreakFunction()) {
                    break;
                }
            }
            StringBuilder builder = new StringBuilder();
            builder.append(label);
            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    builder.append("_");
                }
                builder.append(args[i]);
                if (i != args.length - 1) {
                    builder.append("_");
                }
            }
            placeholders.put("{sender}", sender.getName());
            placeholders.put("{command}", commandConfig.getCommandName());
            placeholders.put("{record}", builder.toString());
            debugRecords.put(MessageUtil.replacePlaceholders(MessageUtil.getMessage(ConfigurationType.MESSAGES, "Debug-Record-Format.Command"), placeholders), record);
            if (PermissionManager.hasPermission(sender, ConfigurationType.PERMISSIONS, "Commands.Debug.View")) {
                LiteCommandEditorCommand.getSubCommands().get("debug").execute(sender, "debug", "view", MessageUtil.replacePlaceholders(MessageUtil.getMessage(ConfigurationType.MESSAGES, "Debug-Record-Format.Command"), placeholders));
            } else {
                MessageUtil.sendCommandMessage(Bukkit.getConsoleSender(), "Debug.View.Command-Executor-Report", placeholders);
                Bukkit.getOnlinePlayers().stream().filter(player -> PermissionManager.hasPermission(player, ConfigurationType.PERMISSIONS, "Commands.Debug.View")).forEach(player -> MessageUtil.sendCommandMessage(player, "Debug.View.Command-Executor-Report", placeholders));
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) throws IllegalArgumentException {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        putPlaceholders(placeholders, sender, label, args);
        List<String> recipes = new ArrayList<>();
        if (!debug) {
            for (TabFunction function : commandConfig.getTabFunctions()) {
                List<String> subRecipes = function.getFunctionRecipes(sender, placeholders);
                if (subRecipes != null) {
                    recipes.addAll(subRecipes);
                    if (function.isBreakFunction()) {
                        break;
                    }
                }
            }
        } else {
            List<DebugRecord> record = new ArrayList<>();
            for (TabFunction function : commandConfig.getTabFunctions()) {
                List<String> subRecipes = function.getFunctionRecipesWithDebug(sender, placeholders, record);
                if (subRecipes != null) {
                    recipes.addAll(subRecipes);
                    if (function.isBreakFunction()) {
                        break;
                    }
                }
            }
            StringBuilder builder = new StringBuilder();
            builder.append(label);
            for (int i = 0; i < args.length; i++) {
                if (i == 0) {
                    builder.append("_");
                }
                builder.append(args[i]);
                if (i != args.length - 1) {
                    builder.append("_");
                }
            }
            placeholders.put("{sender}", sender.getName());
            placeholders.put("{command}", commandConfig.getCommandName());
            placeholders.put("{record}", builder.toString());
            debugRecords.put(MessageUtil.replacePlaceholders(MessageUtil.getMessage(ConfigurationType.MESSAGES, "Debug-Record-Format.Tab"), placeholders), record);
            if (PermissionManager.hasPermission(sender, ConfigurationType.PERMISSIONS, "Commands.Debug.View")) {
                LiteCommandEditorCommand.getSubCommands().get("debug").execute(sender, "debug", "view", MessageUtil.replacePlaceholders(MessageUtil.getMessage(ConfigurationType.MESSAGES, "Debug-Record-Format.Tab"), placeholders));
            } else {
                MessageUtil.sendCommandMessage(Bukkit.getConsoleSender(), "Debug.View.Tab-Completer-Report", placeholders);
                Bukkit.getOnlinePlayers().stream().filter(player -> PermissionManager.hasPermission(player, ConfigurationType.PERMISSIONS, "Commands.Debug.View")).forEach(player -> MessageUtil.sendCommandMessage(player, "Debug.View.Tab-Completer-Report", placeholders));
            }
        }
        return getTabElements(args.length, args, recipes);
    }
    
    private List<String> getTabElements(int length, String[] args, List<String> recipes) {
        if (args.length == length) {
            List<String> elements = new ArrayList<>();
            recipes.stream().filter(command -> command.toLowerCase().startsWith(args[length - 1].toLowerCase())).forEach(command -> elements.add(command));
            return elements;
        }
        return new ArrayList<>();
    }
    
    private void putPlaceholders(Map<String, String> placeholders, CommandSender sender, String label, String[] args) {
        for (int length = 0;length < args.length;length++) {
            placeholders.put("[" + (length + 1) + "]", args[length]);
        }
        placeholders.put("{length}", String.valueOf(args.length));
        placeholders.put("{main_command}", label);
        placeholders.put("{time_millis}", String.valueOf(System.currentTimeMillis()));
        placeholders.put("{arguments}", String.join(" ", args));
        placeholders.put("{sender}", sender.getName());
        placeholders.put("{sender_type}", getCommandSenderType(sender));
    }
    
    private String getCommandSenderType(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return "Console";
        } else if (sender instanceof Player) {
            return "Player";
        } else if (sender instanceof BlockCommandSender) {
            return "CommandBlock";
        } else if (sender instanceof CommandMinecart) {
            return "CommandBlockMinecart";
        } else {
            return sender.getClass().getSimpleName();
        }
    }
    
    public static class DebugRecord {
        private final String fileName;
        private final String configPath;
        private final CommandConfiguration command;
        private final Map<String, String> cachedPlaceholders;
        private final Map<CommandCondition, Boolean> conditionsInfo;
        
        public DebugRecord(String fileName, String configPath, CommandConfiguration command, Map<String, String> cachedPlaceholders, Map<CommandCondition, Boolean> conditionsInfo) {
            this.fileName = fileName;
            this.configPath = configPath;
            this.command = command;
            this.cachedPlaceholders = cachedPlaceholders;
            this.conditionsInfo = conditionsInfo;
        }
        
        public void sendCommandTrack(CommandSender sender) {
            sendTrack(sender, "Command-Messages.Debug.View.Command-Executor-Track");
        }
        
        public void sendTabTrack(CommandSender sender) {
            sendTrack(sender, "Command-Messages.Debug.View.Tab-Completer-Track");
        }
        
        public void sendTrack(CommandSender sender, String path) {
            cachedPlaceholders.put("{fileName}", fileName);
            cachedPlaceholders.put("{configPath}", configPath);
            MessageUtil.getMessageList(path).stream().forEach(message -> {
                if (message.toLowerCase().contains("!conditions!")) {
                    cachedPlaceholders.put("!conditions!", "");
                    conditionsInfo.keySet().stream().forEach(condition -> {
                        cachedPlaceholders.put("{condition}", condition.getExpression());
                        cachedPlaceholders.put("{type}", getConditionTypeDisplay(condition));
                        cachedPlaceholders.put("{pass}", conditionsInfo.get(condition) ? MessageUtil.getMessage("Information-Type.Yes") : MessageUtil.getMessage("Information-Type.No"));
                        String nowPlaceholders = command.getCachePlaceholders().toString().substring(1, command.getCachePlaceholders().toString().length() - 1);
                        cachedPlaceholders.put("%placeholders%", nowPlaceholders.isEmpty() ? MessageUtil.getMessage("Information-Type.None") : nowPlaceholders);
                        MessageUtil.sendMessage(sender, message, cachedPlaceholders);
                    });
                } else {
                    String nowPlaceholders = command.getCachePlaceholders().toString().substring(1, command.getCachePlaceholders().toString().length() - 1);
                    cachedPlaceholders.put("%placeholders%", nowPlaceholders.isEmpty() ? MessageUtil.getMessage("Information-Type.None") : nowPlaceholders);
                    MessageUtil.sendMessage(sender, message, cachedPlaceholders);
                }
            });
        }
        
        private String getConditionTypeDisplay(CommandCondition condition) {
            switch (condition.getConditionType()) {
                case COMPARISON: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Comparison");
                case ITEM: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Item");
                case PERMISSION: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Permission");
                case PLACEHOLDER: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Placeholder");
                case PLAYER: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Player");
                case WORLD: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.World");
                case SERVER: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Server");
            }
            return null;
        }
    }
}
