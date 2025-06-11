package studio.trc.bukkit.litecommandeditor.command.subcommand;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;

import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommand;
import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommandType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandExecutor;
import studio.trc.bukkit.litecommandeditor.module.CommandManager;

public class InfoCommand 
    implements LiteCommandEditorSubCommand
{
    @Override
    public void execute(CommandSender sender, String... args) {
        if (args.length == 1) {
            MessageUtil.sendCommandMessage(sender, "Info.Usage");
            return;
        }
        Command command = CommandManager.getServerCommandMap().getCommand(args[1].toLowerCase());
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{command}", args[1]);
        if (command != null) {
            placeholders.put("{command}", command.getName());
            if (command instanceof CommandExecutor) {
                CommandExecutor executor = (CommandExecutor) command;
                placeholders.put("{prefix}", executor.getCommandConfig().getPrefix());
                placeholders.put("{source}", "LiteCommandEditor");
                placeholders.put("{usage}", executor.getCommandConfig().getUsage() != null ? executor.getCommandConfig().getUsage() : MessageUtil.getMessage("Information-Type.None"));
                placeholders.put("{permission}", executor.getCommandConfig().getPermission() != null ? executor.getCommandConfig().getPermission() : MessageUtil.getMessage("Information-Type.None"));
                placeholders.put("{fileName}", executor.getCommandConfig().getFileName());
                placeholders.put("{description}", executor.getCommandConfig().getDescription() != null ? executor.getCommandConfig().getDescription() : MessageUtil.getMessage("Information-Type.None"));
            } else if (command instanceof PluginCommand) {
                PluginCommand executor = (PluginCommand) command;
                placeholders.put("{prefix}", executor.getPlugin().getName().toLowerCase());
                placeholders.put("{source}", executor.getPlugin().getName());
                placeholders.put("{usage}", executor.getUsage() != null ? executor.getUsage() : MessageUtil.getMessage("Information-Type.None"));
                placeholders.put("{permission}", executor.getPermission() != null ? executor.getPermission() : MessageUtil.getMessage("Information-Type.None"));
                placeholders.put("{fileName}", MessageUtil.getMessage("Information-Type.None"));
                placeholders.put("{description}", executor.getDescription() != null ? executor.getDescription() : MessageUtil.getMessage("Information-Type.None"));
            } else {
                try {
                    CommandMap map = CommandManager.getServerCommandMap();
                    Field field = SimpleCommandMap.class.getDeclaredField("knownCommands");
                    field.setAccessible(true);
                    Map<String, Command> knownCommands = (Map<String, Command>) field.get(map);
                    placeholders.put("{prefix}", knownCommands.keySet().stream().filter(name -> name.contains(":") && name.split(":")[1].equalsIgnoreCase(command.getName())).map(name -> name.split(":")[0]).findFirst().orElse(MessageUtil.getMessage("Information-Type.Unknown")));
                    field.setAccessible(false);
                } catch (Exception ex) {
                    placeholders.put("{prefix}", MessageUtil.getMessage("Information-Type.Unknown"));
                }
                placeholders.put("{source}", command.getClass().getSimpleName());
                placeholders.put("{usage}", command.getUsage() != null ? command.getUsage() : MessageUtil.getMessage("Information-Type.None"));
                placeholders.put("{permission}", command.getPermission() != null ? command.getPermission() : MessageUtil.getMessage("Information-Type.None"));
                placeholders.put("{fileName}", MessageUtil.getMessage("Information-Type.None"));
                placeholders.put("{description}", command.getDescription() != null ? command.getDescription() : MessageUtil.getMessage("Information-Type.None"));
            }
            MessageUtil.sendCommandMessage(sender, "Info.Informations", placeholders);
        } else {
            MessageUtil.sendCommandMessage(sender, "Info.Unknown", placeholders);
        }
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public LiteCommandEditorSubCommandType getCommandType() {
        return LiteCommandEditorSubCommandType.INFO;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String subCommand, String... args) {
        Set<String> commands = new LinkedHashSet();
        commands.addAll(CommandManager.getServerCommands().keySet());
        return getTabElements(args, args.length, commands);
    }
}
