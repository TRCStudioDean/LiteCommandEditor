package studio.trc.bukkit.litecommandeditor.configuration;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import studio.trc.bukkit.litecommandeditor.Main;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;

public class DefaultConfigurationFile
{
    private final static Map<ConfigurationType, YamlConfiguration> cacheDefaultConfig = new HashMap<>();
    private final static Map<ConfigurationType, Boolean> isDefaultConfigLoaded = new HashMap<>();
    
    public static YamlConfiguration getDefaultConfig(ConfigurationType type) {
        if (type.isDefaultConfigExist() && (!isDefaultConfigLoaded.containsKey(type) || !isDefaultConfigLoaded.get(type))) {
            loadDefaultConfigurationFile(type);
            isDefaultConfigLoaded.put(type, true);
        }
        return cacheDefaultConfig.get(type);
    }
    
    public static void loadDefaultConfigurationFile(ConfigurationType fileType) {
        String filePath = getDefaultConfigurationFilePath(fileType);
        if (fileType.isDefaultConfigExist()) {
            try (Reader config = new InputStreamReader(Main.getInstance().getClass().getResource(filePath).openStream(), LiteCommandEditorProperties.getMessage("Charset"))) {
                YamlConfiguration yaml = new YamlConfiguration();
                yaml.load(config);
                cacheDefaultConfig.put(fileType, yaml);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static String getDefaultConfigurationFilePath(ConfigurationType fileType) {
        return "/Languages/" + (fileType.isUniversal() ? "Universal" : MessageUtil.Language.getLocaleLanguage().getFolderName()) + "/" + fileType.getLocalFilePath();
    }
}
