package studio.trc.bukkit.litecommandeditor.event;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;

public class CommandConfigurationLoadEvent
    extends Event
    implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final CommandConfiguration configuration;
    @Getter
    @Setter
    private boolean cancelled;

    public CommandConfigurationLoadEvent(CommandConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
