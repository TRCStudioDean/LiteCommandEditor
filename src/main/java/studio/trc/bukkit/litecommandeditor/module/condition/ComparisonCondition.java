package studio.trc.bukkit.litecommandeditor.module.condition;

import java.util.Map;

import lombok.Getter;

import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.module.CommandCondition;
import studio.trc.bukkit.litecommandeditor.module.CommandConditionType;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.tool.Function;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class ComparisonCondition
    extends CommandCondition
{
    @Getter
    private final CommandConditionType conditionType = CommandConditionType.COMPARISON;
    @Getter
    private final Function function;
    
    public ComparisonCondition(Function function, String expression) {
        super(expression);
        this.function = function;
    }
    
    @Override
    public boolean matchCondition(CommandConfiguration commandConfig, String configPath, CommandSender sender, Map<String, String> placeholders) {
        String value = LiteCommandEditorUtils.getConvertedCondition(getExpression(), sender, placeholders);
        if (value.contains("===")) {
            String[] arguments = value.split("===");
            if (arguments.length == 2) {
                if (isDouble(arguments[0]) && isDouble(arguments[1])) {
                    if (Double.valueOf(arguments[0]).equals(Double.valueOf(arguments[1]))) {
                        return true;
                    }
                } else {
                    if (arguments[0].equals(arguments[1])) {
                        return true;
                    }
                }
            }
        } else if (value.contains("!==")) {
            String[] arguments = value.split("!==");
            if (arguments.length == 2) {
                if (isDouble(arguments[0]) && isDouble(arguments[1])) {
                    if (!Double.valueOf(arguments[0]).equals(Double.valueOf(arguments[1]))) {
                        return true;
                    }
                } else {
                    if (!arguments[0].equals(arguments[1])) {
                        return true;
                    }
                }
            }
        } else if (value.contains("==")) {
            String[] arguments = value.split("==");
            if (arguments.length == 2) {
                if (isDouble(arguments[0]) && isDouble(arguments[1])) {
                    if (Double.valueOf(arguments[0]).equals(Double.valueOf(arguments[1]))) {
                        return true;
                    }
                } else {
                    if (arguments[0].equalsIgnoreCase(arguments[1])) {
                        return true;
                    }
                }
            }
        } else if (value.contains("!=")) {
            String[] arguments = value.split("!=");
            if (arguments.length == 2) {
                if (isDouble(arguments[0]) && isDouble(arguments[1])) {
                    if (!Double.valueOf(arguments[0]).equals(Double.valueOf(arguments[1]))) {
                        return true;
                    }
                } else {
                    if (!arguments[0].equalsIgnoreCase(arguments[1])) {
                        return true;
                    }
                }
            }
        } else if (value.contains(">=")) {
            String[] arguments = value.split(">=");
            if (arguments.length == 2 && isDouble(arguments[0]) && isDouble(arguments[1])) {
                if (Double.valueOf(arguments[0]) >= Double.valueOf(arguments[1])) {
                    return true;
                }
            }
        } else if (value.contains(">")) {
            String[] arguments = value.split(">");
            if (arguments.length == 2 && isDouble(arguments[0]) && isDouble(arguments[1])) {
                if (Double.valueOf(arguments[0]) > Double.valueOf(arguments[1])) {
                    return true;
                }
            }
        } else if (value.contains("<=")) {
            String[] arguments = value.split("<=");
            if (arguments.length == 2 && isDouble(arguments[0]) && isDouble(arguments[1])) {
                if (Double.valueOf(arguments[0]) <= Double.valueOf(arguments[1])) {
                    return true;
                }
            }
        } else if (value.contains("<")) {
            String[] arguments = value.split("<");
            if (arguments.length == 2 && isDouble(arguments[0]) && isDouble(arguments[1])) {
                if (Double.valueOf(arguments[0]) < Double.valueOf(arguments[1])) {
                    return true;
                }
            }
        } else {
            return Boolean.valueOf(value);
        }
        return false;
    }
    
    private boolean isDouble(String value) {
        try {
            Double.valueOf(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
