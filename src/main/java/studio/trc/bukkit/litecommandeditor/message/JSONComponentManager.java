package studio.trc.bukkit.litecommandeditor.message;

import java.io.BufferedReader;
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
import java.util.Map;

import lombok.Getter;

import net.md_5.bungee.api.chat.BaseComponent;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import studio.trc.bukkit.litecommandeditor.Main;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.CommandManager;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;

public class JSONComponentManager
{
    @Getter
    private static final Map<String, YamlConfiguration> loadedJSONComponentConfigs = new HashMap<>();
    @Getter
    private static final PlaceholderTree<JSONComponent> defaultJSONComponents = new PlaceholderTree();
    @Getter
    private static final PlaceholderTree<BaseComponent> defaultComponents = new PlaceholderTree();
    
    static {
        defaultJSONComponents.setStartChar('%');
        defaultJSONComponents.setEndChar('%');
        defaultComponents.setStartChar('%');
        defaultComponents.setEndChar('%');
    }
    
    public static Map<String, String> reloadJSONComponents() {
        defaultJSONComponents.clear();
        defaultComponents.clear();
        loadedJSONComponentConfigs.clear();
        File file = new File("plugins/LiteCommandEditor/JSONComponents/");
        createDirectory(file);
        for (File subFile : file.listFiles()) {
            if (subFile.getName().endsWith(".yml")) {
                YamlConfiguration yaml = new YamlConfiguration();
                loadedJSONComponentConfigs.put(subFile.getName(), yaml);
                try (Reader reader = new InputStreamReader(new FileInputStream(subFile), LiteCommandEditorProperties.getMessage("Charset"))) {
                    yaml.load(reader);
                } catch (IOException | InvalidConfigurationException ex) {
                    Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                    placeholders.put("{file}", subFile.getName());
                    placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                    LiteCommandEditorProperties.sendOperationMessage("LoadingJSONComponentConfigFailed", placeholders);
                }
            }
        }
        String language = MessageUtil.getLanguage();
        saveComponents(language);
        loadedJSONComponentConfigs.keySet().stream().filter(fileName -> loadedJSONComponentConfigs.get(fileName).contains(language)).forEach(fileName -> {
            YamlConfiguration config = loadedJSONComponentConfigs.get(fileName);
            config.getConfigurationSection(language).getKeys(false).stream().forEach(path -> {
                try {
                    String placeholder = toPlaceholder(config.getString(language + "." + path + ".Placeholder"));
                    JSONComponent component = new JSONComponent(fileName, config, language + "." + path);
                    defaultJSONComponents.addPlaceholder(placeholder, component);
                } catch (Exception ex) {
                    Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                    placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                    placeholders.put("{component}", path);
                    placeholders.put("{file}", fileName);
                    LiteCommandEditorProperties.sendOperationMessage("LoadingJSONComponentFailed", placeholders);
                    ex.printStackTrace();
                }
            });
        });
        CommandManager.getRegisteredCommands().stream()
            .filter(command -> command.getConfig().get("JSON-Components") != null && !command.getConfig().getConfigurationSection("JSON-Components").getKeys(false).isEmpty())
            .forEach(command -> {
            YamlConfiguration config = command.getConfig();
            config.getConfigurationSection("JSON-Components").getKeys(false).stream().forEach(path -> {
                try {
                    String placeholder = toPlaceholder(config.getString("JSON-Components." + path + ".Placeholder"));
                    JSONComponent component = new JSONComponent(command.getFileName(), config, "JSON-Components." + path);
                    defaultJSONComponents.addPlaceholder(placeholder, component);
                } catch (Exception ex) {
                    Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                    placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                    placeholders.put("{component}", path);
                    placeholders.put("{file}", command.getFileName());
                    LiteCommandEditorProperties.sendOperationMessage("LoadingJSONComponentFailed", placeholders);
                    ex.printStackTrace();
                }
            });
        });
        defaultJSONComponents.getAllPlaceholders().entrySet().stream().forEach(entry -> defaultComponents.addPlaceholder(entry.getKey(), entry.getValue().getComponent()));
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{components}", String.valueOf(defaultJSONComponents.size()));
        return placeholders;
    }
    
