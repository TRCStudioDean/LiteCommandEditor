package studio.trc.bukkit.litecommandeditor.module.condition;

import java.util.Map;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandCondition;
import studio.trc.bukkit.litecommandeditor.module.CommandConditionType;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.tool.Function;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class PlayerCondition
    extends CommandCondition
{
    @Getter
    private final CommandConditionType conditionType = CommandConditionType.PLAYER;
    @Getter
    private final Function function;
    
    public PlayerCondition(Function function, String expression) {
        super(keyWordsReplace(expression));
        this.function = function;
    }

    //Format: "Player:[PlayerName]:[Option]:[Parameters]"
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
                Player player = Bukkit.getPlayer(parameters[0]);
                if (player != null) {
                    boolean incorrect = false;
                    switch (parameters[1].toLowerCase()) {
                        case "isblocking": {
                            if (nor ? !player.isBlocking() : player.isBlocking()) {
                                return true;
                            }
                            break;
                        }
                        case "iscollidable": {
                            if (nor ? !player.isCollidable() : player.isCollidable()) {
                                return true;
                            }
                            break;
                        }
                        case "iscoversing": {
                            if (nor ? !player.isConversing() : player.isConversing()) {
                                return true;
                            }
                            break;
                        }
                        case "iscustomnamevisible": {
                            if (nor ? !player.isCustomNameVisible() : player.isCustomNameVisible()) {
                                return true;
                            }
                            break;
                        }
                        case "isdead": {
                            if (nor ? !player.isDead() : player.isDead()) {
                                return true;
                            }
                            break;
                        }
                        case "isflying": {
                            if (nor ? !player.isFlying() : player.isFlying()) {
                                return true;
                            }
                            break;
                        }
                        case "isgliding": {
                            if (nor ? !player.isGliding() : player.isGliding()) {
                                return true;
                            }
                            break;
                        }
                        case "isglowing": {
                            if (nor ? !player.isGlowing() : player.isGlowing()) {
                                return true;
                            }
                            break;
                        }
                        case "ishandraised": {
                            if (nor ? !player.isHandRaised() : player.isHandRaised()) {
                                return true;
                            }
                            break;
                        }
                        case "ishealthscaled": {
                            if (nor ? !player.isHealthScaled() : player.isHealthScaled()) {
                                return true;
                            }
                            break;
                        }
                        case "isinwater": {
                            if (nor ? !player.isInWater() : player.isInWater()) {
                                return true;
                            }
                            break;
                        }
                        case "isinsidevehicle": {
                            if (nor ? !player.isInsideVehicle() : player.isInsideVehicle()) {
                                return true;
                            }
                            break;
                        }
                        case "isinvisible": {
                            if (nor ? !player.isInvisible() : player.isInvisible()) {
                                return true;
                            }
                            break;
                        }
                        case "isinvulnerable": {
                            if (nor ? !player.isInvulnerable() : player.isInvulnerable()) {
                                return true;
                            }
                            break;
                        }
                        case "isonground": {
                            if (nor ? !player.isOnGround() : player.isOnGround()) {
                                return true;
                            }
                            break;
                        }
                        case "isop": {
                            if (nor ? !player.isOp() : player.isOp()) {
                                return true;
                            }
                            break;
                        }
                        case "isplayertimerelative": {
                            if (nor ? !player.isPlayerTimeRelative() : player.isPlayerTimeRelative()) {
                                return true;
                            }
                            break;
                        }
                        case "isriptiding": {
                            if (nor ? !player.isRiptiding() : player.isRiptiding()) {
                                return true;
                            }
                            break;
                        }
                        case "isslient": {
                            if (nor ? !player.isSilent() : player.isSilent()) {
                                return true;
                            }
                            break;
                        }
                        case "issleeping": {
                            if (nor ? !player.isSleeping() : player.isSleeping()) {
                                return true;
                            }
                            break;
                        }
                        case "issleepingignored": {
                            if (nor ? !player.isSleepingIgnored() : player.isSleepingIgnored()) {
                                return true;
                            }
                            break;
                        }
                        case "issnakeing": {
                            if (nor ? !player.isSneaking() : player.isSneaking()) {
                                return true;
                            }
                            break;
                        }
                        case "issprinting": {
                            if (nor ? !player.isSprinting() : player.isSprinting()) {
                                return true;
                            }
                            break;
                        }
                        case "isswimming": {
                            if (nor ? !player.isSwimming() : player.isSwimming()) {
                                return true;
                            }
                            break;
                        }
                        case "iswhitelisted": {
                            if (nor ? !player.isWhitelisted() : player.isWhitelisted()) {
                                return true;
                            }
                            break;
                        }
                        case "getallowflight": {
                            if (nor ? !player.getAllowFlight() : player.getAllowFlight()) {
                                return true;
                            }
                            break;
                        }
                        case "getcanpickupitems": {
                            if (nor ? !player.getCanPickupItems() : player.getCanPickupItems()) {
                                return true;
                            }
                            break;
                        }
                        case "hasgravity": {
                            if (nor ? !player.hasGravity() : player.hasGravity()) {
                                return true;
                            }
                            break;
                        }
                        case "hasplayerbefore": {
                            if (nor ? !player.hasPlayedBefore() : player.hasPlayedBefore()) {
                                return true;
                            }
                            break;
                        }
                        case "cansee": {
                            if (parameters.length == 3) {
                                Player target = Bukkit.getPlayer(parameters[2]);
                                if (target != null) {
                                    if (nor ? !player.canSee(target) : player.canSee(target)) {
                                        return true;
                                    }
                                } else {
                                    unknownPlayer(commandConfig.getFileName(), configPath, parameters[2]);
                                }
                            } else {
                                incorrect = true;
                            }
                            break;
                        }
                        case "hascooldown": {
                            if (parameters.length == 3) {
                                Material material = Material.getMaterial(parameters[2]);
                                if (material != null) {
                                    if (nor ? !player.hasCooldown(material) : player.hasCooldown(material)) {
                                        return true;
                                    }
                                } else {
                                    incorrect = true;
                                }
                            } else {
                                incorrect = true;
                            }
                            break;
                        }
                        case "hasmetadata": {
                            if (parameters.length == 3) {
                                if (nor ? !player.hasMetadata(parameters[2]) : player.hasMetadata(parameters[2])) {
                                    return true;
                                }
                            } else {
                                incorrect = true;
                            }
                            break;
                        }
                        case "haspermission": {
                            if (parameters.length == 3) {
                                if (nor ? !player.hasPermission(parameters[2]) : player.hasPermission(parameters[2])) {
                                    return true;
                                }
                            } else {
                                incorrect = true;
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
                        unknownPlayer(commandConfig.getFileName(), configPath, parameters[0]);
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
        placeholders.put("{expression}", getExpression().startsWith("!") ? "!Player:" + getExpression().substring(1) : "Player:" + getExpression());
        placeholders.put("{conditionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Condition-Messages.Conditions-Type.Player"));
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Condition-Messages.Unknown-Condition", placeholders);
    }
    
    private void unknownPlayer(String fileName, String configPath, String playerName) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", fileName);
        placeholders.put("{configPath}", configPath);
        placeholders.put("{playerName}", playerName);
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Condition-Messages.Unknown-Player-Name", placeholders);
    }
    
    private void incorrectParameters(CommandConfiguration commandConfig, String configPath) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{conditionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Condition-Messages.Conditions-Type.Player"));
        placeholders.put("{fileName}", commandConfig.getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{expression}", getExpression().startsWith("!") ? "!Player:" + getExpression().substring(1) : "Player:" + getExpression());
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Condition-Messages.Incorrect-Parameters", placeholders);
    }
    
    private static String keyWordsReplace(String expression) {
        if (expression.toLowerCase().startsWith("player:")) {
            expression = expression.substring("player:".length());
        } else if (expression.toLowerCase().startsWith("!player:")) {
            expression = "!" + expression.substring("!player:".length());
        }
        return expression.replace(" ", "");
    }
}
