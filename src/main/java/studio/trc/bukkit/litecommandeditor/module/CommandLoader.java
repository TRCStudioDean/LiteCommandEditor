package studio.trc.bukkit.litecommandeditor.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import studio.trc.bukkit.litecommandeditor.Main;
import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationVersion;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;

import static studio.trc.bukkit.litecommandeditor.configuration.ConfigurationVersion.VersionType.*;

public class CommandLoader
{
    @Getter
    private static final Map<String, CommandConfiguration> cache = new HashMap<>();
    private static final List<CommandConfigurationOption> defaultCollection = Arrays.asList(
        new CommandConfigurationOption("Example.yml", "Example_V2.yml", V1_7_R1, V1_7_R2, V1_7_R3, V1_7_R4, V1_8_R1, V1_8_R2, V1_8_R3),
        new CommandConfigurationOption("Example.yml", "Example_V1.yml")
    );
    
    public static boolean loadCommandConfiguration(CommandSender sender, String fileName) {
        File folder = new File("plugins/LiteCommandEditor/Commands/");
        createDirectory(folder, true);
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{command}", fileName);
        try {
            File file = new File(folder, fileName);
            if (file == null) {
                placeholders.put("{file}", fileName);
                MessageUtil.sendCommandMessage(sender, "Load.Not-Exist", placeholders);
                return false;
            }
            placeholders.put("{file}", file.getName());
            if (!file.exists()) {
                MessageUtil.sendCommandMessage(sender, "Load.Not-Exist", placeholders);
                return false;
            }
            if (cache.containsKey(fileName)) {
                YamlConfiguration config = cache.get(fileName).getConfig();
                try (Reader reader = new InputStreamReader(new FileInputStream(file), LiteCommandEditorProperties.getMessage("Charset"))) {
                    config.load(reader);
                    if (config.get("Name") == null) {
                        MessageUtil.sendCommandMessage(sender, "Load.Missing-Required-Options", placeholders);
                        return false;
                    }
                } catch (IOException | InvalidConfigurationException ex) {
                    placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                    LiteCommandEditorProperties.sendOperationMessage("LoadingCommandConfigFailed", placeholders);
                    MessageUtil.sendCommandMessage(sender, "Load.Loading-Failed", placeholders);
                    return false;
                }
                cache.get(fileName).reloadConfig();
            } else {
                YamlConfiguration config = new YamlConfiguration();
                try (Reader reader = new InputStreamReader(new FileInputStream(file), LiteCommandEditorProperties.getMessage("Charset"))) {
                    config.load(reader);
                    if (config.get("Name") == null) {
                        MessageUtil.sendCommandMessage(sender, "Load.Missing-Required-Options", placeholders);
                        return false;
                    }
                } catch (IOException | InvalidConfigurationException ex) {
                    placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                    LiteCommandEditorProperties.sendOperationMessage("LoadingCommandConfigFailed", placeholders);
                    MessageUtil.sendCommandMessage(sender, "Load.Loading-Failed", placeholders);
                    return false;
                }
                cache.put(fileName, new CommandConfiguration(fileName, config));
            }
        } catch (Exception ex) {
            MessageUtil.sendCommandMessage(sender, "Load.Not-Exist", placeholders);
            return false;
        }
        return true;
    }
    
    private static List<String> getFilesInFolder(File folder, List<String> folderPath) {
        List<String> files = new ArrayList<>();
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                folderPath.add(file.getName() + "/");
                files.addAll(getFilesInFolder(file, folderPath));
            } else {
                StringBuilder builder = new StringBuilder();
                folderPath.stream().forEach(folderName -> builder.append(folderName));
                files.add(builder.toString() + file.getName());
            }
        }
        if (!folderPath.isEmpty()) folderPath.remove(folderPath.size() - 1);
        return files;
    }
    
    public static void loadCommandConfigurations(String folderPath, boolean clearCache, boolean createDirectory) {
        if (clearCache) cache.clear();
        File folder = new File(folderPath);
        createDirectory(folder, createDirectory);
        for (String fileName : getFilesInFolder(folder, new LinkedList<>())) {
            File file = new File(folder, fileName);
            try {
                if (!file.isDirectory() && file.getName().endsWith(".yml")) {
                    YamlConfiguration config = new YamlConfiguration();
                    try (Reader reader = new InputStreamReader(new FileInputStream(file), LiteCommandEditorProperties.getMessage("Charset"))) {
                        config.load(reader);
                        if (!config.getBoolean("Enabled")) {
                            continue;
                        }
                    } catch (IOException | InvalidConfigurationException ex) {
                        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                        placeholders.put("{file}", file.getName());
                        placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                        LiteCommandEditorProperties.sendOperationMessage("LoadingCommandConfigFailed", placeholders);
                        continue;
                    }
                    cache.put(fileName, new CommandConfiguration(fileName, config));
                }
            } catch (Exception ex) {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{file}", file.getName());
                placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                LiteCommandEditorProperties.sendOperationMessage("LoadingCommandConfigFailed", placeholders);
            }
        }
    }
    
    public static void createDirectory(File directory, boolean defaultFolder) {
        if (directory.isDirectory() && directory.exists()) return;
        directory.mkdirs();
        if (defaultFolder) {
            for (CommandConfigurationOption option : defaultCollection) {
                try {
                    try {
                        String nms = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                        if (option.versions.length != 0 && nms != null) {
                            if (!Arrays.stream(option.versions).anyMatch(version -> nms.equalsIgnoreCase(version.name()))) {
                                continue;
                            }
                        }
                    } catch (Throwable t) {
                        if (option.versions.length != 0) {
                            continue;
                        }
                    }
                    File configFile = new File(directory, option.name);
                    if (!configFile.exists()) {
                        configFile.createNewFile();
                        InputStream is = Main.class.getResourceAsStream("/Languages/" + MessageUtil.Language.getLocaleLanguage().getFolderName() + "/" + directory.getName() + "/" + option.fileName);
                        byte[] bytes = new byte[is.available()];
                        for (int len = 0; len != bytes.length; len += is.read(bytes, len, bytes.length - len));
                        try (OutputStream out = new FileOutputStream(configFile)) {
                            out.write(bytes);
                        }
                    }
                } catch (IOException ex) {
                    Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                    placeholders.put("{file}", option.name);
                    placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                    LiteCommandEditorProperties.sendOperationMessage("LoadingCommandConfigFailed", placeholders);
                }
            }
        }
    }
    
    public static class CommandConfigurationOption {
        protected final String name;
        protected final String fileName;
        protected final ConfigurationVersion.VersionType[] versions;

        public CommandConfigurationOption(String name, String fileName, ConfigurationVersion.VersionType... versions) {
            this.name = name;
            this.fileName = fileName;
            this.versions = versions;
        }
    }
}
