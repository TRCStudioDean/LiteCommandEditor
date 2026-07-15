package studio.trc.bukkit.litecommandeditor.hook;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.Getter;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.OfflinePlayer;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.configuration.RobustConfiguration;
import studio.trc.bukkit.litecommandeditor.message.placeholder.CalculatePlaceholderRequest;
import studio.trc.bukkit.litecommandeditor.message.placeholder.ConfiguratorPlaceholderRequest;
import studio.trc.bukkit.litecommandeditor.message.placeholder.PlayerPlaceholderRequest;
import studio.trc.bukkit.litecommandeditor.message.placeholder.RandomPlaceholderRequest;
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
            Function<String, String> func = result -> {
                if (result != null) {
                    if (cache) {
                        cacheOfServer.put(lowerIdentifier, result);
                    }
                    return result;
                } else {
                    return null;
                }
            };
            if (lowerIdentifier.startsWith("calculate:")) {
                String[] splitedIdentifier = identifier.split(":", 2);
                if (splitedIdentifier.length == 2) {
                    return func.apply(CalculatePlaceholderRequest.calculateReplace(splitedIdentifier[1]));
                }
            } else if (lowerIdentifier.startsWith("configurator:")) {
                return func.apply(ConfiguratorPlaceholderRequest.configPlaceholderRequestPAPI(identifier));
            } else if (lowerIdentifier.startsWith("world:")) {
                return func.apply(WorldPlaceholderRequest.worldPlaceholderAPIRequest(identifier));
            } else if (lowerIdentifier.startsWith("server:")) {
                return func.apply(ServerPlaceholderRequest.serverPlaceholderAPIRequest(identifier));
            } else if (lowerIdentifier.startsWith("player:")) {
                return func.apply(PlayerPlaceholderRequest.playerPlaceholderAPIRequest(identifier));
            } else if (lowerIdentifier.startsWith("me:")) {
                return func.apply(PlayerPlaceholderRequest.playerPlaceholderAPIRequest(player.getPlayer(), identifier));
            } else if (lowerIdentifier.startsWith("random:")) {
                return func.apply(RandomPlaceholderRequest.randomPlaceholderAPIRequest(identifier));
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
