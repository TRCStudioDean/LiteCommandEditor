package studio.trc.bukkit.litecommandeditor.command.subcommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommand;
import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommandType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandExecutor;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class DebugCommand 
    implements LiteCommandEditorSubCommand
{
    @Override
    public void execute(CommandSender sender, String... args) {
        if (args.length == 1) {
            MessageUtil.sendCommandMessage(sender, "Debug.Help");
        } else {
            String subCommandType = args[1];
            if (subCommandType.equalsIgnoreCase("switch")) {
                command_switch(sender, args);
            } else if (subCommandType.equalsIgnoreCase("view")) {
                command_view(sender, args);
            } else if (subCommandType.equalsIgnoreCase("list")) {
                command_list(sender, args);
            } else {
                MessageUtil.sendCommandMessage(sender, "Debug.Help");
            }
        }
    }

    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public LiteCommandEditorSubCommandType getCommandType() {
        return LiteCommandEditorSubCommandType.DEBUG;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String subCommand, String... args) {
        String subCommandType = args[1];
        if (args.length <= 2) {
            return getTabElements(args, 2, Arrays.stream(SubCommandType.values())
                    .filter(type -> LiteCommandEditorUtils.hasCommandPermission(sender, type.getCommandPermissionPath(), false))
                    .map(type -> type.getCommandName())
                    .collect(Collectors.toList()));
        } else {
            if (subCommandType.equalsIgnoreCase("view") && LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.VIEW.commandPermissionPath, false)) {
                return getTabElements(args, 3, CommandExecutor.getDebugRecords().keySet());
            }
        }
        return new ArrayList<>();
    }
    
    private void command_switch(CommandSender sender, String[] args) {
        if (!LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.SWITCH.commandPermissionPath, true)) {
            return;
        }
        CommandExecutor.setDebug(!CommandExecutor.isDebug());
        MessageUtil.sendCommandMessage(sender, CommandExecutor.isDebug() ? "Debug.Switch.Switch-On" : "Debug.Switch.Switch-Off");
    }
    
    private void command_view(CommandSender sender, String[] args) {
        if (!LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.VIEW.commandPermissionPath, true)) {
            return;
        }
        if (args.length == 2) {
            MessageUtil.sendCommandMessage(sender, "Debug.View.Usage");
        } else {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            Map<String, List<CommandExecutor.DebugRecord>> records = CommandExecutor.getDebugRecords();
            String[] recordInfo = records.keySet().stream().collect(Collectors.toList()).toArray(new String[] {});
            if (records.containsKey(args[2])) {
                records.get(args[2]).stream().forEach(record -> record.sendCommandTrack(sender));
            } else if (LiteCommandEditorUtils.isInteger(args[2]) && Integer.valueOf(args[2]) <= recordInfo.length && Integer.valueOf(args[2]) > 0) {
                records.get(recordInfo[Integer.valueOf(args[2]) - 1]).stream().forEach(record -> record.sendCommandTrack(sender));
            } else {
                placeholders.put("{record}", args[2]);
                MessageUtil.sendCommandMessage(sender, "Debug.View.Not-Exist", placeholders);
            }
        }
    }
    
    private void command_list(CommandSender sender, String[] args) {
        if (!LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.LIST.commandPermissionPath, true)) {
            return;
        }
        int page;
        if (args.length >= 3) {
            if (!LiteCommandEditorUtils.isInteger(args[2])) {
                LiteCommandEditorUtils.notANumber(sender, args[2]);
                return;
            }
            page = Integer.valueOf(args[2]);
        } else {
            page = 1;
        }
        Map<String, List<CommandExecutor.DebugRecord>> records = CommandExecutor.getDebugRecords();
        String[] recordInfo = records.keySet().stream().collect(Collectors.toList()).toArray(new String[] {});
        if (records.isEmpty()) {
            MessageUtil.sendCommandMessage(sender, "Debug.List.Empty");
            return;
        }
        int numberOfSinglePage = LiteCommandEditorUtils.isInteger(MessageUtil.getRobustMessage("Command-Messages.Debug.List.Number-Of-Single-Page")) ? Integer.valueOf(MessageUtil.getRobustMessage("Command-Messages.Debug.List.Number-Of-Single-Page")) : 9;
        int arraySize = records.size();
        int maxPage = arraySize % numberOfSinglePage == 0 ? arraySize / numberOfSinglePage : arraySize / numberOfSinglePage + 1;
        if (page > maxPage) {
            page = maxPage;
        }
        if (page < 1) {
            page = 1;
        }
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("!list!", "");
        placeholders.put("{total}", String.valueOf(arraySize));
        placeholders.put("{page}", String.valueOf(page));
        placeholders.put("{previousPage}", String.valueOf(page == 1 ? maxPage : page - 1));
        placeholders.put("{nextPage}", String.valueOf(page == maxPage ? 1 : page + 1));
        placeholders.put("{maxPage}", String.valueOf(maxPage));
        for (String message : MessageUtil.getMessageList("Command-Messages.Debug.List.List")) {
            if (message.toLowerCase().contains("!list!")) {
                for (int count = page * numberOfSinglePage - numberOfSinglePage + 1; count <= arraySize && count <= page * numberOfSinglePage; count++) {
                    placeholders.put("{number}", String.valueOf(count));
                    placeholders.put("{record}", recordInfo[count - 1]);
                    MessageUtil.sendMessage(sender, message, placeholders);
                }
            } else {
                MessageUtil.sendMessage(sender, message, placeholders);
            }
        }
    }
    
    public enum SubCommandType {
        /**
         * /lce debug switch
         */
        SWITCH("switch", "Commands.Debug.Switch"),
        
        /**
         * /lce debug view
         */
        VIEW("view", "Commands.Debug.View"),
        
        /**
         * /lce debug list
         */
        LIST("list", "Commands.Debug.List");
        
        @Getter
        private final String commandName;
        @Getter
        private final String commandPermissionPath;
        
        private SubCommandType(String commandName, String commandPermissionPath) {
            this.commandName = commandName;
            this.commandPermissionPath = commandPermissionPath;
        }
    }
}
