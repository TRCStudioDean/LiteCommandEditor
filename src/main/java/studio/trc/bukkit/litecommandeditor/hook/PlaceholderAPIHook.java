package studio.trc.bukkit.litecommandeditor.hook;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.OfflinePlayer;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.configuration.RobustConfiguration;
import studio.trc.bukkit.litecommandeditor.message.placeholder.CalculatePlaceholderRequest;
import studio.trc.bukkit.litecommandeditor.message.placeholder.ConfiguratorPlaceholderRequest;
import studio.trc.bukkit.litecommandeditor.message.placeholder.PlayerPlaceholderRequest;
import studio.trc.bukkit.litecommandeditor.message.placeholder.ServerPlaceholderRequest;
import studio.trc.bukkit.litecommandeditor.message.placeholder.WorldPlaceholderRequest;

public class PlaceholderAPIHook
    extends PlaceholderExpansion
{
    @Getter
    private static final PlaceholderAPIHook instance = new PlaceholderAPIHook();
    private static final Map<String, String> cacheOfServer = new HashMap<>();
    
    private static long cacheOfUpdateTime = System.currentTimeMillis();
    
    private PlaceholderAPIHook() {
        super();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        checkUpdate();
        String lowerIdentifier = identifier.toLowerCase();
        RobustConfiguration config = ConfigurationType.CONFIG.getRobustConfig();
        boolean cache = !config.getStringList("PlaceholderAPI.Exceptions").stream().anyMatch(placeholder -> placeholder.equals(lowerIdentifier)) && config.getDouble("PlaceholderAPI.Cache-Update-Delay") > 0;
        if (cacheOfServer.get(lowerIdentifier) != null) {
            return cacheOfServer.get(lowerIdentifier);
        } else {
            if (lowerIdentifier.startsWith("calculate:")) {
                String[] splitedIdentifier = identifier.split(":", 2);
                if (splitedIdentifier.length == 2) {
                    String result = CalculatePlaceholderRequest.calculateReplace(splitedIdentifier[1]);
                    if (cache) {
                        cacheOfServer.put(lowerIdentifier, result);
                    }
                    return result;
                }
            } else if (lowerIdentifier.startsWith("configurator:")) {
                String result = ConfiguratorPlaceholderRequest.configPlaceholderRequestPAPI(identifier);
                if (result != null) {
                    if (cache) {
                        cacheOfServer.put(lowerIdentifier, result);
                    }
                    return result;
                }
            } else if (lowerIdentifier.startsWith("world:")) {
                String result = WorldPlaceholderRequest.worldPlaceholderAPIRequest(identifier);
                if (result != null) {
                    if (cache) {
                        cacheOfServer.put(lowerIdentifier, result);
                    }
                    return result;
                }
            } else if (lowerIdentifier.startsWith("server:")) {
                String result = ServerPlaceholderRequest.serverPlaceholderAPIRequest(identifier);
                if (result != null) {
                    if (cache) {
                        cacheOfServer.put(lowerIdentifier, result);
                    }
                    return result;
                }
            } else if (lowerIdentifier.startsWith("player:")) {
                String result = PlayerPlaceholderRequest.playerPlaceholderAPIRequest(identifier);
                if (result != null) {
                    if (cache) {
                        cacheOfServer.put(lowerIdentifier, result);
                    }
                    return result;
                }
            } else if (lowerIdentifier.startsWith("me:")) {
                String result = PlayerPlaceholderRequest.playerPlaceholderAPIRequest(player.getPlayer(), identifier);
                if (result != null) {
                    if (cache) {
                        cacheOfServer.put(lowerIdentifier, result);
                    }
                    return result;
                }
            }
        }
        return null;
    }
    
    @Override
    public String getPlugin() {
        return "LiteCommandEditor";
    }

    @Override
    public String getIdentifier() {
        return "lce";
    }

    @Override
    public String getAuthor() {
        return "TRCStudioDean";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    public static void checkUpdate() {
        if (ConfigurationType.CONFIG.getRobustConfig().getDouble("PlaceholderAPI.Cache-Update-Delay") <= 0) return;
        if (cacheOfUpdateTime < System.currentTimeMillis()) {
            cacheOfServer.clear();
            cacheOfUpdateTime = System.currentTimeMillis() + (long) (ConfigurationType.CONFIG.getRobustConfig().getDouble("PlaceholderAPI.Cache-Update-Delay") * 1000);
        }
    }
}
