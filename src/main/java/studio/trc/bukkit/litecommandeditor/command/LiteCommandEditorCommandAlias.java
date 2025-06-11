package studio.trc.bukkit.litecommandeditor.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.configuration.RobustConfiguration;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandExecutor;
import studio.trc.bukkit.litecommandeditor.module.CommandManager;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class LiteCommandEditorCommandAlias
    extends Command
{
    @Getter
    private final String commandPrefix;
    @Getter
    private final String commandName;
    @Getter
    private final String originalCommand;
    @Getter
    private final List<String> defaultArguments;
    
    public LiteCommandEditorCommandAlias(String commandName, String commandPrefix, String originalCommand, List<String> defaultArguments) {
        super(commandName);
        this.commandPrefix = commandPrefix;
        this.commandName = commandName;
        this.originalCommand = originalCommand;
        this.defaultArguments = defaultArguments;
    }
    
    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        String convertedCommandName = MessageUtil.replacePlaceholders(sender, originalCommand, placeholders);
        List<String> convertedDefaultArguments = defaultArguments.stream().map(text -> MessageUtil.replacePlaceholders(sender, text, placeholders)).collect(Collectors.toList());
        Command command = CommandManager.getServerCommandMap().getCommand(convertedCommandName);
        if (command != null) {
            List<String> executeCommandArguments = new ArrayList<>();
            executeCommandArguments.addAll(convertedDefaultArguments);
            executeCommandArguments.addAll(Arrays.asList(args));
            if (command instanceof CommandExecutor) {
                CommandExecutor executor = (CommandExecutor) command;
                executor.execute(sender, convertedCommandName, executeCommandArguments.toArray(new String[0]));
            } else if (command instanceof PluginCommand) {
                PluginCommand pluginCommand = (PluginCommand) command;
                if (pluginCommand.getPlugin().isEnabled()) {
                    pluginCommand.getExecutor().onCommand(sender, command, convertedCommandName, executeCommandArguments.toArray(new String[0]));
                }
            } else {
                if (command.testPermissionSilent(sender)) {
                    command.execute(sender, convertedCommandName, executeCommandArguments.toArray(new String[0]));
                } else {
                    placeholders.put("{permission}", command.getPermission());
                    MessageUtil.sendMessage(sender, ConfigurationType.MESSAGES.getRobustConfig(), "No-Permission-For-Alias-Command", placeholders);
                }
            }
        } else if (LiteCommandEditorUtils.isPlayer(sender, true)) {
            Player player = (Player) sender;
            List<String> perform = new ArrayList<>();
            perform.add(convertedCommandName); 
            convertedDefaultArguments.stream().forEach(perform::add);
            Arrays.stream(args).forEach(perform::add);
            player.performCommand(String.join(" ", perform));
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        String convertedCommandName = MessageUtil.replacePlaceholders(sender, originalCommand, placeholders);
        List<String> convertedDefaultArguments = defaultArguments.stream().map(text -> MessageUtil.replacePlaceholders(sender, text, placeholders)).collect(Collectors.toList());
        Command command = CommandManager.getServerCommandMap().getCommand(convertedCommandName);
        if (command != null) {
            List<String> executeCommandArguments = new ArrayList<>();
            executeCommandArguments.addAll(convertedDefaultArguments);
            executeCommandArguments.addAll(Arrays.asList(args));
            if (command instanceof CommandExecutor) {
                CommandExecutor executor = (CommandExecutor) command;
                executor.execute(sender, convertedCommandName, executeCommandArguments.toArray(new String[0]));
            } else if (command instanceof PluginCommand) {
                PluginCommand pluginCommand = (PluginCommand) command;
                if (pluginCommand.getPlugin().isEnabled() && pluginCommand.getTabCompleter() != null) {
                    return pluginCommand.getTabCompleter().onTabComplete(sender, command, convertedCommandName, executeCommandArguments.toArray(new String[0]));
                }
            } else {
                return command.tabComplete(sender, convertedCommandName, executeCommandArguments.toArray(new String[0]));
            }
        }
        List<String> onlines = Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
        List<String> names = new ArrayList<>();
        onlines.stream().filter(argument -> argument.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).forEach(names::add);
        return names;
    }
    
    public static List<LiteCommandEditorCommandAlias> getCommandAliases() {
        RobustConfiguration config = ConfigurationType.ALIAS.getRobustConfig();
        return config.getConfigurationSection("Aliases").getKeys(false).stream().map(section -> {
            String commandPrefix;
            String commandName;
            if (section.contains(":")) {
                String[] paragraph = section.split(":", 2);
                commandPrefix = paragraph[0];
                commandName = paragraph[1];
            } else {
                commandPrefix = "litecommandeditor";
                commandName = section;
            }
            String[] arguments = config.getString("Aliases." + section).split(" ", -1);
            List<String> defaultArguments = new ArrayList<>();
            for (int slot = 1;slot < arguments.length;slot++) {
                defaultArguments.add(arguments[slot]);
            }
            return new LiteCommandEditorCommandAlias(commandName, commandPrefix, arguments[0], defaultArguments);
        }).collect(Collectors.toList());
    }
}
