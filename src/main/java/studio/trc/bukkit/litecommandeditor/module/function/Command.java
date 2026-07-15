package studio.trc.bukkit.litecommandeditor.module.function;

import java.util.Map;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.Main;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.thread.LiteCommandEditorThread;
import studio.trc.bukkit.litecommandeditor.util.BukkitSchedulerManager;

public class Command
{
    @Getter
    private final CommandType type;
    @Getter
    private final String command;

    private Command(CommandType type, String command) {
        this.type = type;
        this.command = command;
    }

    public void executeCommand(CommandSender sender) {
        executeCommand(sender, MessageUtil.getDefaultPlaceholders());
    }
    
    public void executeCommand(CommandSender sender, Map<String, String> placeholders) {
        String commandReplaced = MessageUtil.replacePlaceholders(sender, command, placeholders);
        Runnable execution = () -> {
            switch (type) {
                case PLAYER: {
                    Bukkit.dispatchCommand(sender, commandReplaced);
                    break;
                }
                case OP: {
                    if (sender.isOp()) {
                        Bukkit.dispatchCommand(sender, commandReplaced);
                    } else {
                        sender.setOp(true);
                        try {
                            Bukkit.dispatchCommand(sender, commandReplaced);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        } finally {
                            sender.setOp(false);
                        }
                    }
                    break;
                }
                case SERVER: {
                    Main.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), commandReplaced);
                    break;
                }
            }
        };
        if (LiteCommandEditorThread.checkAsync(sender)) {
            BukkitSchedulerManager.runBukkitTask(execution, 0, sender);
        } else {
            execution.run();
        }
    }

    @Override
    public String toString() {
        return type.name() + ":" + command;
    }
    
    public static Command build(String command) {
        if (command.toLowerCase().startsWith("server:")) {
            return new Command(CommandType.SERVER, command.substring(7));
        } else if (command.toLowerCase().startsWith("op:")) {
            return new Command(CommandType.OP, command.substring(3));
        } else {
            return new Command(CommandType.PLAYER, command);
        }
    }
    
    public static Command build(String command, Map<String, String> placeholders) {
        if (command.toLowerCase().startsWith("server:")) {
            return new Command(CommandType.SERVER, MessageUtil.replacePlaceholders(command.substring(7), placeholders));
        } else if (command.toLowerCase().startsWith("op:")) {
            return new Command(CommandType.OP, MessageUtil.replacePlaceholders(command.substring(3), placeholders));
        } else {
            return new Command(CommandType.PLAYER, MessageUtil.replacePlaceholders(command, placeholders));
        }
    }
    
    public static Command build(CommandSender sender, String command, Map<String, String> placeholders) {
        if (command.toLowerCase().startsWith("server:")) {
            return new Command(CommandType.SERVER, MessageUtil.replacePlaceholders(sender, command.substring(7), placeholders));
        } else if (command.toLowerCase().startsWith("op:")) {
            return new Command(CommandType.OP, MessageUtil.replacePlaceholders(sender, command.substring(3), placeholders));
        } else {
            return new Command(CommandType.PLAYER, MessageUtil.replacePlaceholders(sender, command, placeholders));
        }
    }
}
