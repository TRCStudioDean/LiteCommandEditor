package studio.trc.bukkit.litecommandeditor.command.subcommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommand;
import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommandType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandExecutor;
import studio.trc.bukkit.litecommandeditor.module.CommandManager;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class ListAllCommand 
    implements LiteCommandEditorSubCommand
{
    @Override
    public void execute(CommandSender sender, String... args) {
        int page;
        if (args.length >= 2) {
            if (!LiteCommandEditorUtils.isInteger(args[1])) {
                LiteCommandEditorUtils.notANumber(sender, args[1]);
                return;
            }
            page = Integer.valueOf(args[1]);
        } else {
            page = 1;
        }
        int numberOfSinglePage = LiteCommandEditorUtils.isInteger(MessageUtil.getRobustMessage("Command-Messages.List-All.Number-Of-Single-Page")) ? Integer.valueOf(MessageUtil.getRobustMessage("Command-Messages.List-All.Number-Of-Single-Page")) : 9;
        List<Command> commands = new ArrayList(new HashSet(CommandManager.getServerCommands().values()));
        int arraySize = commands.size();
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
        for (String message : MessageUtil.getMessageList("Command-Messages.List-All.List")) {
            if (message.toLowerCase().contains("!list!")) {
                for (int count = page * numberOfSinglePage - numberOfSinglePage + 1; count <= arraySize && count <= page * numberOfSinglePage; count++) {
                    placeholders.put("{number}", String.valueOf(count));
                    Command command = commands.get(count - 1);
                    if (command instanceof CommandExecutor) {
                        placeholders.put("{plugin}", "LiteCommandEditor");
                    } else if (command instanceof PluginCommand) {
                        placeholders.put("{plugin}", ((PluginCommand) command).getPlugin().getName());
                    } else {
                        placeholders.put("{plugin}", command.getClass().getSimpleName());
                    }
                    placeholders.put("{command}", command.getName());
                    MessageUtil.sendMessage(sender, message, placeholders);
                }
            } else {
                MessageUtil.sendMessage(sender, message, placeholders);
            }
        }
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public LiteCommandEditorSubCommandType getCommandType() {
        return LiteCommandEditorSubCommandType.LIST_ALL;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String subCommand, String... args) {
        return new ArrayList<>();
    }
}