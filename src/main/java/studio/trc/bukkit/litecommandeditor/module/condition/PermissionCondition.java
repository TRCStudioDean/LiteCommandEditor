package studio.trc.bukkit.litecommandeditor.module.condition;

import java.util.Map;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandCondition;
import studio.trc.bukkit.litecommandeditor.module.CommandConditionType;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.tool.Function;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class PermissionCondition
    extends CommandCondition
{
    @Getter
    private final CommandConditionType conditionType = CommandConditionType.PERMISSION;
    @Getter
    private final Function function;
    
    public PermissionCondition(Function function, String expression) {
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
        if (parameters.length == 2 && Bukkit.getPlayer(parameters[1]) != null) {
            Player player = Bukkit.getPlayer(parameters[1]);
            return player.hasPermission(MessageUtil.replacePlaceholders(player, parameters[0], placeholders)) ? !getExpression().startsWith("!") : getExpression().startsWith("!");
        } else if (parameters.length < 2) {
            return sender.hasPermission(MessageUtil.replacePlaceholders(sender, parameters[0], placeholders)) ? !getExpression().startsWith("!") : getExpression().startsWith("!");
        } else {
            placeholders.put("{conditionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Condition-Messages.Conditions-Type.Permission"));
            placeholders.put("{fileName}", commandConfig.getFileName());
            placeholders.put("{configPath}", configPath);
            placeholders.put("{expression}", getExpression().startsWith("!") ? "!Permission:" + getExpression().substring(1) : "Permission:" + getExpression());
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Condition-Messages.Incorrect-Parameters", placeholders);
            return getExpression().startsWith("!");
        }
    }
    
    private static String keyWordsReplace(String expression) {
        if (expression.toLowerCase().startsWith("permission:")) {
            expression = expression.substring("permission:".length());
        } else if (expression.toLowerCase().startsWith("!permission:")) {
            expression = "!" + expression.substring("!permission:".length());
        }
        return expression.replace(" ", "");
    }
}
