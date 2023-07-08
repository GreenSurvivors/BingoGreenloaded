package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.gui.base.MenuManager;

import javax.annotation.Nullable;

public interface BingoGameManager {
    @Nullable
    BingoSession getSession(String worldName);

    MenuManager getMenuManager();

    void onDisable();
}
