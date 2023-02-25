package io.github.steaf23.bingoreloaded.event;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CountdownTimerFinishedEvent extends BingoEvent
{
    private static final HandlerList HANDLERS = new HandlerList();

    public CountdownTimerFinishedEvent(String worldName)
    {
        super(worldName);
    }

    @Override
    public @NotNull HandlerList getHandlers()
    {
        return HANDLERS;
    }

    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }
}
