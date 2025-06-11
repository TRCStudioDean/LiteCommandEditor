package studio.trc.bukkit.litecommandeditor.event;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import studio.trc.bukkit.litecommandeditor.module.command.CommandFunction;
import studio.trc.bukkit.litecommandeditor.module.command.CommandCompoundFunctionType;

public class CommandTaskEvent
    extends Event
    implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final CommandFunction function;
    @Getter
    private final CommandSender sender;
    @Getter
    private final Map<String, String> placeholders;
    @Getter
    private final CommandCompoundFunctionType task;
    @Getter
    @Setter
    private boolean cancelled;

    public CommandTaskEvent(CommandFunction function, CommandSender sender, Map<String, String> placeholders, CommandCompoundFunctionType task) {
        this.function = function;
        this.sender = sender;
        this.placeholders = placeholders;
        this.task = task;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
