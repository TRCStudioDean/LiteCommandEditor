package studio.trc.bukkit.litecommandeditor.command;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;
import studio.trc.bukkit.litecommandeditor.module.tool.Updater;

public class LiteCommandEditorCommand
    implements CommandExecutor, TabCompleter
{
    @Getter
    private static final Map<String, LiteCommandEditorSubCommand> subCommands = new HashMap<>();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        checkUpdate();
        if (args.length == 0) {
            MessageUtil.sendCommandMessage(sender, "Help");
        } else if (args.length >= 1) {
            callSubCommand(sender, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return getDefaultTabComplete(sender, args[0]);
        } else if (args.length > 1) {
            return tabComplete(sender, args);
        } else {
            return new ArrayList<>();
        }
    }
    
    private void callSubCommand(CommandSender sender, String[] args) {
        String subCommand = args[0].toLowerCase();
        LiteCommandEditorSubCommandType type = LiteCommandEditorSubCommandType.getCommandType(subCommand);
        if (type == null) {
            MessageUtil.sendCommandMessage(sender, "Unknown-Command");
            return;
        }
        if (!LiteCommandEditorUtils.hasCommandPermission(sender, type.getCommandPermissionPath(), true)) return;
        LiteCommandEditorSubCommandType.getCommandType(subCommand).getSubCommand().execute(sender, args);
    }
    
    private List<String> getDefaultTabComplete(CommandSender sender, String args) {
        return Arrays.stream(LiteCommandEditorSubCommandType.values())
                .filter(command -> LiteCommandEditorUtils.hasCommandPermission(sender, command.getCommandPermissionPath(), false))
                .map(command -> command.getSubCommandName())
                .filter(command -> command.toLowerCase().startsWith(args.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    private List<String> tabComplete(CommandSender sender, String[] args) {
        String subCommand = args[0].toLowerCase();
        if (LiteCommandEditorSubCommandType.getCommandType(subCommand) == null) {
            return new ArrayList<>();
        }
        LiteCommandEditorSubCommand command = LiteCommandEditorSubCommandType.getCommandType(subCommand).getSubCommand();
        return LiteCommandEditorUtils.hasCommandPermission(sender, command.getCommandType().getCommandPermissionPath(), false) ? command.tabComplete(sender, subCommand, args) : new ArrayList<>();
    }
    
    private void checkUpdate() {
        if (Updater.getDate() == null) return;
        String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String checkUpdateTime = new SimpleDateFormat("yyyy-MM-dd").format(Updater.getDate());
        if (!now.equals(checkUpdateTime)) {
            Updater.checkUpdate();
        }
    }
}
