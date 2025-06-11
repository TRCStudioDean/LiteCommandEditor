package studio.trc.bukkit.litecommandeditor.module.condition;

import java.util.Map;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandCondition;
import studio.trc.bukkit.litecommandeditor.module.CommandConditionType;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.tool.Function;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class WorldCondition 
    extends CommandCondition
{
    @Getter
    private final CommandConditionType conditionType = CommandConditionType.WORLD;
    @Getter
    private final Function function;
    
    public WorldCondition(Function function, String expression) {
        super(keyWordsReplace(expression));
        this.function = function;
    }

    //Format: "World:[WorldName]:[Option]:[Parameters]"
    @Override
    public boolean matchCondition(CommandConfiguration commandConfig, String configPath, CommandSender sender, Map<String, String> placeholders) {
        String[] parameters;
        boolean nor;
        if (getExpression().startsWith("!")) {
            nor = true;
            parameters = LiteCommandEditorUtils.getConditionParameters(getExpression().substring(1), sender, placeholders);
        } else {
            nor = false;
            parameters = LiteCommandEditorUtils.getConditionParameters(getExpression(), sender, placeholders);
        }
        try {
            if (parameters.length > 1) {
                World world = Bukkit.getWorld(parameters[0]);
                if (world != null) {
                    boolean incorrect = false;
                    switch (parameters[1].toLowerCase()) {
                        case "getallowanimals": {
                            if (nor ? !world.getAllowAnimals() : world.getAllowAnimals()) {
                                return true;
                            }
                            break;
                        }
                        case "getallowmonsters": {
                            if (nor ? !world.getAllowMonsters() : world.getAllowMonsters()) {
                                return true;
                            }
                            break;
                        }
                        case "getkeepspawninmemory": {
                            if (nor ? !world.getKeepSpawnInMemory() : world.getKeepSpawnInMemory()) {
                                return true;
                            }
                            break;
                        }
                        case "getpvp": {
                            if (nor ? !world.getPVP() : world.getPVP()) {
                                return true;
                            }
                            break;
                        }
                        case "cangeneratestructures": {
                            if (nor ? !world.canGenerateStructures() : world.canGenerateStructures()) {
                                return true;
                            }
                            break;
                        }
                        case "hasmetadata": {
                            if (parameters.length == 3) {
                                if (nor ? !world.hasMetadata(parameters[2]) : world.hasMetadata(parameters[2])) {
                                    return true;
                                }
                            } else {
                                incorrect = true;
                            }
                            break;
                        }
                        case "hasstorm": {
                            if (nor ? !world.hasStorm() : world.hasStorm()) {
                                return true;
                            }
                            break;
                        }
                        case "isautosave": {
                            if (nor ? !world.isAutoSave() : world.isAutoSave()) {
                                return true;
                            }
                            break;
                        }
                        case "ischunkforceloaded": {
                            if (parameters.length == 4 && LiteCommandEditorUtils.isInteger(parameters[2]) && LiteCommandEditorUtils.isInteger(parameters[3])) {
                                if (nor ? !world.isChunkForceLoaded(Integer.valueOf(parameters[2]), Integer.valueOf(parameters[3])) : world.isChunkForceLoaded(Integer.valueOf(parameters[2]), Integer.valueOf(parameters[3]))) {
                                    return true;
                                }
                            } else {
                                incorrect = true;
                            }
                            break;
                        }
                        case "ischunkgenerated": {
                            if (parameters.length == 4 && LiteCommandEditorUtils.isInteger(parameters[2]) && LiteCommandEditorUtils.isInteger(parameters[3])) {
                                if (nor ? !world.isChunkGenerated(Integer.valueOf(parameters[2]), Integer.valueOf(parameters[3])) : world.isChunkGenerated(Integer.valueOf(parameters[2]), Integer.valueOf(parameters[3]))) {
                                    return true;
                                }
                            } else {
                                incorrect = true;
                            }
                            break;
                        }
                        case "ischunkloaded": {
                            if (parameters.length == 4 && LiteCommandEditorUtils.isInteger(parameters[2]) && LiteCommandEditorUtils.isInteger(parameters[3])) {
                                if (nor ? !world.isChunkLoaded(Integer.valueOf(parameters[2]), Integer.valueOf(parameters[3])) : world.isChunkLoaded(Integer.valueOf(parameters[2]), Integer.valueOf(parameters[3]))) {
                                    return true;
                                }
                            } else {
                                incorrect = true;
                            }
                            break;
                        }
                        case "isclearweather": {
                            if (nor ? !world.isClearWeather() : world.isClearWeather()) {
                                return true;
                            }
                            break;
                        }
                        case "isgamerule": {
                            if (parameters.length == 3) {
                                if (nor ? !world.isGameRule(parameters[2]) : world.isGameRule(parameters[2])) {
                                    return true;
                                }
                            } else {
                                incorrect = true;
                            }
                            break;
                        }
                        case "ishardcore": {
                            if (nor ? !world.isHardcore() : world.isHardcore()) {
                                return true;
                            }
                            break;
                        }
                        case "isthundering": {
                            if (nor ? !world.isThundering() : world.isThundering()) {
                                return true;
                            }
                            break;
                        }
                        default: {
                            if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                                unknownCondition(commandConfig.getFileName(), configPath);
                            }
                            break;
                        }
                    }
                    if (incorrect) {
                        if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                            incorrectParameters(commandConfig, configPath);
                        }
                    }
                } else {
                    if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                        unknownWorld(commandConfig.getFileName(), configPath, parameters[0]);
                    }
                }
            } else {
                if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                    unknownCondition(commandConfig.getFileName(), configPath);
                }
            }
        } catch (NoSuchMethodError t) {
            return false;
        }
        return getExpression().startsWith("!");
    }
    
    private void unknownCondition(String fileName, String configPath) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", fileName);
        placeholders.put("{configPath}", configPath);
        placeholders.put("{expression}", getExpression().startsWith("!") ? "!World:" + getExpression().substring(1) : "World:" + getExpression());
        placeholders.put("{conditionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Condition-Messages.Conditions-Type.World"));
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Condition-Messages.Unknown-Condition", placeholders);
    }
    
    private void unknownWorld(String fileName, String configPath, String worldName) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", fileName);
        placeholders.put("{configPath}", configPath);
        placeholders.put("{worldName}", worldName);
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Condition-Messages.Unknown-World-Name", placeholders);
    }
    
    private void incorrectParameters(CommandConfiguration commandConfig, String configPath) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{conditionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Condition-Messages.Conditions-Type.World"));
        placeholders.put("{fileName}", commandConfig.getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{expression}", getExpression().startsWith("!") ? "!World:" + getExpression().substring(1) : "World:" + getExpression());
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Condition-Messages.Incorrect-Parameters", placeholders);
    }
    
    private static String keyWordsReplace(String expression) {
        if (expression.toLowerCase().startsWith("world:")) {
            expression = expression.substring("world:".length());
        } else if (expression.toLowerCase().startsWith("!world:")) {
            expression = "!" + expression.substring("!world:".length());
        }
        return expression.replace(" ", "");
    }
}
