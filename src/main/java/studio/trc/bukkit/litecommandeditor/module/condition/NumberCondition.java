package studio.trc.bukkit.litecommandeditor.module.condition;

import java.util.Map;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandCondition;
import studio.trc.bukkit.litecommandeditor.module.CommandConditionType;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.tool.Function;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class NumberCondition 
    extends CommandCondition
{
    @Getter
    private final CommandConditionType conditionType = CommandConditionType.NUMBER;
    @Getter
    private final Function function;
    
    public NumberCondition(Function function, String expression) {
        super(keyWordsReplace(expression));
        this.function = function;
    }
    
    @Override
    public boolean matchCondition(CommandConfiguration commandConfig, String configPath, CommandSender sender, Map<String, String> placeholders) {
        String[] parameters;
        if (getExpression().startsWith("!")) {
            parameters = LiteCommandEditorUtils.getConditionParameters(getExpression().substring(1), sender, placeholders);
        } else {
            parameters = LiteCommandEditorUtils.getConditionParameters(getExpression(), sender, placeholders);
        }
        if (parameters.length == 2) {
            if (parameters[0].equalsIgnoreCase("isinteger")) {
                return LiteCommandEditorUtils.isInteger(parameters[1]) ? !getExpression().startsWith("!") : getExpression().startsWith("!");
            } else if (parameters[0].equalsIgnoreCase("isdouble")) {
                return LiteCommandEditorUtils.isDouble(parameters[1]) ? !getExpression().startsWith("!") : getExpression().startsWith("!");
            } else if (parameters[0].equalsIgnoreCase("isbyte")) {
                return LiteCommandEditorUtils.isByte(parameters[1]) ? !getExpression().startsWith("!") : getExpression().startsWith("!");
            } else if (parameters[0].equalsIgnoreCase("isfloat")) {
                return LiteCommandEditorUtils.isFloat(parameters[1]) ? !getExpression().startsWith("!") : getExpression().startsWith("!");
            } else if (parameters[0].equalsIgnoreCase("islong")) {
                return LiteCommandEditorUtils.isLong(parameters[1]) ? !getExpression().startsWith("!") : getExpression().startsWith("!");
            }
        } else {
            placeholders.put("{conditionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Condition-Messages.Conditions-Type.Number"));
            placeholders.put("{fileName}", commandConfig.getFileName());
            placeholders.put("{configPath}", configPath);
            placeholders.put("{expression}", getExpression().startsWith("!") ? "!Number:" + getExpression().substring(1) : "Number:" + getExpression());
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Condition-Messages.Incorrect-Parameters", placeholders);
        }
        return getExpression().startsWith("!");
    }
    
    private static String keyWordsReplace(String expression) {
        if (expression.toLowerCase().startsWith("number:")) {
            expression = expression.substring("number:".length());
        } else if (expression.toLowerCase().startsWith("!number:")) {
            expression = "!" + expression.substring("!number:".length());
        }
        return expression.replace(" ", "");
    }
}
