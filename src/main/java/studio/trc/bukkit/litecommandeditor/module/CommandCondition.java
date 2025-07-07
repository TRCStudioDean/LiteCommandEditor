package studio.trc.bukkit.litecommandeditor.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.event.CommandConditionsResetEvent;
import studio.trc.bukkit.litecommandeditor.message.MessageEditor;
import studio.trc.bukkit.litecommandeditor.module.condition.*;
import studio.trc.bukkit.litecommandeditor.module.tool.Function;

public abstract class CommandCondition
{
    public static final Map<String, Class<? extends CommandCondition>> conditions = new HashMap<>();
    @Getter
    private final String expression;
    
    public CommandCondition(String expression) {
        this.expression = expression;
    }
    
    /**
     * @param commandConfig command configuration.
     * @param configPath config path.
     * @param sender Command executor.
     * @param placeholders Placeholders.
     * @return The result.
     */
    public abstract boolean matchCondition(CommandConfiguration commandConfig, String configPath, CommandSender sender, Map<String, String> placeholders);
    
    /**
     * Get condition type of extended class
     * @return 
     */
    public abstract CommandConditionType getConditionType();
    
    public static void resetCommandConditions() {
        conditions.clear();
        conditions.put("permission:", PermissionCondition.class);
        conditions.put("hasitem:", ItemCondition.class);
        conditions.put("player:", PlayerCondition.class);
        conditions.put("world:", WorldCondition.class);
        conditions.put("server:", ServerCondition.class);
        conditions.put("hasplaceholder:", PlaceholderCondition.class);
        conditions.put("number:", NumberCondition.class);
        conditions.put("matcher:", RegularExpressionCondition.class);
        Bukkit.getPluginManager().callEvent(new CommandConditionsResetEvent(conditions));
    }
    
    private static final Map<String, String> operators = new HashMap<>();
    
    static {
        operators.put("||", "1");
        operators.put("&&", "2");
    }
    
    public static Schedule getCommandConditions(Function function, String expression) {
        return new Schedule(expression, MessageEditor.parse(expression, operators).stream().map(text -> {
            if (text.isPlaceholder()) {
                return text.getPlaceholder();
            } else {
                String subExpression = text.getText();
                String conditionName = conditions.keySet().stream().filter(prefix -> subExpression.toLowerCase().startsWith(prefix.toLowerCase()) || subExpression.toLowerCase().startsWith("!" + prefix.toLowerCase())).findFirst().orElse(null);
                if (conditionName != null) {
                    Class<? extends CommandCondition> condition = conditions.get(conditionName);
                    try {
                        return (CommandCondition) condition.getConstructor(Function.class, String.class).newInstance(function, subExpression);
                    } catch (Exception ex) {
                        return new ComparisonCondition(function, subExpression);
                    }
                } else {
                    return new ComparisonCondition(function, subExpression);
                }
            }
        }).collect(Collectors.toList()));
    }
    
    public static class Schedule {
        @Getter
        private final String expression;
        @Getter
        private final List expressions;
        
        public Schedule(String expression, List expressions) {
            this.expression = expression;
            this.expressions = expressions;
        }
    
        public boolean analysis(List processing) {
            for (int i = 0;i < processing.size();i++) {
                if (processing.get(i) instanceof String && i != 0 && i != processing.size() - 1 && processing.get(i).equals("&&") && processing.get(i - 1) instanceof Boolean && processing.get(i + 1) instanceof Boolean) {
                    processing.set(i - 1, (Boolean) processing.get(i - 1) && (Boolean) processing.get(i + 1));
                    processing.remove(i + 1);
                    processing.remove(i);
                    i--;
                }
            }
            for (int i = 0;i < processing.size();i++) {
                if (processing.get(i) instanceof String && i != 0 && i != processing.size() - 1 && processing.get(i).equals("||") && processing.get(i - 1) instanceof Boolean && processing.get(i + 1) instanceof Boolean) {
                    processing.set(i - 1, (Boolean) processing.get(i - 1) || (Boolean) processing.get(i + 1));
                    processing.remove(i + 1);
                    processing.remove(i);
                    i--;
                }
            }
            if (processing.size() != 1 || !(processing.get(0) instanceof Boolean)) {
                return false;
            } else {
                return (Boolean) processing.get(0);
            }
        }
    }
}
