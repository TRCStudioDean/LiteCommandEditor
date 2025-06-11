package studio.trc.bukkit.litecommandeditor.event.listener;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.configuration.RobustConfiguration;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandExecutor;
import studio.trc.bukkit.litecommandeditor.module.CommandManager;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;
import studio.trc.bukkit.litecommandeditor.util.PermissionManager;

public class PlayerEventManager
    implements Listener
{
    private static final Map<UUID, CommandCooldown> playerCommandRecords = new HashMap<>();
    
    public static void clearRecords() {
        playerCommandRecords.clear();
    }
    
    public String getCommandName(String command) {
        return command.split(" ", -1)[0];
    }
    
    public String getCommandArguments(String command) {
        String[] commandInfo = command.split(" ", -1);
        List<String> arguments = Arrays.stream(commandInfo).collect(Collectors.toList());
        arguments.remove(0); //Remove the main command.
        return String.join(" ", arguments);
    }
    
    public String getCommandPrefix(Command command) {
        if (command instanceof CommandExecutor) {
            CommandExecutor executor = (CommandExecutor) command;
            return executor.getCommandConfig().getPrefix();
        } else if (command instanceof PluginCommand) {
            PluginCommand executor = (PluginCommand) command;
            return executor.getPlugin().getName().toLowerCase();
        } else {
            try {
                CommandMap map = CommandManager.getServerCommandMap();
                Field field = SimpleCommandMap.class.getDeclaredField("knownCommands");
                field.setAccessible(true);
                Map<String, Command> knownCommands = (Map<String, Command>) field.get(map);
                String prefix = knownCommands.keySet().stream().filter(commandName -> commandName.contains(":") && commandName.split(":")[1].equalsIgnoreCase(command.getName())).map(commandName -> commandName.split(":")[0]).findFirst().orElse(null);
                field.setAccessible(false);
                return prefix;
            } catch (Exception ex) {
                return null;
            }
        }
    }
    
    public Map<String, Double> getCooldownList() {
        RobustConfiguration config = ConfigurationType.CONFIG.getRobustConfig();
        if (!config.getBoolean("Commands-Cooldown.Enabled") || config.getList("Commands-Cooldown.List").isEmpty()) return null;
        Map<String, Double> result = new HashMap<>();
        config.getList("Commands-Cooldown.List").stream().filter(section -> section instanceof Map).forEach(section -> {
            Map raw = (Map) section;
            raw.keySet().stream().filter(name -> LiteCommandEditorUtils.isDouble(raw.get(name).toString())).forEach(name -> {
                Command command = CommandManager.getServerCommandMap().getCommand(getCommandName(name.toString()));
                if (command != null) {
                    String prefix = getCommandPrefix(command);
                    String arguments = getCommandArguments(name.toString());
                    result.put((prefix != null ? prefix + ":" : "") + command.getName() + (!arguments.isEmpty() ? " " + arguments : ""), Double.valueOf(raw.get(name).toString()));
                    command.getAliases().stream().forEach(alias -> result.put((prefix != null ? prefix + ":" : "") + alias + (!arguments.isEmpty() ? " " + arguments : ""), Double.valueOf(raw.get(name).toString())));
                }
            });
        });
        return result;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommandExecute(PlayerCommandPreprocessEvent event) {
        RobustConfiguration config = ConfigurationType.CONFIG.getRobustConfig();
        if (!config.getBoolean("Commands-Cooldown.Enabled") || config.getList("Commands-Cooldown.List").isEmpty()) return;
        Player player = event.getPlayer();
        if (PermissionManager.hasPermission(player, ConfigurationType.PERMISSIONS, "Command-Cooldown-Bypass")) return;
        if (event.getMessage().startsWith("/")) {
            Command command = CommandManager.getServerCommandMap().getCommand(getCommandName(event.getMessage().substring(1)).toLowerCase());
            Map<String, Double> rules = getCooldownList();
            if (command != null) {
                String prefix = getCommandPrefix(command);
                String main = command.getName();
                List<String> aliases = command.getAliases();
                String arguments = getCommandArguments(event.getMessage().substring(1));
                String ruleName = rules.keySet().stream().filter(rule -> ((prefix != null ? prefix + ":" : "") + main + (!arguments.isEmpty() ? " " + arguments : "")).startsWith(rule)).findFirst().orElse(null);
                if (ruleName != null) {
                    if (!playerCommandRecords.containsKey(player.getUniqueId())) {
                        playerCommandRecords.put(player.getUniqueId(), new CommandCooldown(System.currentTimeMillis(), prefix, main, arguments, aliases));
                    } else {
                        CommandCooldown record = playerCommandRecords.get(player.getUniqueId());
                        if (System.currentTimeMillis() - record.time < rules.get(ruleName) * 1000) {
                            event.setCancelled(true);
                            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                            placeholders.put("{second}", String.valueOf(Double.valueOf(new DecimalFormat("0.#").format(rules.get(ruleName) - ((double) (System.currentTimeMillis() - record.time) / 1000)))));
                            MessageUtil.sendMessage(player, ConfigurationType.MESSAGES.getRobustConfig(), "Cooldown-Of-Command", placeholders);
                        } else {
                            playerCommandRecords.put(player.getUniqueId(), new CommandCooldown(System.currentTimeMillis(), prefix, main, arguments, aliases));
                        }
                    }
                }
            }
        }
    }
    
    private class CommandCooldown {
        protected final long time;
        protected final String prefix;
        protected final String name;
        protected final String arguments;
        protected final List<String> aliases;
        
        protected CommandCooldown(long time, String prefix, String name, String arguments, List<String> aliases) {
            this.time = time;
            this.prefix = prefix;
            this.name = name;
            this.aliases = aliases;
            this.arguments = arguments;
        }
    }
}
