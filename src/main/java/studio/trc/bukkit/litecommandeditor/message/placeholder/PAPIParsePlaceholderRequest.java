package studio.trc.bukkit.litecommandeditor.message.placeholder;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import studio.trc.bukkit.litecommandeditor.message.MessageUtil;

public class PAPIParsePlaceholderRequest
{
    // {papi:[PlayerName]:[PAPIs_Placeholder]}
    public static void playerPlaceholderRequest(Map<String, String> placeholders, String placeholder) {
        String[] content = placeholder.substring(1, placeholder.length() - 1).split(":", 3);
        if (content.length < 3) return;
        Player player = Bukkit.getPlayer(content[1]);
        try {
            if (player != null) {
                placeholders.put(placeholder, MessageUtil.toPlaceholderAPIResult((CommandSender) player, content[2]));
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(content[1]);
                if (offlinePlayer != null) {
                    placeholders.put(placeholder, MessageUtil.toPlaceholderAPIResult(offlinePlayer, content[2]));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
