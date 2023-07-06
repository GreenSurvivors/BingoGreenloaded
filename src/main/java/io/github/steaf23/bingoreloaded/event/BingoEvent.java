package io.github.steaf23.bingoreloaded.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class BingoEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    public final String worldName;

    protected BingoEvent(String worldName) {
        this.worldName = worldName;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
