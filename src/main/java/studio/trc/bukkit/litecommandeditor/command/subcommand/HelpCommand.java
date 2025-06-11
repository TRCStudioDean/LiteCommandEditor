package studio.trc.bukkit.litecommandeditor.command.subcommand;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommand;
import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommandType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;

public class HelpCommand
    implements LiteCommandEditorSubCommand
{
    @Override
    public void execute(CommandSender sender, String... args) {
        MessageUtil.sendCommandMessage(sender, "Help");
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public LiteCommandEditorSubCommandType getCommandType() {
        return LiteCommandEditorSubCommandType.HELP;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String subCommand, String... args) {
        return new ArrayList<>();
    }
}
