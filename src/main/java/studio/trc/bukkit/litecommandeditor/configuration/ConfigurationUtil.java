package studio.trc.bukkit.litecommandeditor.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationUtil
{
    private final static Map<ConfigurationType, RobustConfiguration> cacheConfig = new HashMap<>();
    
    public static RobustConfiguration getConfig(ConfigurationType fileType) {
        if (cacheConfig.containsKey(fileType)) {
            return cacheConfig.get(fileType);
        }
        RobustConfiguration config = new RobustConfiguration(fileType);
        cacheConfig.put(fileType, config);
        return config;
    }
    
    public static void reloadConfig(ConfigurationType... filesType) {
        for (ConfigurationType type : filesType) {
            reloadConfig(type);
        }
    }
    
    public static boolean reloadConfig(ConfigurationType fileType) {
        fileType.saveResource(); 
        return fileType.reloadConfig();
    }
    
    public static Map<String, String> reloadConfig() {
        Map<String, String> parameters = new ConcurrentHashMap();
        int loadedAmount = 0;
        int errorAmount = 0;
        for (ConfigurationType type : ConfigurationType.values()) {
            if (reloadConfig(type)) {
                loadedAmount++;
            } else {
                errorAmount++;
            }
        }
        parameters.put("{configAmount}", String.valueOf(loadedAmount));
        parameters.put("{configError}", String.valueOf(errorAmount));
        return parameters;
    }
}
