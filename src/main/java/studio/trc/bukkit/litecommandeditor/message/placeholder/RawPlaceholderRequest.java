package studio.trc.bukkit.litecommandeditor.message.placeholder;

import java.util.Map;
import java.util.UUID;

public class RawPlaceholderRequest 
{
    public static void rawPlaceholderRequest(Map<String, String> placeholders, String placeholder, Map<String, String> rawRetentions) {
        String[] content = placeholder.substring(1, placeholder.length() - 1).split(":", 2);
        if (content.length < 2) return;
        UUID uid = UUID.randomUUID();
        placeholders.put(placeholder, "{" + uid.toString() + "}");
        rawRetentions.put("{" + uid.toString() + "}", content[1]);
    }
}
