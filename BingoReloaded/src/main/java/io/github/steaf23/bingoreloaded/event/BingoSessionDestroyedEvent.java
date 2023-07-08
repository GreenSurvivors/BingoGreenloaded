package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BingoSessionDestroyedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    public final BingoSession session;

    public BingoSessionDestroyedEvent(BingoSession session) {
        this.session = session;
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
