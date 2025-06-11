package studio.trc.bukkit.litecommandeditor.command.subcommand;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommand;
import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommandType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandLoader;
import studio.trc.bukkit.litecommandeditor.module.CommandManager;

public class UnloadCommand 
    implements LiteCommandEditorSubCommand
{
    @Override
    public void execute(CommandSender sender, String... args) {
        if (args.length == 1) {
            MessageUtil.sendCommandMessage(sender, "Unload.Usage");
            return;
        }
        String fileName = args[1];
        if (!fileName.toLowerCase().endsWith(".yml")) {
            fileName += ".yml";
        }
        if (CommandManager.unregisterCustomCommand(sender, fileName)) {
            CommandManager.syncCommands();
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{fileName}", args[1]);
            MessageUtil.sendCommandMessage(sender, "Unload.Successful", placeholders);
        }
    }

    @Override
    public String getName() {
        return "unload";
    }

    @Override
    public LiteCommandEditorSubCommandType getCommandType() {
        return LiteCommandEditorSubCommandType.UNLOAD;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String subCommand, String... args) {
        return getTabElements(args, args.length, CommandLoader.getCache().keySet());
    }
}