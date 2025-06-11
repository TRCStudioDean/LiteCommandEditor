package studio.trc.bukkit.litecommandeditor.event;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import studio.trc.bukkit.litecommandeditor.module.tab.TabFunction;

public class TabFunctionEvent
    extends Event
    implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final TabFunction function;
    @Getter
    @Setter
    private boolean cancelled;

    public TabFunctionEvent(TabFunction function) {
        this.function = function;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
