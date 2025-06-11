package studio.trc.bukkit.litecommandeditor.module.condition;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandCondition;
import studio.trc.bukkit.litecommandeditor.module.CommandConditionType;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.tool.Function;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class RegularExpressionCondition 
    extends CommandCondition
{
    @Getter
    private final CommandConditionType conditionType = CommandConditionType.REGULAR_EXPRESSION;
    @Getter
    private final Function function;

    public RegularExpressionCondition(Function function, String expression) {
        super(expression);
        this.function = function;
    }

    //Format: "Matcher:[RegularExpression]:[Text]"
    @Override
    public boolean matchCondition(CommandConfiguration commandConfig, String configPath, CommandSender sender, Map<String, String> placeholders) {
        String[] parameters;
        if (getExpression().startsWith("!")) {
            parameters = LiteCommandEditorUtils.getConditionParameters(getExpression().substring(1), sender, placeholders);
        } else {
            parameters = LiteCommandEditorUtils.getConditionParameters(getExpression(), sender, placeholders);
        }
        if (parameters.length == 2) {
            try {
                Pattern pattern = Pattern.compile(parameters[0]);
                Matcher matcher = pattern.matcher(parameters[1]);
                if (matcher.find()) {
                    return !getExpression().startsWith("!");
                }
            } catch (PatternSyntaxException ex) {
                placeholders.put("{fileName}", commandConfig.getFileName());
                placeholders.put("{configPath}", configPath);
                placeholders.put("{expression}", parameters[0]);
                LiteCommandEditorProperties.sendOperationMessage("InvalidRegularExpression", placeholders);
            }
        } else {
            placeholders.put("{conditionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Condition-Messages.Conditions-Type.Matcher"));
            placeholders.put("{fileName}", commandConfig.getFileName());
            placeholders.put("{configPath}", configPath);
            placeholders.put("{expression}", getExpression().startsWith("!") ? "!Matcher:" + getExpression().substring(1) : "Matcher:" + getExpression());
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Condition-Messages.Incorrect-Parameters", placeholders);
        }
        return getExpression().startsWith("!");
    }
    
    private static String keyWordsReplace(String expression) {
        if (expression.toLowerCase().startsWith("matcher:")) {
            expression = expression.substring("matcher:".length());
        } else if (expression.toLowerCase().startsWith("!matcher:")) {
            expression = "!" + expression.substring("!matcher:".length());
        }
        return expression;
    }
}
