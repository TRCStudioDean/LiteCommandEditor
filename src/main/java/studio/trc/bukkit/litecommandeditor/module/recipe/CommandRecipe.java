package studio.trc.bukkit.litecommandeditor.module.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.tab.TabRecipe;

public class CommandRecipe
    extends TabRecipe
{
    public CommandRecipe(String expression) {
        super(keyWordsReplace(expression));
    }
    
    @Override
    public List<String> getRecipes(CommandSender sender, Map<String, String> placeholders) {
        List<String> names = new ArrayList<>();
        //Raw command contents.
        String[] commandArguments = getExpression().split(" ", -1);
        //Converted main command.
        String convertedCommandName = MessageUtil.replacePlaceholders(sender, commandArguments[0], placeholders, true, false);
        //Converted sub commands.
        List<String> defaultArguments = new ArrayList<>();
        for (int slot = 1;slot < commandArguments.length;slot++) {
            defaultArguments.add(commandArguments[slot]);
        }
        List<String> convertedDefaultArguments = defaultArguments.stream().map(text -> MessageUtil.replacePlaceholders(sender, text, placeholders, true, false)).collect(Collectors.toList());
        //Get command from server command map.
        Command command = getServerCommandMap().getCommand(convertedCommandName);
        if (command != null) {
            if (command instanceof PluginCommand) {
                PluginCommand pluginCommand = (PluginCommand) command;
                if (pluginCommand.getPlugin().isEnabled()) {
                    return pluginCommand.getTabCompleter().onTabComplete(sender, command, convertedCommandName, convertedDefaultArguments.toArray(new String[0]));
                }
            } else {
                return command.tabComplete(sender, convertedCommandName, convertedDefaultArguments.toArray(new String[0]));
            }
        }
        return names;
    }
    
    private static String keyWordsReplace(String expression) {
        if (expression.toLowerCase().startsWith("command:")) {
            expression = expression.substring("command:".length());
        }
        return expression;
    }
    
    public static CommandMap getServerCommandMap() {
        try {
            return (CommandMap) Bukkit.getServer().getClass().getMethod("getCommandMap").invoke(Bukkit.getServer());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
