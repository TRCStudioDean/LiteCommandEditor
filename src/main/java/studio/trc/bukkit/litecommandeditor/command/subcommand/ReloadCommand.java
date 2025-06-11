package studio.trc.bukkit.litecommandeditor.command.subcommand;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommand;
import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommandType;
import studio.trc.bukkit.litecommandeditor.util.PluginControl;

public class ReloadCommand
    implements LiteCommandEditorSubCommand
{
    @Override
    public void execute(CommandSender sender, String... args) {
        PluginControl.reloadPlugin(sender);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public LiteCommandEditorSubCommandType getCommandType() {
        return LiteCommandEditorSubCommandType.RELOAD;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String subCommand, String... args) {
        return new ArrayList<>();
    }
}