    public static void reloadJSONComponents(CommandConfiguration command) {
        if (command.getConfig().get("JSON-Components") != null && !command.getConfig().getConfigurationSection("JSON-Components").getKeys(false).isEmpty()) {
            YamlConfiguration config = command.getConfig();
            config.getConfigurationSection("JSON-Components").getKeys(false).stream().forEach(path -> {
                try {
                    String placeholder = toPlaceholder(config.getString("JSON-Components." + path + ".Placeholder"));
                    JSONComponent component = new JSONComponent(command.getFileName(), config, "JSON-Components." + path);
                    defaultJSONComponents.addPlaceholder(placeholder, component);
                } catch (Exception ex) {
                    Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                    placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                    placeholders.put("{component}", path);
                    placeholders.put("{file}", command.getFileName());
                    LiteCommandEditorProperties.sendOperationMessage("LoadingJSONComponentFailed", placeholders);
                    ex.printStackTrace();
                }
            });
        }
    }
    
    public static void createDirectory(File directory) {
        if (directory.isDirectory() && directory.exists()) return;
        directory.mkdirs();
        Arrays.stream(JSONComponentConfig.values()).forEach(config -> {
            try {
                File configFile = new File(directory, config.getFileName());
                if (!configFile.exists()) {
                    configFile.createNewFile();
                }
            } catch (IOException ex) {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{file}", config.getFileName());
                placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                LiteCommandEditorProperties.sendOperationMessage("LoadingJSONComponentConfigFailed", placeholders);
            }
        });
    }
    
    public static void saveComponents(String language) {
        loadedJSONComponentConfigs.keySet().stream()
        .filter(fileName -> JSONComponentConfig.getConfig(fileName) != null)
        .forEach(fileName -> {
            JSONComponentConfig jsonConfig = JSONComponentConfig.getConfig(fileName);
            YamlConfiguration config = loadedJSONComponentConfigs.get(fileName);
            String localFileName = jsonConfig.getLocalFilePath();
            if (!config.contains(language) && Main.class.getResource("/Languages/Universal/JSONComponents/" + localFileName) != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/Languages/Universal/JSONComponents/" + localFileName), LiteCommandEditorProperties.getMessage("Charset")))) {
                    String line;
                    //Copy the source and re-append it to the Writer (otherwise the original content in the file will be overwritten)
                    StringBuilder source = new StringBuilder();
                    try (BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream("plugins/LiteCommandEditor/JSONComponents/" + fileName), LiteCommandEditorProperties.getMessage("Charset")))) {
                        while ((line = input.readLine()) != null) {
                            source.append(line);
                            source.append('\n');
                        }
                    }
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("plugins/LiteCommandEditor/JSONComponents/" + fileName), LiteCommandEditorProperties.getMessage("Charset")))) {
                        writer.append(source.toString());
                        boolean keepWriting = false;
                        while ((line = reader.readLine()) != null) {
                            //Determine language blocks.
                            if (line.startsWith(language + ":")) {
                                keepWriting = true;
                            }
                            if (!line.startsWith("    ") && !line.startsWith(language)) {
                                keepWriting = false;
                            }
                            if (keepWriting) {
                                writer.append(line);
                                writer.append('\n');
                            }
                        }
                    }
                    try (Reader reloader = new InputStreamReader(new FileInputStream("plugins/LiteCommandEditor/JSONComponents/" + fileName), LiteCommandEditorProperties.getMessage("Charset"))) {
                        config.load(reloader);
                    } catch (IOException | InvalidConfigurationException ex) {
                        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                        placeholders.put("{file}", fileName);
                        placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                        LiteCommandEditorProperties.sendOperationMessage("LoadingJSONComponentConfigFailed", placeholders);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    
    private static String toPlaceholder(String text) {
        return text.startsWith("%") && text.endsWith("%") ? text : "%" + text + "%";
    }
}
