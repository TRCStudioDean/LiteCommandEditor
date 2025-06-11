package studio.trc.bukkit.litecommandeditor.event;

import java.util.Map;

import lombok.Getter;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;

public class CommandConfigurationsLoadedEvent
    extends Event
{
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Map<String, CommandConfiguration> configurations;

    public CommandConfigurationsLoadedEvent(Map<String, CommandConfiguration> configurations) {
        this.configurations = configurations;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
