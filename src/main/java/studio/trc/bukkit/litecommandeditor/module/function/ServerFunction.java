package studio.trc.bukkit.litecommandeditor.module.function;

import com.pa_project.lib.json.JSONObject;
import com.pa_project.lib.json.JSONUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunction;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunctionTask;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class ServerFunction 
    implements CommandFunctionTask
{
    @Getter
    private final String expression;
    @Getter
    private final String configPath;
    @Getter
    private final CommandFunction function;
    @Getter
    private final String identifier = "ServerFunction";

    public ServerFunction(CommandFunction function, String expression, String configPath) {
        this.expression = expression;
        this.function = function;
        this.configPath = configPath;
    }

    @Override
    public void executeTask(CommandSender sender, Map<String, String> placeholders) {
        String[] parameters = MessageUtil.splitStringBySymbol(MessageUtil.replacePlaceholders(sender, expression, placeholders), ':');
        try {
            boolean incorrect = true;
            switch (parameters[0].toLowerCase()) {
                case "banip": {
                    if (parameters.length > 1) {
                        Bukkit.banIP(parameters[1]);
                        incorrect = false;
                    }
                    break;
                }
                case "broadcastmessage": {
                    if (parameters.length > 1) {
                        Bukkit.broadcastMessage(LiteCommandEditorUtils.rebuildText(parameters, 1));
                        incorrect = false;
                    }
                    break;
                }
                case "clearrecipes": {
                    Bukkit.clearRecipes();
                    incorrect = false;
                    break;
                }
                case "createworld": {
                    if (parameters.length > 1) {
                        WorldCreator creator = WorldCreator.name(parameters[1]);
                        if (parameters.length > 2 && JSONUtils.isJSON(LiteCommandEditorUtils.rebuildText(parameters, 2))) {
                            try {
                                JSONObject settings = JSONObject.toJSONObject(LiteCommandEditorUtils.rebuildText(parameters, 2));
                                if (settings.containsKey("Environment")) {
                                    creator.environment(World.Environment.valueOf(settings.getString("Environment").toUpperCase()));
                                }
                                if (settings.containsKey("GenerateStructures")) {
                                    creator.generateStructures(settings.getBoolean("GenerateStructures"));
                                }
                                if (settings.containsKey("GeneratorSettings")) {
                                    creator.generatorSettings(settings.getJSONObject("GeneratorSettings").toJSONString());
                                }
                                if (settings.containsKey("Hardcore")) {
                                    creator.hardcore(settings.getBoolean("Hardcore"));
                                }
                                if (settings.containsKey("Seed")) {
                                    creator.seed(settings.getLong("Seed"));
                                }
                                if (settings.containsKey("Type")) {
                                    creator.type(WorldType.valueOf(settings.getString("Type")));
                                }
                                Bukkit.createWorld(creator);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            Bukkit.createWorld(creator);
                        }
                        incorrect = false;
                    }
                    break;
                }
                case "dispatchcommand": {
                    Bukkit.dispatchCommand(Bukkit.getPlayer(parameters[1]), LiteCommandEditorUtils.rebuildText(parameters, 2));
                    incorrect = false;
                    break;
                }
                case "reload": {
                    Bukkit.reload();
                    incorrect = false;
                    break;
                }
                case "reloaddata": {
                    Bukkit.reloadData();
                    incorrect = false;
                    break;
                }
                case "reloadwhitelist": {
                    Bukkit.reloadWhitelist();
                    incorrect = false;
                    break;
                }
                case "resetrecipes": {
                    Bukkit.resetRecipes();
                    incorrect = false;
                    break;
                }
                case "saveplayers": {
                    Bukkit.savePlayers();
                    incorrect = false;
                    break;
                }
                case "setdefaultgamemode": {
                    if (parameters.length > 1) {
                        try {
                            Bukkit.setDefaultGameMode(GameMode.valueOf(parameters[1].toUpperCase()));
                            incorrect = false;
                        } catch (Exception ex) {}
                    }
                    break;
                }
                case "setidletimeout": {
                    if (parameters.length > 1 && LiteCommandEditorUtils.isInteger(parameters[1])) {
                        Bukkit.setIdleTimeout(Integer.valueOf(parameters[1]));
                        incorrect = false;
                    }
                    break;
                }
                case "setmotd": {
                    if (parameters.length > 1) {
                        try {
                            Field console = Bukkit.getServer().getClass().getDeclaredField("console");
                            console.setAccessible(true);
                            Object minecraftServer = console.get(Bukkit.getServer());
                            minecraftServer.getClass().getMethod("setMotd", String.class).invoke(minecraftServer, LiteCommandEditorUtils.rebuildText(parameters, 1));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        incorrect = false;
                    }
                    break;
                }
                case "setspawnradius": {
                    if (parameters.length > 1 && LiteCommandEditorUtils.isInteger(parameters[1])) {
                        Bukkit.setSpawnRadius(Integer.valueOf(parameters[1]));
                        incorrect = false;
                    }
                    break;
                }
                case "setwhitelist": {
                    if (parameters.length > 1) {
                        Bukkit.setWhitelist(Boolean.valueOf(parameters[1]));
                        incorrect = false;
                    }
                    break;
                }
                case "shutdown": {
                    Bukkit.shutdown();
                    incorrect = false;
                    break;
                }
                case "unbanip": {
                    if (parameters.length > 1) {
                        Bukkit.unbanIP(parameters[1]);
                        incorrect = false;
                    }
                    break;
                }
                case "unloadworld": {
                    if (parameters.length > 2) {
                        Bukkit.unloadWorld(LiteCommandEditorUtils.rebuildText(parameters, 2), Boolean.valueOf(parameters[1]));
                        incorrect = false;
                    }
                    break;
                }
                case "pluginmanager": {
                    if (parameters.length > 1) {
                        pluginManager(sender, placeholders, LiteCommandEditorUtils.rebuildText(parameters, 1));
                        incorrect = false;
                    }
                    break;
                }
                default: {
                    if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                        unknownFunction(parameters[0]);
                    }
                    incorrect = false;
                    break;
                }
            }
            if (incorrect) {
                if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                    incorrectParameters(parameters[0], String.join(":", Arrays.asList(parameters).stream().skip(1).toArray(String[]::new)));
                }
            }
        } catch (NoSuchMethodError t) {}
    }

    @Override
    public String toString() {
        return "[" + getIdentifier() + "]: Expression=" + expression;
    }
    
    private void pluginManager(CommandSender sender, Map<String, String> placeholders, String input) {
        String[] parameters = MessageUtil.splitStringBySymbol(MessageUtil.replacePlaceholders(sender, input, placeholders), ':');
        PluginManager manager = Bukkit.getPluginManager();
        try {
            boolean incorrect = true;
            switch (parameters[0].toLowerCase()) {
                case "clearplugins": {
                    manager.clearPlugins();
                    incorrect = false;
                    break;
                }
                case "disableplugin": {
                    if (parameters.length > 1) {
                        manager.disablePlugin(manager.getPlugin(parameters[1]));
                        incorrect = false;
                    }
                    break;
                }
                case "disableplugins": {
                    manager.disablePlugins();
                    incorrect = false;
                    break;
                }
                case "enableplugin": {
                    if (parameters.length > 1) {
                        manager.enablePlugin(manager.getPlugin(parameters[1]));
                        incorrect = false;
                    }
                    break;
                }
                case "loadplugin": {
                    if (parameters.length > 1) {
                        try {
                            manager.loadPlugin(new File(parameters[1]));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        incorrect = false;
                    }
                    break;
                }
                default: {
                    if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                        unknownFunction(parameters[0]);
                    }
                    incorrect = false;
                    break;
                }
            }
            if (incorrect) {
                if (!function.getConfig().getBoolean(function.getConfigPath() + ".No-Function-Reminder")) {
                    incorrectParameters(parameters[0], String.join(":", Arrays.asList(parameters).stream().skip(1).toArray(String[]::new)));
                }
            }
        } catch (NoSuchMethodError t) {}
    }
    
    private void incorrectParameters(String functionName, String parameters) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", function.getCommandConfig().getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{parameters}", parameters);
        placeholders.put("{functionName}", functionName);
        placeholders.put("{functionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Function-Messages.Functions-Type.Server"));
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Function-Messages.Incorrect-Parameters", placeholders);
    }
    
    private void unknownFunction(String functionName) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", function.getCommandConfig().getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{functionName}", functionName);
        placeholders.put("{functionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Function-Messages.Functions-Type.Server"));
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Function-Messages.Unknown-Function", placeholders);
    }
    
    public static ServerFunction build(CommandFunction function, Map map, String configPath) {
        if (map.get("Server-Function") != null) {
            return new ServerFunction(function, map.get("Server-Function").toString(), configPath);
        }
        return null;
    }
    
    public static List<ServerFunction> build(CommandFunction function, List<String> functions, String configPath) {
        return functions.stream().map(syntax -> new ServerFunction(function, syntax, configPath)).collect(Collectors.toList());
    }
}
