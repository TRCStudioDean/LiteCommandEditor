package studio.trc.bukkit.litecommandeditor.message.placeholder;

import java.util.Map;

import org.bukkit.Bukkit;

public class ServerPlaceholderRequest 
{
    // {server:[Contents]}
    public static void serverPlaceholderRequest(Map<String, String> placeholders, String placeholder) {
        String[] content = placeholder.substring(1, placeholder.length() - 1).split(":", 2);
        if (content.length < 2) return;
        try {
            String result = noParameterRequest(content[1]);
            if (result != null) {
                placeholders.put(placeholder, result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    // %lce_server:[Contents]%
    public static String serverPlaceholderAPIRequest(String placeholder) {
        String[] content = placeholder.split(":", 2);
        if (content.length < 2) return null;
        try {
            return noParameterRequest(content[1]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private static String noParameterRequest(String content) {
        String result = null;
        switch (content.toLowerCase()) {
            case "allow_end": {
                result = String.valueOf(Bukkit.getAllowEnd());
                break;
            }
            case "allow_flight": {
                result = String.valueOf(Bukkit.getAllowFlight());
                break;
            }
            case "allow_nether": {
                result = String.valueOf(Bukkit.getAllowNether());
                break;
            }
            case "ambient_spawn_limit": {
                result = String.valueOf(Bukkit.getAmbientSpawnLimit());
                break;
            }
            case "animal_spawn_limit": {
                result = String.valueOf(Bukkit.getAnimalSpawnLimit());
                break;
            }
            case "bukkit_version": {
                result = Bukkit.getBukkitVersion();
                break;
            }
            case "connection_throttle": {
                result = String.valueOf(Bukkit.getConnectionThrottle());
                break;
            }
            case "default_game_mode": {
                result = Bukkit.getDefaultGameMode().name();
                break;
            }
            case "generate_structures": {
                result = String.valueOf(Bukkit.getGenerateStructures());
                break;
            }
            case "idle_timeout": {
                result = String.valueOf(Bukkit.getIdleTimeout());
                break;
            }
            case "ip": {
                result = Bukkit.getIp();
                break;
            }
            case "max_players": {
                result = String.valueOf(Bukkit.getMaxPlayers());
                break;
            }
            case "max_world_size": {
                result = String.valueOf(Bukkit.getMaxWorldSize());
                break;
            }
            case "monster_spawn_limit": {
                result = String.valueOf(Bukkit.getMonsterSpawnLimit());
                break;
            }
            case "motd": {
                result = Bukkit.getMotd();
                break;
            }
            case "name": {
                result = Bukkit.getName();
                break;
            }
            case "online_mode": {
                result = String.valueOf(Bukkit.getOnlineMode());
                break;
            }
            case "online_players": {
                result = String.valueOf(Bukkit.getOnlinePlayers().size());
                break;
            }
            case "port": {
                result = String.valueOf(Bukkit.getPort());
                break;
            }
            case "shutdown_message": {
                result = Bukkit.getShutdownMessage();
                break;
            }
            case "spawn_radius": {
                result = String.valueOf(Bukkit.getSpawnRadius());
                break;
            }
            case "ticks_per_ambient_spawns": {
                result = String.valueOf(Bukkit.getTicksPerAmbientSpawns());
                break;
            }
            case "ticks_per_animal_spawns": {
                result = String.valueOf(Bukkit.getTicksPerAnimalSpawns());
                break;
            }
            case "ticks_per_monster_spawns": {
                result = String.valueOf(Bukkit.getTicksPerMonsterSpawns());
                break;
            }
            case "ticks_per_water_ambient_spawns": {
                result = String.valueOf(Bukkit.getTicksPerWaterAmbientSpawns());
                break;
            }
            case "version": {
                result = Bukkit.getVersion();
                break;
            }
            case "view_distance": {
                result = String.valueOf(Bukkit.getViewDistance());
                break;
            }
            case "water_ambient_spawn_limit": {
                result = String.valueOf(Bukkit.getWaterAmbientSpawnLimit());
                break;
            }
            case "world_type": {
                result = Bukkit.getWorldType();
                break;
            }
            case "has_whitelist": {
                result = String.valueOf(Bukkit.hasWhitelist());
                break;
            }
            case "is_hardcore": {
                result = String.valueOf(Bukkit.isHardcore());
                break;
            }
        }
        return result;
    }
}
