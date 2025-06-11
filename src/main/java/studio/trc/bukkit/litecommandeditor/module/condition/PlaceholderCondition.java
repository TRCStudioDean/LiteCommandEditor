package studio.trc.bukkit.litecommandeditor.module.condition;

import java.util.Map;

import lombok.Getter;

import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.module.CommandCondition;
import studio.trc.bukkit.litecommandeditor.module.CommandConditionType;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.tool.Function;

public class PlaceholderCondition 
    extends CommandCondition
{   
    @Getter
    private final CommandConditionType conditionType = CommandConditionType.PLACEHOLDER;
    @Getter
    private final Function function;
    
    public PlaceholderCondition(Function function, String expression) {
        super(keyWordsReplace(expression));
        this.function = function;
    }

    @Override
    public boolean matchCondition(CommandConfiguration commandConfig, String configPath, CommandSender sender, Map<String, String> placeholders) {
        return getExpression().startsWith("!") ? placeholders.keySet().stream().allMatch(placeholder -> !getExpression().substring(1).equalsIgnoreCase(placeholder)) : placeholders.keySet().stream().anyMatch(placeholder -> getExpression().equalsIgnoreCase(placeholder));
    }
    
    private static String keyWordsReplace(String expression) {
        if (expression.toLowerCase().startsWith("hasplaceholder:")) {
            expression = expression.substring("hasplaceholder:".length());
        } else if (expression.toLowerCase().startsWith("!hasplaceholder:")) {
            expression = "!" + expression.substring("!hasplaceholder:".length());
        }
        return expression.replace(" ", "");
    }
}
