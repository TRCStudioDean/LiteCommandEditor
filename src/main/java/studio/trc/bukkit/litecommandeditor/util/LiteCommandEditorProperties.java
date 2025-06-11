package studio.trc.bukkit.litecommandeditor.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.Main;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.message.color.ColorUtils;

public class LiteCommandEditorProperties
{
    /**
     * System Language
     */
    public static Properties propertiesFile = new Properties();
    
    public static void reloadProperties() {
        try {
            propertiesFile.load(Main.class.getResourceAsStream("/Languages/" + MessageUtil.Language.getLocaleLanguage().getFolderName() + ".properties"));
            sendOperationMessage("PluginPropertiesLoaded");
            List<String> authors = new ArrayList<>();
            switch (MessageUtil.Language.getLocaleLanguage()) {
                case SIMPLIFIED_CHINESE: {
                    authors.add("红色创意工作室 (TRC Studio)");
                    break;
                }
                case TRADITIONAL_CHINESE: {
                    authors.add("紅色創意工作室 (TRC Studio)");
                    break;
                }
                default: {
                    authors.add("The Red Creative Studio (TRC Studio)");
                    break;
                }
            }
            Field field = Main.getInstance().getDescription().getClass().getDeclaredField("authors");
            field.setAccessible(true);
            field.set(Main.getInstance().getDescription(), authors);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void sendOperationMessage(String configPath) {
        CommandSender sender = Bukkit.getConsoleSender();
        if (propertiesFile.containsKey(configPath)) {
            sender.sendMessage(ColorUtils.toColor(propertiesFile.getProperty(configPath)));
        }
    }
    
    public static void sendOperationMessage(String configPath, Map<String, String> placeholders) {
        CommandSender sender = Bukkit.getConsoleSender();
        if (propertiesFile.containsKey(configPath)) {
            String message = propertiesFile.getProperty(configPath);
            sender.sendMessage(MessageUtil.replacePlaceholders(message, placeholders));
        }
    }
    
    public static String getMessage(String configPath) {
        return propertiesFile.getProperty(configPath);
    }
    
    public static String getMessage(String configPath, Map<String, String> placeholders) {
        return MessageUtil.replacePlaceholders(propertiesFile.getProperty(configPath), placeholders);
    }
}
