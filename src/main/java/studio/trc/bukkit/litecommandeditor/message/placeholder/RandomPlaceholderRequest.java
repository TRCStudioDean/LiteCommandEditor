package studio.trc.bukkit.litecommandeditor.message.placeholder;

import java.text.DecimalFormat;
import java.util.Map;

import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class RandomPlaceholderRequest
{
    /**
     * Formats:
     * <pre>
     * {random:[Range]}
     * {random:[Range]:[EnableDecimals]}
     * {random:[Range]:[EnableDecimals]:[DecimalPlaces]}
     * </pre>
     * @param placeholders
     * @param placeholder
     */
    public static void randomPlaceholderRequest(Map<String, String> placeholders, String placeholder) {
        String[] content = placeholder.substring(1, placeholder.length() - 1).split(":");
        if (content.length < 2) return;
        String[] range = content[1].split("-");
        if (range.length != 2) return;
        if (content.length >= 3 && content[2].equalsIgnoreCase("true")) {
            if (LiteCommandEditorUtils.isDouble(range[0]) && LiteCommandEditorUtils.isDouble(range[1])) {
                double random = LiteCommandEditorUtils.getRandomDecimal(Double.valueOf(range[0]), Double.valueOf(range[1]));
                if (content.length >= 4 && LiteCommandEditorUtils.isInteger(content[3])) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0;i < Integer.valueOf(content[3]);i++) {
                        builder.append("#");
                    }
                    placeholders.put(placeholder, new DecimalFormat("0." + builder.toString()).format(random));
                } else {
                    placeholders.put(placeholder, String.valueOf(random));
                }
            }
        } else {
            if (LiteCommandEditorUtils.isInteger(range[0]) && LiteCommandEditorUtils.isInteger(range[1])) {
                placeholders.put(placeholder, String.valueOf(LiteCommandEditorUtils.getRandomNumber(Integer.valueOf(range[0]), Integer.valueOf(range[1]))));
            }
        }
    }
    
    /**
     * Formats:
     * <pre>
     * %lce_random:[Range]%
     * %lce_random:[Range]:[EnableDecimals]%
     * %lce_random:[Range]:[EnableDecimals]:[DecimalPlaces]%
     * </pre>
     * @param placeholder
     * @return 
     */
    public static String randomPlaceholderAPIRequest(String placeholder) {
        String[] content = placeholder.split(":");
        if (content.length < 2) return null;
        String[] range = content[1].split("-");
        if (range.length != 2) return null;
        if (content.length >= 3 && content[2].equalsIgnoreCase("true")) {
            if (LiteCommandEditorUtils.isDouble(range[0]) && LiteCommandEditorUtils.isDouble(range[1])) {
                double random = LiteCommandEditorUtils.getRandomDecimal(Double.valueOf(range[0]), Double.valueOf(range[1]));
                if (content.length >= 4 && LiteCommandEditorUtils.isInteger(content[3])) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0;i < Integer.valueOf(content[3]);i++) {
                        builder.append("#");
                    }
                    return new DecimalFormat("0." + builder.toString()).format(random);
                } else {
                    return String.valueOf(random);
                }
            }
        } else {
            if (LiteCommandEditorUtils.isInteger(range[0]) && LiteCommandEditorUtils.isInteger(range[1])) {
                return String.valueOf(LiteCommandEditorUtils.getRandomNumber(Integer.valueOf(range[0]), Integer.valueOf(range[1])));
            }
        }
        return null;
    }
}
