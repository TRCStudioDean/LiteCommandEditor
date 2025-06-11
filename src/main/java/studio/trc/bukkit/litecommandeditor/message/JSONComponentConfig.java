package studio.trc.bukkit.litecommandeditor.message;

import java.util.Arrays;

import lombok.Getter;

import org.bukkit.Bukkit;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationVersion;

public enum JSONComponentConfig 
{
    DEBUG_COMMAND("DebugCommand.yml"),
    
    ITEM_COLLECTION_COMMAND("ItemCollectionCommand.yml"),
    
    MAIN_COMMANDS("MainCommands.yml"),
    
    OTHERS_COMMANDS("OthersCommands.yml"),
    
    TOOLS_COMMAND("ToolsCommands.yml", JSONConfigurationVersion.TOOLS_COMMAND_V2, JSONConfigurationVersion.TOOLS_COMMAND_V1);
    
    @Getter
    private final String fileName;
    @Getter
    private final JSONConfigurationVersion[] versions;

    private JSONComponentConfig(String fileName, JSONConfigurationVersion... versions) {
        this.fileName = fileName;
        this.versions = versions;
    }
    
    public String getLocalFilePath() {
        if (versions.length == 0) {
            return fileName;
        } else {
            try {
                String nms = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                JSONConfigurationVersion specialVersion = Arrays.stream(versions).filter(version -> version.versions.length != 0 ? Arrays.stream(version.versions).anyMatch(type -> nms.equalsIgnoreCase(type.name())) : true).findFirst().get();
                return specialVersion.fileName;
            } catch (Exception ex) {
                JSONConfigurationVersion specialVersion = Arrays.stream(versions).filter(version -> version.versions.length == 0).findFirst().get();
                return specialVersion.fileName;
            }
        }
    }
    
    public static JSONComponentConfig getConfig(String fileName) {
        return Arrays.stream(values()).filter(jsonConfig -> jsonConfig.fileName.equalsIgnoreCase(fileName)).findFirst().orElse(null);
    }
    
    private enum JSONConfigurationVersion {
        TOOLS_COMMAND_V2("ToolsCommand_V2.yml", 
            ConfigurationVersion.VersionType.V1_7_R1, ConfigurationVersion.VersionType.V1_7_R2, ConfigurationVersion.VersionType.V1_7_R3, ConfigurationVersion.VersionType.V1_7_R4, 
            ConfigurationVersion.VersionType.V1_8_R1, ConfigurationVersion.VersionType.V1_8_R2, ConfigurationVersion.VersionType.V1_8_R3, 
            ConfigurationVersion.VersionType.V1_9_R1, ConfigurationVersion.VersionType.V1_9_R2,
            ConfigurationVersion.VersionType.V1_10_R1,
            ConfigurationVersion.VersionType.V1_11_R1, 
            ConfigurationVersion.VersionType.V1_12_R1,
            ConfigurationVersion.VersionType.V1_13_R1, ConfigurationVersion.VersionType.V1_13_R2,
            ConfigurationVersion.VersionType.V1_14_R1
        ),
        
        TOOLS_COMMAND_V1("ToolsCommand_V1.yml");
        
        private final String fileName;
        private final ConfigurationVersion.VersionType[] versions;
            
        private JSONConfigurationVersion(String fileName, ConfigurationVersion.VersionType... versions) {
            this.fileName = fileName;
            this.versions = versions;
        }
    }
}
