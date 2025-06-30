package studio.trc.bukkit.litecommandeditor.module.function;

import com.pa_project.lib.json.JSONDataStructure;
import com.pa_project.lib.json.JSONList;
import com.pa_project.lib.json.JSONObject;
import com.pa_project.lib.json.parser.JSONParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.message.placeholder.ConfiguratorPlaceholderRequest;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunction;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunctionTask;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class Configurator
    implements CommandFunctionTask
{
    @Getter
    private static final Map<String, JSONObject> tables = new HashMap<>();
    
    @Getter
    private final String expression;
    @Getter
    private final String configPath;
    @Getter
    private final CommandFunction function;
    @Getter
    private final String identifier = "Configurator";

    public Configurator(CommandFunction function, String expression, String configPath) {
        this.expression = expression;
        this.configPath = configPath;
        this.function = function;
    }
    
    @Override
    public void executeTask(CommandSender sender, Map<String, String> placeholders) {
        String[] parameters = MessageUtil.splitStringBySymbol(MessageUtil.replacePlaceholders(sender, expression, placeholders), ':');
        boolean incorrect = true;
        switch (parameters[0].toLowerCase()) {
            case "use": {
                if (parameters.length > 2) {
                    JSONObject json = getTable(parameters[1]);
                    if (json != null) {
                        switch (parameters[2].toLowerCase()) {
                            case "set": { //Configurator:use:[Name]:set:[Path]:[Value]
                                if (parameters.length > 4) {
                                    json.set(parameters[3], LiteCommandEditorUtils.toValue(parameters[4]));
                                    incorrect = false;
                                }
                                break;
                            }
                            case "remove": {
                                if (parameters.length > 3) { //Configurator:use:[Name]:remove:[Path]
                                    json.set(parameters[3], null);
                                    incorrect = false;
                                }
                                break;
                            }
                            case "listadd": { //Configurator:use:[Name]:listadd:[Path]:[Value]
                                if (parameters.length > 4) {
                                    if (json.get(parameters[3]) instanceof List) {
                                        json.getRawList(parameters[3]).add(LiteCommandEditorUtils.toValue(parameters[4]));
                                    } else {
                                        JSONList array = JSONList.create();
                                        array.add(LiteCommandEditorUtils.toValue(parameters[4]));
                                        json.set(parameters[3], array);
                                    }
                                    incorrect = false;
                                }
                                break;
                            }
                            case "listset": { //Configurator:use:[Name]:listset:[Path]:[Index]:[Value]
                                if (parameters.length > 5 && LiteCommandEditorUtils.isInteger(parameters[4])) {
                                    if (json.get(parameters[3]) instanceof List) {
                                        List list = json.getRawList(parameters[3]);
                                        if (list.size() > Integer.valueOf(parameters[4]) - 1 && Integer.valueOf(parameters[4]) > 0) {
                                            list.set(Integer.valueOf(parameters[4]) - 1, LiteCommandEditorUtils.toValue(parameters[5]));
                                        } else {
                                            list.add(LiteCommandEditorUtils.toValue(parameters[5]));
                                        }
                                    } else {
                                        JSONList array = JSONList.create();
                                        array.add(LiteCommandEditorUtils.toValue(parameters[5]));
                                        json.set(parameters[3], array);
                                    }
                                    incorrect = false;
                                }
                                break;
                            }
                            case "listremove": { //Configurator:use:[Name]:listset:[Path]:[Index]
                                if (parameters.length > 3 && json.get(parameters[3]) instanceof List && LiteCommandEditorUtils.isInteger(parameters[4])) {
                                    List list = json.getRawList(parameters[3]);
                                    if (list.size() > Integer.valueOf(parameters[4]) - 1 && Integer.valueOf(parameters[4]) > 0) {
                                        list.remove(Integer.valueOf(parameters[4]) - 1);
                                    }
                                    incorrect = false;
                                }
                                break;
                            }
                            case "listclear": { //Configurator:use:[Name]:listset:[Path]
                                if (parameters.length > 3 && json.get(parameters[3]) instanceof List) {
                                    json.getRawList(parameters[3]).clear();
                                    incorrect = false;
                                }
                                break;
                            }
                        }
                    } else {
                        unknownTable(parameters[1]);
                        incorrect = false;
                    }
                }
                break;
            }
            case "create": { //Configurator:create:[Name]
                if (parameters.length >= 2 && getTable(parameters[1]) == null) {
                    tables.put(parameters[1], JSONObject.create(JSONDataStructure.LINKED));
                    incorrect = false;
                }
                break;
            }
            case "delete": { //Configurator:delete:[Name]
                if (parameters.length >= 2 && getTable(parameters[1]) != null) {
                    tables.remove(parameters[1]);
                    incorrect = false;
                }
                break;
            }
            case "load": { //Configurator:load:[Type]:[Name]:[Value]
                if (parameters.length >= 3) {
                    switch (parameters[1].toLowerCase()) { 
                        case "text": {
                            String text = String.join(":", Arrays.stream(parameters).skip(3).toArray(String[]::new));
                            try {
                                tables.put(parameters[2], JSONObject.toJSONObject(text, JSONDataStructure.LINKED));
                            } catch (JSONParseException ex) {
                                incorrectFormat_Text(text);
                            }
                            incorrect = false;
                            break;
                        }
                        case "file": {
                            String fileName = String.join(":", Arrays.stream(parameters).skip(3).toArray(String[]::new));
                            File file = new File(fileName);
                            if (file.exists()) {
                                try {
                                    tables.put(parameters[2], JSONObject.toJSONObject(file, JSONDataStructure.LINKED));
                                } catch (JSONParseException ex) {
                                    try (Reader reader = new InputStreamReader(new FileInputStream(file), LiteCommandEditorProperties.getMessage("Charset"))) {
                                        tables.put(parameters[2], JSONObject.toJSONObject((Map<?,?>) new Yaml().load(YamlConfiguration.loadConfiguration(reader).saveToString()), true, JSONDataStructure.LINKED));
                                    } catch (IOException | JSONParseException ex1) {
                                        incorrectFormat_File(fileName);
                                    }
                                }
                            } else {
                                unknownFile(fileName);
                            }
                            incorrect = false;
                            break;
                        }
                    }
                }
                break;
            }
            case "save": { //Configurator:save:[Name]:[Type]:[File]
                if (parameters.length > 3) {
                    JSONObject json = getTable(parameters[1]);
                    if (json != null) {
                        switch (parameters[2].toLowerCase()) {
                            case "json": {
                                try {
                                    File file = LiteCommandEditorUtils.createFile(String.join(":", Arrays.stream(parameters).skip(3).toArray(String[]::new)));
                                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), LiteCommandEditorProperties.getMessage("Charset")))) {
                                        writer.write(json.toVisualizedJSONString());
                                    }
                                    incorrect = false;
                                } catch (IOException ex) {}
                                break;
                            }
                            case "yaml": {
                                YamlConfiguration yaml = new YamlConfiguration();
                                json.entrySet().stream().forEach(entry -> {
                                    if (entry.getValue() instanceof Map) {
                                        yaml.createSection(entry.getKey().toString(), (Map<?, ?>) entry.getValue());
                                    } else {
                                        yaml.set(entry.getKey().toString(), entry.getValue());
                                    }
                                });
                                try {
                                    File file = LiteCommandEditorUtils.createFile(String.join(":", Arrays.stream(parameters).skip(3).toArray(String[]::new)));
                                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), LiteCommandEditorProperties.getMessage("Charset")))) {
                                        writer.write(yaml.saveToString());
                                    }
                                    incorrect = false;
                                } catch (IOException ex) {}
                                break;
                            }
                        }
                    } else {
                        unknownTable(parameters[1]);
                        incorrect = false;
                    }
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
    }
    
    private void incorrectParameters(String functionName, String parameters) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", function.getCommandConfig().getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{parameters}", parameters);
        placeholders.put("{functionName}", functionName);
        placeholders.put("{functionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Function-Messages.Functions-Type.Configurator"));
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Function-Messages.Incorrect-Parameters", placeholders);
    }
    
    private void incorrectFormat_Text(String text) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", function.getCommandConfig().getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{text}", text.length() > 1024 ? text.substring(0, 1023) + "..." : text);
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Function-Messages.Incorrect-Format:Text", placeholders);
    }
    
    private void incorrectFormat_File(String file) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", function.getCommandConfig().getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{file}", file);
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Function-Messages.Incorrect-Format:File", placeholders);
    }
    
    private void unknownTable(String tableName) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", function.getCommandConfig().getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{tableName}", tableName);
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Function-Messages.Unknown-Table-Name", placeholders);
    }
    
    private void unknownFile(String fileName) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", function.getCommandConfig().getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{fileName}", fileName);
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Function-Messages.Unknown-File-Name", placeholders);
    }
    
    private void unknownFunction(String functionName) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{fileName}", function.getCommandConfig().getFileName());
        placeholders.put("{configPath}", configPath);
        placeholders.put("{functionName}", functionName);
        placeholders.put("{functionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Function-Messages.Functions-Type.Configurator"));
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Function-Messages.Unknown-Function", placeholders);
    }
    
    public static void initialize() {
        tables.clear();
        ConfiguratorPlaceholderRequest.getCachePlaceholders().clear();
    }
    
    public static JSONObject getTable(String name) {
        String tableName = tables.entrySet().stream().map(table -> table.getKey()).filter(table -> table.equalsIgnoreCase(name)).findFirst().orElse(null);
        return tableName != null ? tables.get(tableName) : null;
    }
    
    public static Configurator build(CommandFunction function, Map map, String configPath) {
        if (map.get("Configurator") != null) {
            return new Configurator(function, map.get("Configurator").toString(), configPath);
        }
        return null;
    }
    
    public static List<Configurator> build(CommandFunction function, List<String> functions, String configPath) {
        return functions.stream().map(syntax -> new Configurator(function, syntax, configPath)).collect(Collectors.toList());
    }
}
