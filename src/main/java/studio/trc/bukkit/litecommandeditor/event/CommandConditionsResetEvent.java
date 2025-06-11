package studio.trc.bukkit.litecommandeditor.event;

import java.util.Map;

import lombok.Getter;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import studio.trc.bukkit.litecommandeditor.module.CommandCondition;

public class CommandConditionsResetEvent
    extends Event
{
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Map<String, Class<? extends CommandCondition>> conditions;

    public CommandConditionsResetEvent(Map<String, Class<? extends CommandCondition>> conditions) {
        this.conditions = conditions;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
