package studio.trc.bukkit.litecommandeditor.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.configuration.file.YamlConfiguration;

import studio.trc.bukkit.litecommandeditor.module.command.CommandFunction;
import studio.trc.bukkit.litecommandeditor.module.tab.TabFunction;
import studio.trc.bukkit.litecommandeditor.module.tool.ItemCollection;

public class CommandConfiguration
{
    @Getter
    private final String fileName;
    @Getter
    private final YamlConfiguration config;
    @Getter
    @Setter
    private CommandExecutor executor = null;
    
    /*
     * Basic settings.
     */
    @Getter
    @Setter
    private boolean enabled;
    @Getter
    @Setter
    private String prefix;
    @Getter
    @Setter
    private String commandName;
    @Getter
    @Setter
    private String permission;
    @Getter
    @Setter
    private String usage;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private List<String> aliases;
    
    /*
     * Special settings.
     */
    @Getter
    @Setter
    private List<CommandFunction> commandFunctions;
    @Getter
    @Setter
    private List<TabFunction> tabFunctions;
    
    /*
     * Other settings.
     */
    @Getter
    @Setter
    private ItemCollection itemCollection;
    
    /*
     * Cache
     */
    @Getter
    private final Map<String, String> cachePlaceholders = new HashMap<>();
    
    public CommandConfiguration(String fileName, YamlConfiguration config) {
        this.fileName = fileName;
        this.config = config;
        enabled = config.getBoolean("Enabled");
        prefix = config.getString("Prefix", "litecommandeditor");
        commandName = config.getString("Name");
        permission = config.getString("Permission");
        usage = config.getString("Usage");
        description = config.getString("Description");
        aliases = config.getStringList("Aliases");
        itemCollection = ItemCollection.build(CommandConfiguration.this, fileName, config, "Item-Collection");
        commandFunctions = CommandFunction.build(CommandConfiguration.this, fileName, config, "Command-Executor");
        tabFunctions = TabFunction.build(CommandConfiguration.this, fileName, config, "Tab-Completer");
    }
    
    public void reloadConfig() {
        enabled = config.getBoolean("Enabled");
        prefix = config.getString("Prefix", "litecommandeditor");
        commandName = config.getString("Name");
        permission = config.getString("Permission");
        usage = config.getString("Usage");
        description = config.getString("Description");
        aliases = config.getStringList("Aliases");
        itemCollection = ItemCollection.build(CommandConfiguration.this, fileName, config, "Item-Collection");
        commandFunctions = CommandFunction.build(CommandConfiguration.this, fileName, config, "Command-Executor");
        tabFunctions = TabFunction.build(CommandConfiguration.this, fileName, config, "Tab-Completer");
    }
    
    public LastCommandConfiguration cloneCommandInfo() {
        return new LastCommandConfiguration(prefix, commandName, new ArrayList<>(aliases));
    }
    
    public class LastCommandConfiguration {
        @Getter
        private final String prefix;
        @Getter
        private final String commandName;
        @Getter
        private final List<String> aliases;

        public LastCommandConfiguration(String prefix, String commandName, List<String> aliases) {
            this.prefix = prefix;
            this.commandName = commandName;
            this.aliases = aliases;
        }

        public boolean equals(LastCommandConfiguration lastConfig) {
            return prefix.equals(lastConfig.prefix) && commandName.equals(lastConfig.commandName) && aliases.equals(lastConfig.aliases);
        }
    }
}
