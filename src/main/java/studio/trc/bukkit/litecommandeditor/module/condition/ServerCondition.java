package studio.trc.bukkit.litecommandeditor.module.condition;

import java.util.Map;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandCondition;
import studio.trc.bukkit.litecommandeditor.module.CommandConditionType;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.tool.Function;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class ServerCondition 
    extends CommandCondition
{
    @Getter
    private final CommandConditionType conditionType = CommandConditionType.SERVER;
    @Getter
    private final Function function;
    
    public ServerCondition(Function function, String expression) {
        super(keyWordsReplace(expression));
        this.function = function;
    }

    //Format: "Server:[Option]:[Parameters]"
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
            boolean incorrect = false;
            switch (parameters[0].toLowerCase()) {
                case "getallowend": {
                    if (nor ? !Bukkit.getAllowEnd() : Bukkit.getAllowEnd()) {
                        return true;
                    }
                    break;
                }
                case "getallowflight": {
                    if (nor ? !Bukkit.getAllowFlight() : Bukkit.getAllowFlight()) {
                        return true;
                    }
                    break;
                }
                case "getallownether": {
                    if (nor ? !Bukkit.getAllowNether() : Bukkit.getAllowNether()) {
                        return true;
                    }
                    break;
                }
                case "getgeneratestructures": {
                    if (nor ? !Bukkit.getGenerateStructures() : Bukkit.getGenerateStructures()) {
                        return true;
                    }
                    break;
                }
                case "getonlinemode": {
                    if (nor ? !Bukkit.getOnlineMode() : Bukkit.getOnlineMode()) {
                        return true;
                    }
                    break;
                }
                case "haswhitelist": {
                    if (nor ? !Bukkit.hasWhitelist() : Bukkit.hasWhitelist()) {
                        return true;
                    }
                    break;
                }
                case "ishardcore": {
                    if (nor ? !Bukkit.isHardcore() : Bukkit.isHardcore()) {
                        return true;
                    }
                    break;
                }
                case "pluginManager": {
                    if (parameters.length > 1) {
                        return pluginManager(commandConfig, configPath, parameters, nor);
                    }
                    incorrect = true;
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
        } catch (NoSuchMethodError t) {
            return false;
        }
        return getExpression().startsWith("!");
    }
    
    private boolean pluginManager(CommandConfiguration commandConfig, String configPath, String[] parameters, boolean nor) {
        PluginManager manager = Bukkit.getPluginManager();
        try {
            boolean incorrect = false;
            switch (parameters[1].toLowerCase()) {
                case "ispluginenabled": {
                    if (parameters.length > 2) {
                        if (nor ? !manager.isPluginEnabled(parameters[2]) : manager.isPluginEnabled(parameters[2])) {
                            return true;
                        }
                    }
                    incorrect = true;
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
        } catch (NoSuchMethodError t) {
            return false;
        }
        return getExpression().startsWith("!");
    }
    
    private void unknownCondition(String fileName, String configPath) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", fileName);
        placeholders.put("{configPath}", configPath);
        placeholders.put("{expression}", getExpression().startsWith("!") ? "!Server:" + getExpression().substring(1) : "Server:" + getExpression());
        placeholders.put("{conditionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Condition-Messages.Conditions-Type.Server"));
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Condition-Messages.Unknown-Condition", placeholders);
    }
    
    private void incorrectParameters(CommandConfiguration commandConfig, String configPath) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{conditionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Condition-Messages.Conditions-Type.Server"));
        placeholders.put("{fileName}", commandConfig.getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{expression}", getExpression().startsWith("!") ? "!Server:" + getExpression().substring(1) : "Server:" + getExpression());
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Condition-Messages.Incorrect-Parameters", placeholders);
    }
    
    private static String keyWordsReplace(String expression) {
        if (expression.toLowerCase().startsWith("server:")) {
            expression = expression.substring("server:".length());
        } else if (expression.toLowerCase().startsWith("!server:")) {
            expression = "!" + expression.substring("!server:".length());
        }
        return expression.replace(" ", "");
    }
}
