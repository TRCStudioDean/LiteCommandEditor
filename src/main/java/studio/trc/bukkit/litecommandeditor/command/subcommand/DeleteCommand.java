package studio.trc.bukkit.litecommandeditor.command.subcommand;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorCommand;
import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommand;
import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommandType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandExecutor;
import studio.trc.bukkit.litecommandeditor.module.CommandManager;

public class DeleteCommand
    implements LiteCommandEditorSubCommand
{
    @Override
    public void execute(CommandSender sender, String... args) {
        if (args.length == 1) {
            MessageUtil.sendCommandMessage(sender, "Delete.Usage");
            return;
        }
        Command command = CommandManager.getServerCommandMap().getCommand(args[1].toLowerCase());
        if (command != null && command instanceof CommandExecutor) {
            LiteCommandEditorCommand.getSubCommands().get("unload").execute(sender, "unload", ((CommandExecutor) command).getCommandConfig().getFileName());
        } else if (CommandManager.unregisterCommand(sender, args[1])) {
            CommandManager.syncCommands();
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{command}", args[1]);
            MessageUtil.sendCommandMessage(sender, "Delete.Successful", placeholders);
        }
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String subCommand, String... args) {
        Set<String> commands = new LinkedHashSet();
        commands.addAll(CommandManager.getServerCommands().keySet());
        return getTabElements(args, args.length, commands);
    }

    @Override
    public LiteCommandEditorSubCommandType getCommandType() {
        return LiteCommandEditorSubCommandType.DELETE;
    }
}
