package studio.trc.bukkit.litecommandeditor.module.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.module.command.CommandFunctionTask;
import studio.trc.bukkit.litecommandeditor.util.PluginControl;

public class CommandBatch
    implements CommandFunctionTask
{
    @Getter
    private final List<Command> commands;
    @Getter
    private final String identifier = "Command";
    
    public CommandBatch(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public void executeTask(CommandSender sender, Map<String, String> placeholders) {
        PluginControl.runBukkitTask(() -> commands.stream().forEach(command -> command.executeCommand(sender, placeholders)), 0);
    }
    
    public static CommandBatch build(Map map) {
        if (map.get("Commands") != null) {
            List<Command> commands = new ArrayList<>();
            if (map.get("Commands") instanceof Collection) {
                ((Collection<? extends String>) map.get("Commands")).stream().forEach(command -> commands.add(Command.build(command)));
            } else {
                commands.add(Command.build(map.get("Commands").toString()));
            }
            return new CommandBatch(commands);
        }
        return null;
    }

    @Override
    public String toString() {
        return "[" + getIdentifier() + "]: Commands=" + commands;
    }
}
