package studio.trc.bukkit.litecommandeditor.module;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorCommandAlias;
import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.event.CommandConfigurationLoadEvent;
import studio.trc.bukkit.litecommandeditor.event.CommandConfigurationUnloadEvent;
import studio.trc.bukkit.litecommandeditor.event.CommandConfigurationsLoadedEvent;
import studio.trc.bukkit.litecommandeditor.event.CommandDisableEvent;
import studio.trc.bukkit.litecommandeditor.event.CommandConfigurationsRegisteredEvent;
import studio.trc.bukkit.litecommandeditor.message.JSONComponentManager;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.tab.TabRecipe;
import studio.trc.bukkit.litecommandeditor.util.PluginControl;

public class CommandManager
{
    @Getter
    private static final List<CommandConfiguration> registeredCommands = new ArrayList<>();
    @Getter
    private static final Set<String> registeredAliasCommands = new HashSet();
    private static Set<String> registeredCommandsName = null;
    
    public static void reloadCommandConfigurations() {
        CommandCondition.resetCommandConditions();
        TabRecipe.resetTabRecipes();
        CommandLoader.loadCommandConfigurations("plugins/LiteCommandEditor/Commands/", true, true);
        Bukkit.getPluginManager().callEvent(new CommandConfigurationsLoadedEvent(CommandLoader.getCache()));
        registerAllCustomCommands();
        PluginControl.runBukkitTask(() -> disableCommands(), 1);
    }
    
    public static void disableCommands() {
        if (!ConfigurationType.CONFIG.getRobustConfig().getStringList("Disabled-Commands").isEmpty()) {
            if (ConfigurationType.CONFIG.getRobustConfig().getStringList("Disabled-Commands").stream().map(command -> unregisterCommand(null, command)).collect(Collectors.toList()).stream().anyMatch(result -> result)) {
                syncCommands();
            }
        }
    }
    
