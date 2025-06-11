package studio.trc.bukkit.litecommandeditor.message.placeholder;

import java.util.Map;
import java.util.UUID;

public class ColourlessPlaceholderRequest 
{
    public static void colourlessPlaceholderRequest(Map<String, String> placeholders, String placeholder, Map<String, String> colorRetentions) {
        String[] content = placeholder.substring(1, placeholder.length() - 1).split(":", 2);
        if (content.length < 2) return;
        UUID uid = UUID.randomUUID();
        placeholders.put(placeholder, "{" + uid.toString() + "}");
        colorRetentions.put("{" + uid.toString() + "}", content[1]);
    }
}
