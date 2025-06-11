package studio.trc.bukkit.litecommandeditor.event;

import java.util.List;

import lombok.Getter;

import org.bukkit.command.CommandMap;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;

public class CommandConfigurationsRegisteredEvent
    extends Event
{
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final CommandMap commandMap;
    @Getter
    private final List<CommandConfiguration> configurations;

    public CommandConfigurationsRegisteredEvent(CommandMap commandMap, List<CommandConfiguration> configurations) {
        this.commandMap = commandMap;
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
