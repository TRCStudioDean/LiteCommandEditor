package studio.trc.bukkit.litecommandeditor.message.placeholder;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class WorldPlaceholderRequest 
{
    // {world:[WorldName]:[Contents]}
    public static void worldPlaceholderRequest(Map<String, String> placeholders, String placeholder) {
        String[] content = placeholder.substring(1, placeholder.length() - 1).split(":", 3);
        if (content.length < 3) return;
        World world = Bukkit.getWorld(content[1]);
        if (world == null) return;
        try {
            String result = noParameterRequest(world, content[2]);
            if (result == null) {
                result = parameterRequest(world, content[2]);
            }
            if (result != null) {
                placeholders.put(placeholder, result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    // %lce_world:[WorldName]:[Contents]%
    public static String worldPlaceholderAPIRequest(String placeholder) {
        String[] content = placeholder.split(":", 3);
        if (content.length < 3) return placeholder;
        World world = Bukkit.getWorld(content[1]);
        if (world == null) return placeholder;
        try {
            String result = noParameterRequest(world, content[2]);
            if (result == null) {
                result = parameterRequest(world, content[2]);
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static String noParameterRequest(World world, String content) {
        String result = null;
        switch (content.toLowerCase()) {
            case "can_generate_structures": {
                result = String.valueOf(world.canGenerateStructures());
                break;
            }
            case "allow_animals": {
                result = String.valueOf(world.getAllowAnimals());
                break;
            }
            case "allow_monsters": {
                result = String.valueOf(world.getAllowMonsters());
                break;
            }
            case "ambient_spawn_limit": {
                result = String.valueOf(world.getAmbientSpawnLimit());
                break;
            }
            case "animal_spawn_limit": {
                result = String.valueOf(world.getAnimalSpawnLimit());
                break;
            }
            case "clear_weather_duration": {
                result = String.valueOf(world.getClearWeatherDuration());
                break;
            }
            case "difficulty": {
                result = world.getDifficulty().name();
                break;
            }
            case "environment": {
                result = world.getEnvironment().name();
                break;
            }
            case "full_time": {
                result = String.valueOf(world.getFullTime());
                break;
            }
            case "keep_spawn_in_memory": {
                result = String.valueOf(world.getKeepSpawnInMemory());
                break;
            }
            case "max_height": {
                result = String.valueOf(world.getMaxHeight());
                break;
            }
            case "min_height": {
                result = String.valueOf(world.getMinHeight());
                break;
            }
            case "monster_spawn_limit": {
                result = String.valueOf(world.getMonsterSpawnLimit());
                break;
            }
            case "pvp": {
                result = String.valueOf(world.getPVP());
                break;
            }
            case "name": {
                result = world.getName();
                break;
            }
            case "sea_level": {
                result = String.valueOf(world.getSeaLevel());
                break;
            }
            case "seed": {
                result = String.valueOf(world.getSeed());
                break;
            }
            case "thunder_duration": {
                result = String.valueOf(world.getThunderDuration());
                break;
            }
            case "ticks_per_ambient_spawns": {
                result = String.valueOf(world.getTicksPerAmbientSpawns());
                break;
            }
            case "ticks_per_animal_spawns": {
                result = String.valueOf(world.getTicksPerAnimalSpawns());
                break;
            }
            case "ticks_per_monster_spawns": {
                result = String.valueOf(world.getTicksPerMonsterSpawns());
                break;
            }
            case "ticks_per_water_ambient_spawns": {
                result = String.valueOf(world.getTicksPerWaterAmbientSpawns());
                break;
            }
            case "ticks_per_water_spawns": {
                result = String.valueOf(world.getTicksPerWaterSpawns());
                break;
            }
            case "time": {
                result = String.valueOf(world.getTime());
                break;
            }
            case "uid": {
                result = world.getUID().toString();
                break;
            }
            case "view_distance": {
                result = String.valueOf(world.getViewDistance());
                break;
            }
            case "water_ambient_spawn_limit": {
                result = String.valueOf(world.getWaterAmbientSpawnLimit());
                break;
            }
            case "water_animal_spawn_limit": {
                result = String.valueOf(world.getWaterAnimalSpawnLimit());
                break;
            }
            case "weather_duration": {
                result = String.valueOf(world.getWeatherDuration());
                break;
            }
            case "world_type": {
                result = world.getWorldType().name();
                break;
            }
            case "has_storm": {
                result = String.valueOf(world.hasStorm());
                break;
            }
            case "auto_save": {
                result = String.valueOf(world.isAutoSave());
                break;
            }
            case "clear_weather": {
                result = String.valueOf(world.isClearWeather());
                break;
            }
            case "hard_core": {
                result = String.valueOf(world.isHardcore());
                break;
            }
            case "thundering": {
                result = String.valueOf(world.isThundering());
                break;
            }
        }
        return result;
    }

    private static String parameterRequest(World world, String content) {
        //{world:[Name]:chunk_at_[X]_[Z]_[Details]}
        if (content.toLowerCase().startsWith("chunk_at")) {
            String[] details = content.split("_", 5);
            if (details.length == 5 && LiteCommandEditorUtils.isInteger(details[2]) && LiteCommandEditorUtils.isInteger(details[3])) {
                return getChunkDetail(world.getChunkAt(Integer.valueOf(details[2]), Integer.valueOf(details[3])), details[4]);
            }
        }
        //{world:[Name]:block_at_[X]_[Y]_[Z]_[Details]}
        if (content.toLowerCase().startsWith("block_at")) {
            String[] details = content.split("_", 6);
            if (details.length == 6 && LiteCommandEditorUtils.isInteger(details[2]) && LiteCommandEditorUtils.isInteger(details[3]) && LiteCommandEditorUtils.isInteger(details[4])) {
                return getBlockDetail(world.getBlockAt(Integer.valueOf(details[2]), Integer.valueOf(details[3]), Integer.valueOf(details[4])), details[5]);
            }
        }
        //{world:[Name]:game_rule_value_[Rule]}
        if (content.toLowerCase().startsWith("game_rule_value")) {
            String[] details = content.split("_", 4);
            if (details.length == 4 && GameRule.getByName(details[3]) != null) {
                return world.getGameRuleValue(GameRule.getByName(details[3])).toString();
            }
        }
        //{world:[Name]:spawn_location_[Details]}
        if (content.toLowerCase().startsWith("spawn_location")) {
            String[] details = content.split("_", 3);
            if (details.length == 3) {
                return getLocationDetail(world.getSpawnLocation(), details[2]);
            }
        }
        //{world:[Name]:is_chunk_force_loaded_[X]_[Z]]}
        if (content.toLowerCase().startsWith("is_chunk_force_loaded")) {
            String[] details = content.split("_", 6);
            if (details.length == 6 && LiteCommandEditorUtils.isInteger(details[4]) && LiteCommandEditorUtils.isInteger(details[5])) {
                return String.valueOf(world.isChunkForceLoaded(Integer.valueOf(details[4]), Integer.valueOf(details[5])));
            }
        }
        //{world:[Name]:is_chunk_generated_[X]_[Z]]}
        if (content.toLowerCase().startsWith("is_chunk_generated")) {
            String[] details = content.split("_", 5);
            if (details.length == 5 && LiteCommandEditorUtils.isInteger(details[3]) && LiteCommandEditorUtils.isInteger(details[4])) {
                return String.valueOf(world.isChunkGenerated(Integer.valueOf(details[3]), Integer.valueOf(details[4])));
            }
        }
        //{world:[Name]:is_chunk_loaded_[X]_[Z]]}
        if (content.toLowerCase().startsWith("is_chunk_loaded")) {
            String[] details = content.split("_", 5);
            if (details.length == 5 && LiteCommandEditorUtils.isInteger(details[3]) && LiteCommandEditorUtils.isInteger(details[4])) {
                return String.valueOf(world.isChunkLoaded(Integer.valueOf(details[3]), Integer.valueOf(details[4])));
            }
        }
        //{world:[Name]:is_game_rule_[Rule]]}
        if (content.toLowerCase().startsWith("is_game_rule")) {
            String[] details = content.split("_", 4);
            if (details.length == 4) {
                return String.valueOf(world.isGameRule(details[3]));
            }
        }
        //{world:[Name]:humidity_[X]_[Y]_[Z]]}
        if (content.toLowerCase().startsWith("humidity")) {
            String[] details = content.split("_", 4);
            if (details.length == 3 && LiteCommandEditorUtils.isInteger(details[1]) && LiteCommandEditorUtils.isInteger(details[2])) {
                return String.valueOf(world.getHumidity(Integer.valueOf(details[1]), Integer.valueOf(details[2])));
            } else if (details.length == 4 && LiteCommandEditorUtils.isInteger(details[1]) && LiteCommandEditorUtils.isInteger(details[2]) && LiteCommandEditorUtils.isInteger(details[3])) {
                return String.valueOf(world.getHumidity(Integer.valueOf(details[1]), Integer.valueOf(details[2]), Integer.valueOf(details[3])));
            }
        }
        //{world:[Name]:temperature_[X]_[Y]_[Z]]}
        if (content.toLowerCase().startsWith("temperature")) {
            String[] details = content.split("_", 4);
            if (details.length == 3 && LiteCommandEditorUtils.isInteger(details[1]) && LiteCommandEditorUtils.isInteger(details[2])) {
                return String.valueOf(world.getTemperature(Integer.valueOf(details[1]), Integer.valueOf(details[2])));
            } else if (details.length == 4 && LiteCommandEditorUtils.isInteger(details[1]) && LiteCommandEditorUtils.isInteger(details[2]) && LiteCommandEditorUtils.isInteger(details[3])) {
                return String.valueOf(world.getTemperature(Integer.valueOf(details[1]), Integer.valueOf(details[2]), Integer.valueOf(details[3])));
            }
        }
        return null;
    }
    
    private static String getBlockDetail(Block block, String detail) {
        switch (detail.toLowerCase()) {
            case "type": {
                return block.getType().name();
            }
            case "is_block_indirectly_powered": {
                return String.valueOf(block.isBlockIndirectlyPowered());
            }
            case "is_block_powered": {
                return String.valueOf(block.isBlockPowered());
            }
            case "is_empty": {
                return String.valueOf(block.isEmpty());
            }
            case "is_liquid": {
                return String.valueOf(block.isLiquid());
            }
            case "is_passable": {
                return String.valueOf(block.isPassable());
            }
            case "power": {
                return String.valueOf(block.getBlockPower());
            }
            case "humidity": {
                return String.valueOf(block.getHumidity());
            }
        }
        return null;
    }
    
    private static String getChunkDetail(Chunk chunk, String detail) {
        switch (detail.toLowerCase()) {
            case "is_force_loaded": {
                return String.valueOf(chunk.isForceLoaded());
            }
            case "is_loaded": {
                return String.valueOf(chunk.isLoaded());
            }
            case "is_slime_chunk": {
                return String.valueOf(chunk.isSlimeChunk());
            }
        }
        if (detail.toLowerCase().startsWith("contains")) {
            String[] detailsInDetail = detail.split("_", 2);
            if (detailsInDetail.length == 2) {
                return String.valueOf(chunk.contains(Material.getMaterial(detailsInDetail[1]).createBlockData()));
            }
        }
        return null;
    }
    
    private static String getLocationDetail(Location location, String detail) {
        switch (detail.toLowerCase()) {
            case "x": {
                return String.valueOf(location.getX());
            }
            case "y": {
                return String.valueOf(location.getY());
            }
            case "z": {
                return String.valueOf(location.getZ());
            }
            case "block_x": {
                return String.valueOf(location.getBlockX());
            }
            case "block_y": {
                return String.valueOf(location.getBlockY());
            }
            case "block_z": {
                return String.valueOf(location.getBlockZ());
            }
            case "pitch": {
                return String.valueOf(location.getPitch());
            }
            case "yaw": {
                return String.valueOf(location.getYaw());
            }
            case "world": {
                return location.getWorld().getName();
            }
        }
        return null;
    }
}
