package studio.trc.bukkit.litecommandeditor.module.tool;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import studio.trc.bukkit.litecommandeditor.Main;
import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;
import studio.trc.bukkit.litecommandeditor.util.PluginControl;

public class Updater
{
    private static Thread checkUpdateThread;
    @Getter
    private static Date date = null;
    @Getter
    @Setter
    private static boolean hasNewVersion = false;
    @Getter
    @Setter
    private static String newVersion = null;
    @Getter
    @Setter
    private static String link = null;
    @Getter
    @Setter
    private static String description = null;
    @Getter
    @Setter
    private static List<String> extra = new ArrayList<>();
    
    public static void initialize() {
        checkUpdateThread = new Thread(() -> {
            date = new Date();
            if (PluginControl.enableUpdater()) {
                try {
                    URL url = new URL("https://api.trc.studio/resources/spigot/litecommandeditor/update.yml");
                    try (Reader reader = new InputStreamReader(url.openStream(), LiteCommandEditorProperties.getMessage("Charset"))) {
                        YamlConfiguration yaml = new YamlConfiguration();
                        yaml.load(reader);
                        String version = yaml.getString("Latest-Version");
                        String versionBelongTo = yaml.getString("Version-Belonging-to");
                        String nowVersion = Main.getInstance().getDescription().getVersion();
                        if (!nowVersion.startsWith(versionBelongTo)) {
                            String downloadLink = yaml.getString("Link");
                            String descriptionMessages = yaml.getString("Description." + MessageUtil.getLanguage());
                            if (descriptionMessages == null) {
                                descriptionMessages = yaml.getString("Description.Default");
                            }
                            List<String> extraMessages = yaml.getStringList("Extra." + MessageUtil.getLanguage());
                            if (extraMessages == null) {
                                extraMessages = yaml.getStringList("Extra.Default");
                            }
                            hasNewVersion = true;
                            newVersion = version;
                            link = downloadLink;
                            description = descriptionMessages;
                            extra = extraMessages;
                            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                            placeholders.put("{version}", version);
                            placeholders.put("{link}", downloadLink);
                            placeholders.put("{nowVersion}", Main.getInstance().getDescription().getVersion());
                            placeholders.put("{description}", descriptionMessages);
                            MessageUtil.sendConsoleMessage("Updater", ConfigurationType.MESSAGES, placeholders);
                            MessageUtil.sendMessage(Bukkit.getConsoleSender(), extraMessages);
                        }
                    }
                } catch (Throwable t) {}
            }
        }, "LiteCommandEditor-Updater");
    }
    
    public static void checkUpdate() {
        initialize();
        checkUpdateThread.start();
    }
}