    public static boolean unregisterCustomCommand(CommandSender sender, String fileName) {
        try {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{command}", fileName);
            CommandMap map = getServerCommandMap();
            Field field = SimpleCommandMap.class.getDeclaredField("knownCommands");
            field.setAccessible(true);
            Map<String, Command> knownCommands = (Map<String, Command>) field.get(map);
            CommandConfiguration commandConfig = registeredCommands.stream().filter(config -> knownCommands.get(config.getCommandName()) != null && config.getFileName().equalsIgnoreCase(fileName)).findFirst().orElse(null);
            if (commandConfig == null) {
                MessageUtil.sendCommandMessage(sender, "Unload.Not-Exist", placeholders);
                field.setAccessible(false);
                return false;
            }
            CommandConfigurationUnloadEvent event = new CommandConfigurationUnloadEvent(commandConfig);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                field.setAccessible(false);
                return false;
            }
            Command command = knownCommands.get(commandConfig.getCommandName());
            if (command != null) {
                command.unregister(map);
            }
            knownCommands.remove(commandConfig.getCommandName());
            knownCommands.remove(commandConfig.getPrefix() + ":" + commandConfig.getCommandName());
            if (registeredCommandsName != null) {
                registeredCommandsName.remove(commandConfig.getPrefix() + ":" + commandConfig.getCommandName());
            }
            if (!commandConfig.getAliases().isEmpty()) {
                commandConfig.getAliases().stream().forEach(alias -> {
                    Command aliasCommand = knownCommands.get(alias);
                    if (aliasCommand != null) {
                        aliasCommand.unregister(map);
                    }
                    knownCommands.remove(alias);
                    knownCommands.remove(commandConfig.getPrefix() + ":" + alias);
                    if (registeredCommandsName != null) {
                        registeredCommandsName.remove(commandConfig.getPrefix() + ":" + alias);
                    }
                });
            }
            CommandLoader.getCache().remove(commandConfig.getFileName());
            field.setAccessible(false);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public static boolean unregisterCommand(CommandSender sender, String commandInfo) {
        try {
            String[] details = commandInfo.split(":", 2);
            CommandMap map = getServerCommandMap();
            Field field = SimpleCommandMap.class.getDeclaredField("knownCommands");
            field.setAccessible(true);
            Map<String, Command> knownCommands = (Map<String, Command>) field.get(map);
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            if (details.length == 2) {
                placeholders.put("{command}", details[0] + ":" + details[1]);
                if (knownCommands.containsKey(commandInfo)) {
                    CommandDisableEvent event = new CommandDisableEvent(details[0], details[1]);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        field.setAccessible(false);
                        return false;
                    }
                    Command command = knownCommands.get(details[1]);
                    command.unregister(map);
                    knownCommands.remove(commandInfo);
                    knownCommands.remove(details[1]);
                    field.setAccessible(false);
                    return true;
                } else {
                    MessageUtil.sendCommandMessage(sender, "Delete.Not-Exist", placeholders);
                    field.setAccessible(false);
                    return false;
                }
            } else {
                Command command = knownCommands.get(commandInfo);
                placeholders.put("{command}", commandInfo);
                if (command != null) {
                    if (!knownCommands.containsKey(commandInfo)) {
                        field.setAccessible(false);
                        return false;
                    }
                    CommandDisableEvent event = new CommandDisableEvent(null, commandInfo);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        field.setAccessible(false);
                        return false;
                    }
                    command.unregister(map);
                    knownCommands.remove(commandInfo);
                    new HashSet<>(knownCommands.keySet()).stream().filter(name -> name.contains(":") && name.split(":")[1].equalsIgnoreCase(commandInfo)).forEach(name -> knownCommands.remove(name));
                    field.setAccessible(false);
                    return true;
                } else {
                    MessageUtil.sendCommandMessage(sender, "Delete.Not-Exist", placeholders);
                    field.setAccessible(false);
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public static boolean registerCustomCommand(CommandSender sender, String fileName, CommandConfiguration.LastCommandConfiguration lastConfig) {
        try {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{command}", fileName);
            CommandMap map = getServerCommandMap();
            Field field = SimpleCommandMap.class.getDeclaredField("knownCommands");
            field.setAccessible(true);
            Map<String, Command> knownCommands = (Map<String, Command>) field.get(map);
            CommandConfiguration commandConfig = CommandLoader.getCache().get(fileName);
            if (commandConfig == null) {
                MessageUtil.sendCommandMessage(sender, "Load.Not-Exist", placeholders);
                field.setAccessible(false);
                return false;
            }
            CommandConfigurationLoadEvent event = new CommandConfigurationLoadEvent(commandConfig);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                field.setAccessible(false);
                return false;
            }
            //Delete all registered commands from last command configuration
            if (lastConfig != null && !lastConfig.getCommandName().equalsIgnoreCase(commandConfig.getCommandName())) {
                //Main command
                Command command = knownCommands.get(lastConfig.getCommandName());
                if (command != null) {
                    command.unregister(map);
                }
                knownCommands.remove(lastConfig.getCommandName());
                knownCommands.remove(lastConfig.getPrefix() + ":" + lastConfig.getCommandName());
                if (registeredCommandsName != null) {
                    registeredCommandsName.remove(lastConfig.getPrefix() + ":" + lastConfig.getCommandName());
                }
            }
            if (lastConfig != null && !lastConfig.getAliases().isEmpty() && !lastConfig.getAliases().stream().allMatch(alias -> commandConfig.getAliases().contains(alias))) {
                //Aliases
                lastConfig.getAliases().stream().forEach(alias -> {
                    Command aliasCommand = knownCommands.get(alias);
                    if (aliasCommand != null) {
                        aliasCommand.unregister(map);
                    }
                    knownCommands.remove(alias);
                    knownCommands.remove(lastConfig.getPrefix() + ":" + alias);
                    if (registeredCommandsName != null) {
                        registeredCommandsName.remove(lastConfig.getPrefix() + ":" + alias);
                    }
                });
            }
            CommandExecutor executor;
            if (commandConfig.getExecutor() != null) {
                executor = commandConfig.getExecutor();
            } else {
                //Register new command executor
                executor = new CommandExecutor(commandConfig);
            }
            //Check for changes in main command name in the command list
            boolean reset = false;
            if (!knownCommands.containsKey(commandConfig.getCommandName())) {
                reset = true;
                map.register(commandConfig.getCommandName(), commandConfig.getPrefix(), executor);
                if (registeredCommandsName == null) {
                    registeredCommandsName = new HashSet();
                }
                registeredCommandsName.add(commandConfig.getPrefix() + ":" + commandConfig.getCommandName());
            }
            //Check for changes in command aliases in the command list
            for (String alias : commandConfig.getAliases()) {
                //If command aliases is increased
                if (!knownCommands.containsKey(alias)) {
                    reset = true;
                    map.register(alias, commandConfig.getPrefix(), executor);
                    if (registeredCommandsName == null) {
                        registeredCommandsName = new HashSet();
                    }
                    registeredCommandsName.add(commandConfig.getPrefix() + ":" + alias);
                }
            }
            if (lastConfig != null && !lastConfig.getAliases().isEmpty()) {
                //If command aliases is reduced
                for (String alias : lastConfig.getAliases()) {
                    if (!commandConfig.getAliases().contains(alias)) {
                        reset = true;
                    }
                }
            }
            //Set command basic properties
            if (commandConfig.getDescription() != null) {
                executor.setDescription(commandConfig.getDescription());
            }
            if (commandConfig.getUsage() != null) {
                executor.setUsage(commandConfig.getUsage());
            }
            if (commandConfig.getPermission() != null) {
                executor.setPermission(commandConfig.getPermission());
            }
            JSONComponentManager.reloadJSONComponents(commandConfig);
            //If the command list changes, then syncCommands()
            if (reset) {
                syncCommands();
            }
            field.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static void registerAllCustomCommands() {
        try {
            CommandMap map = getServerCommandMap();
            Field field = SimpleCommandMap.class.getDeclaredField("knownCommands");
            field.setAccessible(true);
            //Uninstall the existing custom commands
            Map<String, Command> knownCommands = (Map<String, Command>) field.get(map);
            registeredCommands.stream().forEach(commandConfig -> {
                //Command
                Command command = knownCommands.get(commandConfig.getCommandName());
                if (command != null) {
                    command.unregister(map);
                }
                knownCommands.remove(commandConfig.getCommandName());
                knownCommands.remove(commandConfig.getPrefix() + ":" + commandConfig.getCommandName());
                //Aliases
                if (!commandConfig.getAliases().isEmpty()) {
                    commandConfig.getAliases().stream().forEach(alias -> {
                        Command aliasCommand = knownCommands.get(alias);
                        if (aliasCommand != null) {
                            aliasCommand.unregister(map);
                        }
                        knownCommands.remove(alias);
                        knownCommands.remove(commandConfig.getPrefix() + ":" + alias);
                    });
                }
            });
            registeredAliasCommands.stream().forEach(alias -> {
                Command command = knownCommands.get(alias);
                if (command != null) {
                    command.unregister(map);
                }
                knownCommands.remove(alias);
            });
            field.setAccessible(false);
            registeredCommands.clear();
            //Register custom commands
            CommandLoader.getCache().values().stream().forEach(commandConfig -> {
                Command executor = new CommandExecutor(commandConfig);
                map.register(commandConfig.getCommandName(), commandConfig.getPrefix(), executor);
                registeredCommands.add(commandConfig);
                if (!commandConfig.getAliases().isEmpty()) {
                    commandConfig.getAliases().stream().forEach(alias -> map.register(alias, commandConfig.getPrefix(), executor));
                }
                if (commandConfig.getDescription() != null) {
                    executor.setDescription(commandConfig.getDescription());
                }
                if (commandConfig.getUsage() != null) {
                    executor.setUsage(commandConfig.getUsage());
                }
                if (commandConfig.getPermission() != null) {
                    executor.setPermission(commandConfig.getPermission());
                }
            });
            LiteCommandEditorCommandAlias.getCommandAliases().stream().forEach(executor -> map.register(executor.getCommandName(), executor.getCommandPrefix(), executor));
            //If the command list is changed, then syncCommands()
            if (registeredCommandsName == null) {
                registeredCommandsName = new HashSet();
                registeredCommandsName.addAll(registeredCommands.stream().map(command -> command.getPrefix() + ":" + command.getCommandName()).collect(Collectors.toList()));
                registeredCommands.stream().forEach(command -> registeredCommandsName.addAll(command.getAliases().stream().map(alias -> command.getPrefix() + ":" + alias).collect(Collectors.toList())));
                LiteCommandEditorCommandAlias.getCommandAliases().stream().forEach(executor -> {
                    map.register(executor.getCommandName(), executor.getCommandPrefix(), executor);
                    registeredAliasCommands.add(executor.getCommandPrefix() + ":" + executor.getCommandName());
                    registeredAliasCommands.add(executor.getCommandName());
                    registeredCommandsName.add(executor.getCommandPrefix() + ":" + executor.getCommandName());
                });
            } else {
                Set<String> lastChanges = new HashSet<>(registeredCommandsName);
                registeredCommandsName.clear();
                registeredCommandsName.addAll(registeredCommands.stream().map(command -> command.getPrefix() + ":" + command.getCommandName()).collect(Collectors.toList()));
                registeredCommands.stream().forEach(command -> registeredCommandsName.addAll(command.getAliases().stream().map(alias -> command.getPrefix() + ":" + alias).collect(Collectors.toList())));
                LiteCommandEditorCommandAlias.getCommandAliases().stream().forEach(executor -> {
                    map.register(executor.getCommandName(), executor.getCommandPrefix(), executor);
                    registeredAliasCommands.add(executor.getCommandPrefix() + ":" + executor.getCommandName());
                    registeredAliasCommands.add(executor.getCommandName());
                    registeredCommandsName.add(executor.getCommandPrefix() + ":" + executor.getCommandName());
                });
                if (!registeredCommandsName.equals(lastChanges)) {
                    syncCommands();
                }
            }
            Bukkit.getPluginManager().callEvent(new CommandConfigurationsRegisteredEvent(map, registeredCommands));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void syncCommands() {
        try {
            if (!Bukkit.getBukkitVersion().startsWith("1.7") && !Bukkit.getBukkitVersion().startsWith("1.8") && !Bukkit.getBukkitVersion().startsWith("1.9") && !Bukkit.getBukkitVersion().startsWith("1.10") &&
                !Bukkit.getBukkitVersion().startsWith("1.11") && !Bukkit.getBukkitVersion().startsWith("1.12")) {
                Bukkit.getServer().getClass().getMethod("syncCommands").invoke(Bukkit.getServer());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static CommandConfiguration getCommandConfiguration(String commandName) {
        return registeredCommands.stream().filter(command -> command.getCommandName().equalsIgnoreCase(commandName) || command.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(commandName))).findFirst().orElse(null);
    }
    
    public static CommandMap getServerCommandMap() {
        try {
            return (CommandMap) Bukkit.getServer().getClass().getMethod("getCommandMap").invoke(Bukkit.getServer());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static Map<String, Command> getServerCommands() {
        try {
            CommandMap map = getServerCommandMap();
            Field field = SimpleCommandMap.class.getDeclaredField("knownCommands");
            field.setAccessible(true);
            Map<String, Command> knownCommands = (Map<String, Command>) field.get(map);
            field.setAccessible(false);
            return knownCommands;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
