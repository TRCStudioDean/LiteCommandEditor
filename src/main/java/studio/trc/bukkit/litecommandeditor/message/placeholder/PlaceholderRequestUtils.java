package studio.trc.bukkit.litecommandeditor.message.placeholder;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PlaceholderRequestUtils 
{
    public static void findPlaceholders(char startChar, char endChar, String text, Consumer<String> consumer) {
        StringBuilder builder = new StringBuilder(text);
        int i = 0;
        while (i < builder.length()) {
            if (builder.charAt(i) == startChar) {
                int start = i;
                int depth = 1;
                i++;
                while (i < builder.length() && depth > 0) {
                    if (builder.charAt(i) == startChar) {
                        if (startChar != endChar) {
                            depth++;
                        } else {
                            depth = (depth == 1) ? 0 : 1;
                        }
                    } else if (builder.charAt(i) == endChar && startChar != endChar) {
                        depth--;
                    }
                    i++;
                }
                if (depth == 0) {
                    String placeholder = builder.substring(start, i);
                    consumer.accept(placeholder);
                    builder.deleteCharAt(i - 1);
                    builder.deleteCharAt(start);
                    i = start;
                }
            } else {
                i++;
            }
        }
    }
    
    public static void analysisPlaceholderRequest(Map<String, String> placeholders, String placeholder) {
        if (placeholder.toLowerCase().startsWith("{player:") && placeholder.endsWith("}")) {
            PlayerPlaceholderRequest.playerPlaceholderRequest(placeholders, placeholder);
        } else if (placeholder.toLowerCase().startsWith("{world:") && placeholder.endsWith("}")) {
            WorldPlaceholderRequest.worldPlaceholderRequest(placeholders, placeholder);
        } else if (placeholder.toLowerCase().startsWith("{server:") && placeholder.endsWith("}")) {
            ServerPlaceholderRequest.serverPlaceholderRequest(placeholders, placeholder);
        } else if (placeholder.toLowerCase().startsWith("{calculate:") && placeholder.endsWith("}")) {
            CalculatePlaceholderRequest.calculatePlaceholderRequest(placeholders, placeholder);
        } else {
            ConfiguratorPlaceholderRequest.configPlaceholderRequest(placeholders, placeholder);
        }
    }
    
    public static void analysisColourlessRequest(Map<String, String> placeholders, String placeholder, Map<String, String> colorRetentions) {
        if ((placeholder.toLowerCase().startsWith("{colourless:") || placeholder.toLowerCase().startsWith("{colorless:")) && placeholder.endsWith("}"))  {
            ColourlessPlaceholderRequest.colourlessPlaceholderRequest(placeholders, placeholder, colorRetentions);
        }
    }
    
    public static void analysisRawRequest(Map<String, String> placeholders, String placeholder, Map<String, String> rawRetentions) {
        if (placeholder.toLowerCase().startsWith("{raw:") && placeholder.endsWith("}")) {
            RawPlaceholderRequest.rawPlaceholderRequest(placeholders, placeholder, rawRetentions);
        }
    }
    
    public static Map<String, String> filterPlaceholders(String start, String end, Map<String, String> placeholders) {
        return placeholders.entrySet().stream().filter(placeholder -> placeholder.getKey().startsWith(start) && placeholder.getKey().endsWith(end)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}