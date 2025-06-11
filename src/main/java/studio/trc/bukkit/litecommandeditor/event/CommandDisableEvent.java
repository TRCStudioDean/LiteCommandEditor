package studio.trc.bukkit.litecommandeditor.event;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CommandDisableEvent 
    extends Event
    implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final String commandPrefix;
    @Getter
    private final String commandName;
    @Getter
    @Setter
    private boolean cancelled;

    public CommandDisableEvent(String commandPrefix, String commandName) {
        this.commandName = commandName;
        this.commandPrefix = commandPrefix;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
