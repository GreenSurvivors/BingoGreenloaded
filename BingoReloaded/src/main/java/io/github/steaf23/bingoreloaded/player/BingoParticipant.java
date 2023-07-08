package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.tasks.BingoTask;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface BingoParticipant {
    BingoSession getSession();

    @Nullable
    BingoTeam getTeam();

    UUID getId();

    Optional<Player> sessionPlayer();

    String getDisplayName();

    void showDeathMatchTask(BingoTask task);

    boolean alwaysActive();
}
