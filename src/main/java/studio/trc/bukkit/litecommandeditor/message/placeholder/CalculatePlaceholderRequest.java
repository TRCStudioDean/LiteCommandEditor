package studio.trc.bukkit.litecommandeditor.message.placeholder;

import java.text.DecimalFormat;
import java.util.Map;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.module.tool.Calculator;

public class CalculatePlaceholderRequest
{
    public static void calculatePlaceholderRequest(Map<String, String> placeholders, String placeholder) {
        String[] content = placeholder.substring(1, placeholder.length() - 1).split(":", 2);
        if (content.length < 2) return;
        placeholders.put(placeholder, calculateReplace(content[1]));
    }
    
    public static String calculateReplace(String expression) {
        try {
            double result = Calculator.calculateAll(expression);
            StringBuilder builder = new StringBuilder();
            for (int i = 0;i < ConfigurationType.CONFIG.getRobustConfig().getInt("Retaining-Decimal-Places");i++) {
                builder.append("#");
            }
            return new DecimalFormat("0." + builder.toString()).format(result);
        } catch (NumberFormatException ex) {
            return expression;
        }
    }
}
