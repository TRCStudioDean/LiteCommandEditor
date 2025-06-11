package studio.trc.bukkit.litecommandeditor.command.subcommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommand;
import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommandType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.CommandLoader;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class ListCommand 
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
        
        if (CommandLoader.getCache().isEmpty()) {
            MessageUtil.sendCommandMessage(sender, "List.Empty");
            return;
        }
        int numberOfSinglePage = LiteCommandEditorUtils.isInteger(MessageUtil.getProtectedMessage("Command-Messages.List.Number-Of-Single-Page")) ? Integer.valueOf(MessageUtil.getProtectedMessage("Command-Messages.List.Number-Of-Single-Page")) : 9;
        List<CommandConfiguration> commandConfigs = new ArrayList(CommandLoader.getCache().values());
        int arraySize = commandConfigs.size();
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
        for (String message : MessageUtil.getMessageList("Command-Messages.List.List")) {
            if (message.toLowerCase().contains("!list!")) {
                for (int count = page * numberOfSinglePage - numberOfSinglePage + 1; count <= arraySize && count <= page * numberOfSinglePage; count++) {
                    placeholders.put("{number}", String.valueOf(count));
                    placeholders.put("{command}", commandConfigs.get(count - 1).getCommandName());
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
        return LiteCommandEditorSubCommandType.LIST;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String subCommand, String... args) {
        return new ArrayList<>();
    }
}
