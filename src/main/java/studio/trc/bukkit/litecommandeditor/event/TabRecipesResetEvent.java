package studio.trc.bukkit.litecommandeditor.event;

import java.util.Map;

import lombok.Getter;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import studio.trc.bukkit.litecommandeditor.module.tab.TabRecipe;

public class TabRecipesResetEvent
    extends Event
{
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Map<String, Class<? extends TabRecipe>> recipes;

    public TabRecipesResetEvent(Map<String, Class<? extends TabRecipe>> recipes) {
        this.recipes = recipes;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
