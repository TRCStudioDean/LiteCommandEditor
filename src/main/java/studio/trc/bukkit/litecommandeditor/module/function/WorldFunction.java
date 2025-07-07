package studio.trc.bukkit.litecommandeditor.module.function;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunction;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunctionTask;
import studio.trc.bukkit.litecommandeditor.module.tool.ItemCollection;
import studio.trc.bukkit.litecommandeditor.module.tool.ItemInfo;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;
import studio.trc.bukkit.litecommandeditor.util.NMSUtils;

public class WorldFunction 
    implements CommandFunctionTask
{
    @Getter
    private final String expression;
    @Getter
    private final String configPath;
    @Getter
    private final CommandFunction function;
    @Getter
    private final String identifier = "WorldFunction";
    
    public WorldFunction(CommandFunction function, String expression, String configPath) {
        this.expression = expression;
        this.function = function;
        this.configPath = configPath;
    }

    @Override
    public void executeTask(CommandSender sender, Map<String, String> placeholders) {
        String[] parameters = MessageUtil.splitStringBySymbol(MessageUtil.replacePlaceholders(sender, expression, placeholders), ':');
        try {
            if (parameters.length > 1) {
                World world = Bukkit.getWorld(parameters[0]);
                if (world != null) {
                    boolean incorrect = true;
                    switch (parameters[1].toLowerCase()) {
                        case "addpluginchunkticket": {
                            if (parameters.length > 4 && LiteCommandEditorUtils.isInteger(parameters[2]) && LiteCommandEditorUtils.isInteger(parameters[3]) && Bukkit.getPluginManager().getPlugin(parameters[4]) != null) {
                                world.addPluginChunkTicket(Integer.valueOf(parameters[2]), Integer.valueOf(parameters[3]), Bukkit.getPluginManager().getPlugin(parameters[4]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "createexplosion": {
                            if (parameters.length > 5 && LiteCommandEditorUtils.isDouble(parameters[2]) && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4]) && LiteCommandEditorUtils.isFloat(parameters[5])) {
                                double x = Double.valueOf(parameters[2]);
                                double y = Double.valueOf(parameters[3]);
                                double z = Double.valueOf(parameters[4]);
                                float power = Float.valueOf(parameters[5]);
                                boolean setFire = false;
                                boolean breakBlocks = false;
                                Player entity = null;
                                if (parameters.length > 6) {
                                    setFire = Boolean.valueOf(parameters[6]);
                                }
                                if (parameters.length > 7) {
                                    breakBlocks = Boolean.valueOf(parameters[7]);
                                }
                                if (parameters.length > 8) {
                                    entity = Bukkit.getPlayer(parameters[8]);
                                }
                                world.createExplosion(x, y, z, power, setFire, breakBlocks, entity);
                                incorrect = false;
                            }
                            break;
                        }
                        case "dropitem": {
                            if (parameters.length > 5 && LiteCommandEditorUtils.isDouble(parameters[2]) && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4])) {
                                ItemCollection collection = function.getCommandConfig().getItemCollection();
                                double x = Double.valueOf(parameters[2]);
                                double y = Double.valueOf(parameters[3]);
                                double z = Double.valueOf(parameters[4]);
                                ItemInfo item = collection.getItem(LiteCommandEditorUtils.rebuildText(parameters, 5));
                                if (item != null) {
                                    world.dropItem(new Location(world, x, y, z), item.getItem(sender, placeholders));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "dropitemnaturally": {
                            if (parameters.length > 5 && LiteCommandEditorUtils.isDouble(parameters[2]) && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4])) {
                                ItemCollection collection = function.getCommandConfig().getItemCollection();
                                double x = Double.valueOf(parameters[2]);
                                double y = Double.valueOf(parameters[3]);
                                double z = Double.valueOf(parameters[4]);
                                ItemInfo item = collection.getItem(LiteCommandEditorUtils.rebuildText(parameters, 5));
                                if (item != null) {
                                    world.dropItemNaturally(new Location(world, x, y, z), item.getItem(sender, placeholders));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "generatetree": {
                            if (parameters.length > 5 && LiteCommandEditorUtils.isDouble(parameters[2]) && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4])) {
                                double x = Double.valueOf(parameters[2]);
                                double y = Double.valueOf(parameters[3]);
                                double z = Double.valueOf(parameters[4]);
                                world.generateTree(new Location(world, x, y, z), TreeType.valueOf(parameters[5]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "loadchunk": {
                            if (parameters.length > 4 && LiteCommandEditorUtils.isInteger(parameters[2]) && LiteCommandEditorUtils.isInteger(parameters[3])) {
                                int x = Integer.valueOf(parameters[2]);
                                int z = Integer.valueOf(parameters[3]);
                                boolean generate = false;
                                if (parameters.length > 5) {
                                    generate = Boolean.valueOf(parameters[4]);
                                }
                                world.loadChunk(x, z, generate);
                                incorrect = false;
                            }
                            break;
                        }
                        case "playeffect": {
                            if (parameters.length > 6 && LiteCommandEditorUtils.isDouble(parameters[2]) && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4]) && LiteCommandEditorUtils.isInteger(parameters[6])) {
                                double x = Double.valueOf(parameters[2]);
                                double y = Double.valueOf(parameters[3]);
                                double z = Double.valueOf(parameters[4]);
                                int data = Integer.valueOf(parameters[6]);
                                if (parameters.length > 7 && LiteCommandEditorUtils.isInteger(parameters[7])) {
                                    int radius = Integer.valueOf(parameters[7]);
                                    world.playEffect(new Location(world, x, y, z), Effect.valueOf(parameters[5]), data, radius);
                                } else {
                                    world.playEffect(new Location(world, x, y, z), Effect.valueOf(parameters[5]), data);
                                }
                                incorrect = false;
                            }
                            break;
                        }
                        case "playsound": {
                            if (parameters.length > 7 && LiteCommandEditorUtils.isDouble(parameters[2]) && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4]) && LiteCommandEditorUtils.isFloat(parameters[6]) && LiteCommandEditorUtils.isFloat(parameters[7])) {
                                double x = Double.valueOf(parameters[2]);
                                double y = Double.valueOf(parameters[3]);
                                double z = Double.valueOf(parameters[4]);
                                float volume = Float.valueOf(parameters[6]);
                                float pitch = Float.valueOf(parameters[7]);
                                world.playSound(new Location(world, x, y, z), parameters[5], volume, pitch);
                                incorrect = false;
                            }
                            break;
                        }
                        case "removepluginchunkticket": {
                            if (parameters.length > 4 && LiteCommandEditorUtils.isInteger(parameters[2]) && LiteCommandEditorUtils.isInteger(parameters[3]) && Bukkit.getPluginManager().getPlugin(parameters[4]) != null) {
                                world.removePluginChunkTicket(Integer.valueOf(parameters[2]), Integer.valueOf(parameters[3]), Bukkit.getPluginManager().getPlugin(parameters[4]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "removepluginchunktickets": {
                            if (parameters.length > 2 && Bukkit.getPluginManager().getPlugin(parameters[2]) != null) {
                                world.removePluginChunkTickets(Bukkit.getPluginManager().getPlugin(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "save": {
                            world.save();
                            incorrect = false;
                            break;
                        }
                        case "setambientspawnlimit": {
                            if (parameters.length > 2 && LiteCommandEditorUtils.isInteger(parameters[2])) {
                                world.setAmbientSpawnLimit(Integer.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setanimalspawnlimit": {
                            if (parameters.length > 2 && LiteCommandEditorUtils.isInteger(parameters[2])) {
                                world.setAnimalSpawnLimit(Integer.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setautosave": {
                            if (parameters.length > 2) {
                                world.setAutoSave(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setbiome": {
                            if (parameters.length == 5 && LiteCommandEditorUtils.isInteger(parameters[2]) && LiteCommandEditorUtils.isInteger(parameters[3])) {
                                int x = Integer.valueOf(parameters[2]);
                                int z = Integer.valueOf(parameters[3]);
                                world.setBiome(x, z, Biome.valueOf(parameters[4]));
                                incorrect = false;
                            } else if (parameters.length > 5 && LiteCommandEditorUtils.isInteger(parameters[2]) && LiteCommandEditorUtils.isInteger(parameters[3]) && LiteCommandEditorUtils.isInteger(parameters[4])) {
                                int x = Integer.valueOf(parameters[2]);
                                int y = Integer.valueOf(parameters[3]);
                                int z = Integer.valueOf(parameters[4]);
                                world.setBiome(x, y, z, Biome.valueOf(parameters[5]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setchunkforceloaded": {
                            if (parameters.length > 4 && LiteCommandEditorUtils.isInteger(parameters[2]) && LiteCommandEditorUtils.isInteger(parameters[3])) {
                                world.setChunkForceLoaded(Integer.valueOf(parameters[2]), Integer.valueOf(parameters[3]), Boolean.valueOf(parameters[4]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setdifficulty": {
                            if (parameters.length > 2) {
                                world.setDifficulty(Difficulty.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setgamerule": {
                            if (parameters.length > 3) {
                                GameRule<?> gamerule = GameRule.getByName(parameters[2]);
                                if (gamerule != null) {
                                    if (gamerule.getType().equals(Integer.class)) {
                                        world.setGameRule((GameRule<Integer>) gamerule, Integer.valueOf(parameters[3]));
                                        incorrect = false;
                                    } else if (gamerule.getType().equals(Boolean.class)) {
                                        world.setGameRule((GameRule<Boolean>) gamerule, Boolean.valueOf(parameters[3]));
                                        incorrect = false;
                                    }
                                }
                            }
                            break;
                        }
                        case "setgamerulevalue": {
                            if (parameters.length > 3) {
                                world.setGameRuleValue(parameters[2], parameters[3]);
                                incorrect = false;
                            }
                            break;
                        }
                        case "sethardcore": {
                            if (parameters.length > 2) {
                                world.setHardcore(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setkeepspawninmemory": {
                            if (parameters.length > 2) {
                                world.setKeepSpawnInMemory(Boolean.valueOf(parameters[2])); 
                                incorrect = false;
                            }
                            break;
                        }
                        case "setmonsterspawnlimit": {
                            if (parameters.length > 2 && LiteCommandEditorUtils.isInteger(parameters[2])) {
                                world.setMonsterSpawnLimit(Integer.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setpvp": {
                            if (parameters.length > 2) {
                                world.setPVP(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setspawnflags": {
                            if (parameters.length > 3) {
                                world.setSpawnFlags(Boolean.valueOf(parameters[2]), Boolean.valueOf(parameters[3]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setspawnlocation": {
                            if (parameters.length > 4 && LiteCommandEditorUtils.isDouble(parameters[2]) && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4])) {
                                double x = Double.valueOf(parameters[2]);
                                double y = Double.valueOf(parameters[3]);
                                double z = Double.valueOf(parameters[4]);
                                if (parameters.length > 6 && LiteCommandEditorUtils.isFloat(parameters[5]) && LiteCommandEditorUtils.isFloat(parameters[6])) {
                                    world.setSpawnLocation(new Location(world, x, y, z, Float.valueOf(parameters[5]), Float.valueOf(parameters[6])));
                                    incorrect = false;
                                } else {
                                    world.setSpawnLocation(new Location(world, x, y, z));
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "setstorm": {
                            if (parameters.length > 2) {
                                world.setStorm(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setthunderduration": {
                            if (parameters.length > 2 && LiteCommandEditorUtils.isInteger(parameters[2])) {
                                world.setThunderDuration(Integer.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setthundering": {
                            if (parameters.length > 2) {
                                world.setThundering(Boolean.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setticksperambientspawns": {
                            if (parameters.length > 2 && LiteCommandEditorUtils.isInteger(parameters[2])) {
                                world.setTicksPerAmbientSpawns(Integer.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "settickperanimalspawns": {
                            if (parameters.length > 2 && LiteCommandEditorUtils.isInteger(parameters[2])) {
                                world.setTicksPerAnimalSpawns(Integer.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "settickpermonsterspawns": {
                            if (parameters.length > 2 && LiteCommandEditorUtils.isInteger(parameters[2])) {
                                world.setTicksPerMonsterSpawns(Integer.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "settickperwaterambientspawns": {
                            if (parameters.length > 2 && LiteCommandEditorUtils.isInteger(parameters[2])) {
                                world.setTicksPerWaterAmbientSpawns(Integer.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "settickperwaterspawns": {
                            if (parameters.length > 2 && LiteCommandEditorUtils.isInteger(parameters[2])) {
                                world.setTicksPerWaterSpawns(Integer.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "settime": {
                            if (parameters.length > 2 && LiteCommandEditorUtils.isLong(parameters[2])) {
                                world.setTime(Long.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setwaterambientspawnlimit": {
                            if (parameters.length > 2 && LiteCommandEditorUtils.isInteger(parameters[2])) {
                                world.setWaterAmbientSpawnLimit(Integer.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setwateranimalspawnlimit": {
                            if (parameters.length > 2 && LiteCommandEditorUtils.isInteger(parameters[2])) {
                                world.setWaterAnimalSpawnLimit(Integer.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "setweatherduration": {
                            if (parameters.length > 2 && LiteCommandEditorUtils.isInteger(parameters[2])) {
                                world.setWeatherDuration(Integer.valueOf(parameters[2]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "spawnarrow": {
                            if (parameters.length > 9 && LiteCommandEditorUtils.isDouble(parameters[2]) && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4]) && LiteCommandEditorUtils.isDouble(parameters[5]) && LiteCommandEditorUtils.isDouble(parameters[6]) && LiteCommandEditorUtils.isDouble(parameters[7]) && LiteCommandEditorUtils.isFloat(parameters[8]) && LiteCommandEditorUtils.isFloat(parameters[9])) {
                                double x = Double.valueOf(parameters[2]);
                                double y = Double.valueOf(parameters[3]);
                                double z = Double.valueOf(parameters[4]);
                                world.spawnArrow(new Location(world, x, y, z), new Vector(Double.valueOf(parameters[5]), Double.valueOf(parameters[6]), Double.valueOf(parameters[7])), Float.valueOf(parameters[8]), Float.valueOf(parameters[9]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "spawnentity": {
                            if (parameters.length > 5 && LiteCommandEditorUtils.isDouble(parameters[2]) && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4])) {
                                double x = Double.valueOf(parameters[2]);
                                double y = Double.valueOf(parameters[3]);
                                double z = Double.valueOf(parameters[4]);
                                world.spawnEntity(new Location(world, x, y, z), EntityType.valueOf(parameters[5]));
                                incorrect = false;
                            }
                            break;
                        }
                        case "spawnfallingblock": {
                            if (parameters.length > 5 && LiteCommandEditorUtils.isDouble(parameters[2]) && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4])) {
                                double x = Double.valueOf(parameters[2]);
                                double y = Double.valueOf(parameters[3]);
                                double z = Double.valueOf(parameters[4]);
                                ItemInfo item = function.getCommandConfig().getItemCollection().getItem(parameters[5]);
                                if (item != null) {
                                    world.spawnFallingBlock(new Location(world, x, y, z), item.getItem(sender, placeholders).getData());
                                    incorrect = false;
                                }
                            }
                            break;
                        }
                        case "spawnparticle": {
                            if (parameters.length > 6 && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4]) && LiteCommandEditorUtils.isDouble(parameters[5]) && LiteCommandEditorUtils.isInteger(parameters[6])) {
                                double x = Double.valueOf(parameters[3]);
                                double y = Double.valueOf(parameters[4]);
                                double z = Double.valueOf(parameters[5]);
                                int count = Integer.valueOf(parameters[6]);
                                if (parameters.length == 7) {
                                    NMSUtils.ParticleUtil.spawnParticle(world, parameters[2], x, y, z, count);
                                    incorrect = false;
                                } else if (parameters.length > 9 && LiteCommandEditorUtils.isDouble(parameters[7]) && LiteCommandEditorUtils.isDouble(parameters[8]) && LiteCommandEditorUtils.isDouble(parameters[9])) {
                                    double offsetX = Double.valueOf(parameters[7]);
                                    double offsetY = Double.valueOf(parameters[8]);
                                    double offsetZ = Double.valueOf(parameters[9]);
                                    if (parameters.length == 10) {
                                        NMSUtils.ParticleUtil.spawnParticle(world, parameters[2], x, y, z, count, offsetX, offsetY, offsetZ);
                                        incorrect = false;
                                    } else if (LiteCommandEditorUtils.isDouble(parameters[10])) {
                                        NMSUtils.ParticleUtil.spawnParticle(world, parameters[2], x, y, z, count, offsetX, offsetY, offsetZ, Double.valueOf(parameters[10]));
                                        incorrect = false;
                                    }
                                }
                            }
                            break;
                        }
                        case "strikelightning": {
                            if (parameters.length > 5 && LiteCommandEditorUtils.isDouble(parameters[2]) && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4])) {
                                double x = Double.valueOf(parameters[2]);
                                double y = Double.valueOf(parameters[3]);
                                double z = Double.valueOf(parameters[4]);
                                world.strikeLightning(new Location(world, x, y, z));
                                incorrect = false;
                            }
                            break;
                        }
                        case "strikelightningeffect": {
                            if (parameters.length > 5 && LiteCommandEditorUtils.isDouble(parameters[2]) && LiteCommandEditorUtils.isDouble(parameters[3]) && LiteCommandEditorUtils.isDouble(parameters[4])) {
                                double x = Double.valueOf(parameters[2]);
                                double y = Double.valueOf(parameters[3]);
                                double z = Double.valueOf(parameters[4]);
                                world.strikeLightningEffect(new Location(world, x, y, z));
                                incorrect = false;
                            }
                            break;
                        }
                        case "unloadchunk": {
                            if (parameters.length > 4 && LiteCommandEditorUtils.isInteger(parameters[2]) && LiteCommandEditorUtils.isInteger(parameters[3])) {
                                int x = Integer.valueOf(parameters[2]);
                                int z = Integer.valueOf(parameters[3]);
                                boolean save = false;
                                if (parameters.length > 5) {
                                    save = Boolean.valueOf(parameters[4]);
                                }
                                world.unloadChunk(x, z, save);
                                incorrect = false;
                            }
                            break;
                        }
                        case "unloadchunkrequest": {
                            if (parameters.length > 4 && LiteCommandEditorUtils.isInteger(parameters[2]) && LiteCommandEditorUtils.isInteger(parameters[3])) {
                                int x = Integer.valueOf(parameters[2]);
                                int z = Integer.valueOf(parameters[3]);
                                world.unloadChunkRequest(x, z);
                                incorrect = false;
                            }
                            break;
                        }
                        default: {
                            if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                                unknownFunction(parameters[1]);
                            }
                            incorrect = false;
                            break;
                        }
                    }
                    if (incorrect) {
                        if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                            incorrectParameters(parameters[1], String.join(":", Arrays.asList(parameters).stream().skip(2).toArray(String[]::new)));
                        }
                    }
                } else {
                    if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                        unknownWorld(parameters[0]);
                    }
                }
            } else {
                if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                    unknownFunction(expression);
                }
            }
        } catch (NoSuchMethodError t) {}
    }

    @Override
    public String toString() {
        return "[" + getIdentifier() + "]: Expression=" + expression;
    }
    
    private void incorrectParameters(String functionName, String parameters) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", function.getCommandConfig().getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{parameters}", parameters);
        placeholders.put("{functionName}", functionName);
        placeholders.put("{functionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Function-Messages.Functions-Type.World"));
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Function-Messages.Incorrect-Parameters", placeholders);
    }
    
    private void unknownWorld(String worldName) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", function.getCommandConfig().getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{worldName}", worldName);
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Function-Messages.Unknown-World-Name", placeholders);
    }
    
    private void unknownFunction(String functionName) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", function.getCommandConfig().getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{functionName}", functionName);
        placeholders.put("{functionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Function-Messages.Functions-Type.World"));
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Function-Messages.Unknown-Function", placeholders);
    }
    
    public static WorldFunction build(CommandFunction function, Map map, String configPath) {
        if (map.get("World-Function") != null) {
            return new WorldFunction(function, map.get("World-Function").toString(), configPath);
        }
        return null;
    }
    
    public static List<WorldFunction> build(CommandFunction function, List<String> functions, String configPath) {
        return functions.stream().map(syntax -> new WorldFunction(function, syntax, configPath)).collect(Collectors.toList());
    }
}
