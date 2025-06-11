package studio.trc.bukkit.litecommandeditor.command.subcommand;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommand;
import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommandType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.CommandLoader;
import studio.trc.bukkit.litecommandeditor.module.CommandManager;

public class LoadCommand 
    implements LiteCommandEditorSubCommand
{
    @Override
    public void execute(CommandSender sender, String... args) {
        if (args.length == 1) {
            MessageUtil.sendCommandMessage(sender, "Load.Usage");
            return;
        }
        String fileName = args[1];
        if (!fileName.toLowerCase().endsWith(".yml")) {
            fileName += ".yml";
        }
        CommandConfiguration.LastCommandConfiguration lastConfig = CommandLoader.getCache().containsKey(fileName) ? CommandLoader.getCache().get(fileName).cloneCommandInfo() : null;
        if (CommandLoader.loadCommandConfiguration(sender, fileName) && CommandManager.registerCustomCommand(sender, fileName, lastConfig)) {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{fileName}", args[1]);
            MessageUtil.sendCommandMessage(sender, "Load.Successful", placeholders);
        }
    }

    @Override
    public String getName() {
        return "load";
    }

    @Override
    public LiteCommandEditorSubCommandType getCommandType() {
        return LiteCommandEditorSubCommandType.LOAD;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String subCommand, String... args) {
        return getTabElements(args, args.length, getFilesInFolder(new File("plugins/LiteCommandEditor/Commands"), new LinkedList()));
    }
    
    private List<String> getFilesInFolder(File folder, List<String> folderPath) {
        List<String> files = new ArrayList<>();
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                folderPath.add(file.getName() + "/");
                files.addAll(getFilesInFolder(file, folderPath));
            } else {
                StringBuilder builder = new StringBuilder();
                folderPath.stream().forEach(folderName -> builder.append(folderName));
                files.add(builder.toString() + file.getName());
            }
        }
        if (!folderPath.isEmpty()) folderPath.remove(folderPath.size() - 1);
        return files;
    }
}
