package studio.trc.bukkit.litecommandeditor.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.Map;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import studio.trc.bukkit.litecommandeditor.Main;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;

public enum ConfigurationType
{
    /**
     * Config.yml
     */
    CONFIG("Config.yml", "", new YamlConfiguration(), false, true),
    
    /**
     * Messages.yml
     */
    MESSAGES("Messages.yml", "", new YamlConfiguration(), true, true),
    
    /**
     * Alias.yml
     */
    ALIAS("Alias.yml", "", new YamlConfiguration(), false, true),
    
    /**
     * Items.yml
     */
    ITEMS("Items.yml", "", new YamlConfiguration(), true, false),
    
    /**
     * Permissions.yml
     */
    PERMISSIONS("Permissions.yml", "", new YamlConfiguration(), false, true);
    
    @Getter
    private final boolean universal;
    @Getter
    private final boolean defaultConfigExist;
    @Getter
    private final String fileName;
    @Getter
    private final String folder;
    @Getter
    private final YamlConfiguration config;
    @Getter
    private final ConfigurationVersion[] versions;

    private ConfigurationType(String fileName, String folder, YamlConfiguration config, boolean universal, boolean defaultConfigExist, ConfigurationVersion... versions) {
        this.fileName = fileName;
        this.folder = folder;
        this.universal = universal;
        this.defaultConfigExist = defaultConfigExist;
        this.config = config;
        this.versions = versions;
    }
    
    public void saveResource() {
        File dataFolder = new File("plugins/LiteCommandEditor/" + folder);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        try {
            File configFile = new File(dataFolder, fileName);
            if (!configFile.exists()) {
                configFile.createNewFile();
                if (!universal && defaultConfigExist) {
                    InputStream is = Main.class.getResourceAsStream("/Languages/" + (universal ? "Universal" : "") + MessageUtil.Language.getLocaleLanguage().getFolderName() + "/" + getLocalFilePath());
                    byte[] bytes = new byte[is.available()];
                    for (int len = 0; len != bytes.length; len += is.read(bytes, len, bytes.length - len));
                    try (OutputStream out = new FileOutputStream(configFile)) {
                        out.write(bytes);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void saveConfig() {
        try {
            config.save("plugins/LiteCommandEditor/" + folder + fileName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean reloadConfig() {
        try (InputStreamReader configFile = new InputStreamReader(new FileInputStream("plugins/LiteCommandEditor/" + folder + fileName), LiteCommandEditorProperties.getMessage("Charset"))) {
            config.load(configFile);
            if (universal && defaultConfigExist) {
                saveLanguageConfig(this);
            }
            return true;
        } catch (IOException | InvalidConfigurationException ex) {
            File oldFile = new File("plugins/LiteCommandEditor/" + folder + fileName + ".old");
            File file = new File("plugins/LiteCommandEditor/" + folder + fileName);
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{file}", folder + fileName);
            LiteCommandEditorProperties.sendOperationMessage("ConfigurationLoadingError", placeholders);
            if (oldFile.exists()) {
                oldFile.delete();
            }
            file.renameTo(oldFile);
            saveResource();
            try (InputStreamReader newConfig = new InputStreamReader(new FileInputStream(file), LiteCommandEditorProperties.getMessage("Charset"))) {
                config.load(newConfig);
                LiteCommandEditorProperties.sendOperationMessage("ConfigurationRepair", MessageUtil.getDefaultPlaceholders());
            } catch (IOException | InvalidConfigurationException ex1) {
                ex1.printStackTrace();
            }
        }
        return false;
    }
    
    public String getLocalFilePath() {
        if (versions.length == 0) {
            return folder + fileName;
        } else {
            try {
                String nms = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                ConfigurationVersion specialVersion = Arrays.stream(versions).filter(version -> version.getVersions().length != 0 ? Arrays.stream(version.getVersions()).anyMatch(type -> nms.equalsIgnoreCase(type.name())) : true).findFirst().get();
                return specialVersion.getFolder() + specialVersion.getFileName();
            } catch (Exception ex) {
                ConfigurationVersion specialVersion = Arrays.stream(versions).filter(version -> version.getVersions().length == 0).findFirst().get();
                return specialVersion.getFolder() + specialVersion.getFileName();
            }
        }
    }
    
    public RobustConfiguration getRobustConfig() {
        return ConfigurationUtil.getConfig(this);
    }
    
    public static void saveLanguageConfig(ConfigurationType type) {
        String language = MessageUtil.getLanguage();
        if (!type.getRobustConfig().getConfig().contains(language)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/Languages/Universal/" + type.getLocalFilePath()), LiteCommandEditorProperties.getMessage("Charset")))) {
                String line;
                StringBuilder source = new StringBuilder();
                try (BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream("plugins/LiteCommandEditor/" + type.getFolder() + type.getFileName()), LiteCommandEditorProperties.getMessage("Charset")))) {
                    while ((line = input.readLine()) != null) {
                        source.append(line);
                        source.append('\n');
                    }
                }
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("plugins/LiteCommandEditor/" + type.getFolder() + type.getFileName()), LiteCommandEditorProperties.getMessage("Charset")))) {
                    writer.append(source.toString());
                    boolean keepWriting = false;
                    while ((line = reader.readLine()) != null) {
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
                try (Reader reloader = new InputStreamReader(new FileInputStream("plugins/LiteCommandEditor/" + type.getFolder() + type.getFileName()), LiteCommandEditorProperties.getMessage("Charset"))) {
                    type.config.load(reloader);
                } catch (IOException | InvalidConfigurationException ex) {
                    ex.printStackTrace();
                    Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                    placeholders.put("{file}", type.getFolder() + type.getFileName());
                    LiteCommandEditorProperties.sendOperationMessage("ConfigurationLoadingError", placeholders);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{file}", type.getFolder() + type.getFileName());
                LiteCommandEditorProperties.sendOperationMessage("ConfigurationLoadingError", placeholders);
            }
        }
    }
    
    public static ConfigurationType getType(String filePath) {
        String[] path = filePath.split("/", -1);
        if (path.length > 1) {
            String fileName = path[path.length - 1];
            StringBuilder folder = new StringBuilder();
            for (int i = 0;i < path.length - 1;i++) {
                folder.append(path[i]);
                folder.append("/");
            }
            return Arrays.stream(ConfigurationType.values()).filter(type -> type.getFolder().equalsIgnoreCase(folder.toString()) && type.getFileName().equalsIgnoreCase(fileName)).findFirst().orElse(null);
        } else {
            String fileName = filePath;
            return Arrays.stream(ConfigurationType.values()).filter(type -> type.getFileName().equalsIgnoreCase(fileName)).findFirst().orElse(null);
        }
    }
}
