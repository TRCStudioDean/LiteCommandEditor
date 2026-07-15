package studio.trc.bukkit.litecommandeditor.message.placeholder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;
import studio.trc.bukkit.litecommandeditor.util.SecuritySettingsManager;

public class ArgumentsPlaceholderRequest 
{
    public static void argumentsPlaceholderRequest(Map<String, String> placeholders, String placeholder) {
        if (!placeholder.startsWith("[") || !placeholder.endsWith("]")) return;
        String[] arguments = placeholders.entrySet().stream()
            .filter(entry -> LiteCommandEditorUtils.isInteger(entry.getKey().substring(1, entry.getKey().length() - 1)))
            .sorted(Comparator.comparingInt(entry -> Integer.valueOf(entry.getKey().substring(1, entry.getKey().length() - 1))))
            .map(Map.Entry::getValue)
            .toArray(String[]::new);
        String replaced;
        if (!placeholder.equals("[-]")) {
            if (!formatCheck(placeholder)) return;
            List<String> text = new ArrayList<>();
            String[] segments = placeholder.substring(1, placeholder.length() - 1).split(",");
            for (String segment : segments) {
                String[] rangeParts = segment.split("-", 2);
                if (rangeParts.length == 1 && LiteCommandEditorUtils.isInteger(rangeParts[0]) && Integer.valueOf(rangeParts[0]) <= arguments.length) {
                    text.add(arguments[Integer.valueOf(rangeParts[0]) - 1]);
                } else if (rangeParts.length == 2) {
                    int start = LiteCommandEditorUtils.isInteger(rangeParts[0]) && Integer.valueOf(rangeParts[0]) > 0 && Integer.valueOf(rangeParts[0]) <= arguments.length ? Integer.valueOf(rangeParts[0]) : 1;
                    int end = LiteCommandEditorUtils.isInteger(rangeParts[1]) && Integer.valueOf(rangeParts[0]) > 0 && Integer.valueOf(rangeParts[1]) <= arguments.length ? Integer.valueOf(rangeParts[1]) : arguments.length;
                    for (int i = start;i <= end;i++) {
                        text.add(arguments[i - 1]);
                    }
                }
            }
            replaced = String.join(" ", text);
        } else {
            replaced = String.join(" ", arguments);
        }
        if (!SecuritySettingsManager.PARSE_PLACEHOLDER_FROM_SUB_COMMAND_PLACEHOLDER) {
            replaced = "{raw:" + replaced + "}";
        }
        if (!SecuritySettingsManager.PARSE_COLOR_FROM_SUB_COMMAND_PLACEHOLDER) {
            replaced = "{colourless:" + replaced + "}";
        }
        placeholders.put(placeholder, replaced);
    }
    
    private static boolean formatCheck(String placeholder) {
        char[] chars = placeholder.toCharArray();
        boolean number = false, minus = false, comma = false;
        for (int i = 1;i < chars.length - 1;i++) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                number = true;
            } else if (chars[i] == '-') {
                minus = true;
            } else if (chars[i] == ',') {
                comma = true;
            } else if (chars[i] == ' ') {
                // Allow space
            } else {
                return false;
            }
        }
        return number && (minus || comma);
    }
}
