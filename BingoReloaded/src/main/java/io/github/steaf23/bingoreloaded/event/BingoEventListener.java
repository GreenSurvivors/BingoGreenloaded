package io.github.steaf23.bingoreloaded.event;

import io.github.steaf23.bingoreloaded.gameloop.BingoGame;
import io.github.steaf23.bingoreloaded.gameloop.BingoSession;
import io.github.steaf23.bingoreloaded.gameloop.PregameLobby;
import io.github.steaf23.bingoreloaded.tasks.statistics.StatisticTracker;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;

import javax.annotation.Nullable;
import java.util.function.Function;

public class BingoEventListener implements Listener
{
    private final Function<World, BingoSession> sessionResolver;
    private final boolean disableAdvancements;
    private final boolean disableStatistics;

    public BingoEventListener(Function<World, BingoSession> sessionResolver, boolean disableAdvancements, boolean disableStatistics)
    {
        this.sessionResolver = sessionResolver;
        this.disableAdvancements = disableAdvancements;
        this.disableStatistics = disableStatistics;
    }

    @Nullable
    private BingoSession getSession(World world)
    {
        return sessionResolver.apply(world);
    }

    @EventHandler
    public void handleBingoGameEnded(final BingoEndedEvent event)
    {
        if (event.session != null)
        {
            event.session.handleGameEnded(event);
        }
    }

    @EventHandler
    public void handleBingoTaskComplete(final BingoCardTaskCompleteEvent event)
    {
        BingoGame game = event.session != null && event.session.isRunning() ? (BingoGame)event.session.phase() : null;
        if (game != null)
        {
            game.handleBingoTaskComplete(event);
        }
    }

    @EventHandler
    public void handlePlayerDropItem(final PlayerDropItemEvent event)
    {
        BingoSession session = getSession(event.getPlayer().getWorld());
        if (session == null)
            return;

        session.handlePlayerDropItem(event);

        BingoGame game = session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            game.getCardEventManager().handlePlayerDroppedItem(event, game);
        }
    }

    @EventHandler
    public void handlePlayerInteract(final PlayerInteractEvent event)
    {
        BingoSession session = getSession(event.getPlayer().getWorld());
        if (session == null)
        {
            return;
        }

        // Determine if the event is fired for the correct hand, to avoid duplicate events
        if (!(event.getHand() == EquipmentSlot.HAND && event.getPlayer().getInventory().getItemInMainHand().equals(event.getItem()) ||
                event.getHand() == EquipmentSlot.OFF_HAND && event.getPlayer().getInventory().getItemInOffHand().equals(event.getItem())))
        {
            return;
        }

        if (session.isRunning())
        {
            session.teamManager.handlePlayerShowCard(event, ((BingoGame)session.phase()).getDeathMatchTask());
        }
        else
        {
            session.teamManager.handlePlayerShowCard(event, null);
        }

        session.phase().handlePlayerInteract(event);
    }

    @EventHandler
    public void handleEntityDamage(final EntityDamageEvent event)
    {
        BingoSession session = getSession(event.getEntity().getWorld());
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            game.handleEntityDamage(event);
        }
    }

    @EventHandler
    public void handlePlayerDeath(final PlayerDeathEvent event)
    {
        BingoSession session = getSession(event.getEntity().getWorld());
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            game.handlePlayerDeath(event);
        }
    }

    @EventHandler
    public void handlePlayerRespawn(final PlayerRespawnEvent event)
    {
        BingoSession session = getSession(event.getPlayer().getWorld());
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            game.handlePlayerRespawn(event);
        }
    }

    @EventHandler
    public void handleCountdownFinished(final CountdownTimerFinishedEvent event)
    {
        BingoGame game = event.session != null && event.session.isRunning() ? (BingoGame)event.session.phase() : null;
        if (game != null)
        {
            game.handleCountdownFinished(event);
        }
    }

    @EventHandler
    public void handlePlayerAdvancementCompleted(final PlayerAdvancementDoneEvent event)
    {
        if (disableAdvancements)
            return;

        BingoSession session = getSession(event.getPlayer().getWorld());
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            game.getCardEventManager().handlePlayerAdvancementCompleted(event, getSession(event.getPlayer().getWorld()));
        }
    }

    @EventHandler
    public void handlePlayerPickupItem(final EntityPickupItemEvent event)
    {
        BingoSession session = getSession(event.getEntity().getWorld());
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            game.getCardEventManager().handlePlayerPickupItem(event, game);
        }
    }

    @EventHandler
    public void handleInventoryClicked(final InventoryClickEvent event)
    {
        BingoSession session = getSession(event.getWhoClicked().getWorld());
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            game.getCardEventManager().handleInventoryClicked(event, game);
        }
    }

    @EventHandler
    public void handlePlayerJoinsServer(final PlayerJoinEvent event)
    {
        BingoSession session = getSession(event.getPlayer().getWorld());
        if (session == null)
            return;

        session.handlePlayerJoinsServer(event);
    }

    @EventHandler
    public void handlePlayerQuitsServer(final PlayerQuitEvent event)
    {
        BingoSession session = getSession(event.getPlayer().getWorld());
        if (session == null)
            return;

        session.handlePlayerQuitsServer(event);
    }

    @EventHandler
    public void handlePlayerTeleport(final PlayerTeleportEvent event)
    {
        // This event is special in the sense we need to catch the session both
        //    as the player is teleporting into a bingo world and teleporting out of a bingo world
        BingoSession session = getSession(event.getTo().getWorld());
        if (session != null)
        {
            session.handlePlayerTeleport(event);
        }

        session = getSession(event.getFrom().getWorld());
        if (session != null)
        {
            session.handlePlayerTeleport(event);
        }
    }

    @EventHandler
    public void onPlayerItemDamaged(PlayerItemDamageEvent event)
    {
        BingoSession session = getSession(event.getPlayer().getWorld());
        if (session != null && session.isRunning())
        {
            ((BingoGame)session.phase()).handlePlayerItemDamaged(event);
        }
    }

    @EventHandler
    public void handleStatisticIncrement(final PlayerStatisticIncrementEvent event)
    {
        if (disableStatistics)
            return;

        BingoSession session = getSession(event.getPlayer().getWorld());
        BingoGame game = session != null && session.isRunning() ? (BingoGame)session.phase() : null;
        if (game != null)
        {
            StatisticTracker tracker = game.getStatisticTracker();
            if (tracker != null)
                tracker.handleStatisticIncrement(event, game);
        }
    }

    @EventHandler
    public void handleStatisticCompleted(final BingoStatisticCompletedEvent event)
    {
        if (disableStatistics)
            return;

        BingoGame game = event.session != null && event.session.isRunning() ? (BingoGame)event.session.phase() : null;
        if (game != null)
        {
            game.getCardEventManager().handleStatisticCompleted(event, game);
        }
    }

    @EventHandler
    public void handlePlayerMove(final PlayerMoveEvent event)
    {
        BingoSession session = getSession(event.getPlayer().getWorld());
        if (session == null)
            return;

        if (session.isRunning())
        {
            ((BingoGame)session.phase()).handlePlayerMove(event);
        }
    }

    @EventHandler
    public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event)
    {
        event.session.handleSettingsUpdated(event);
    }

    @EventHandler
    public void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event)
    {
        event.session.handlePlayerJoinedSessionWorld(event);
        event.session.phase().handlePlayerJoinedSessionWorld(event);
        event.session.scoreboard.handlePlayerJoin(event);
        event.session.teamManager.handlePlayerJoinedSessionWorld(event);
    }

    @EventHandler
    public void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event)
    {
        event.session.handlePlayerLeftSessionWorld(event);
        event.session.phase().handlePlayerLeftSessionWorld(event);
        event.session.scoreboard.handlePlayerLeave(event);
        event.session.teamManager.handlePlayerLeftSessionWorld(event);
    }

    @EventHandler
    public void handleParticipantJoinedTeam(final ParticipantJoinedTeamEvent event) {
        if (event.session.phase() instanceof PregameLobby lobby) {
            lobby.handleParticipantJoinedTeam(event);
        }
    }

    @EventHandler
    public void handleParticipantLeftTeam(final ParticipantLeftTeamEvent event) {
        if (event.session.phase() instanceof PregameLobby lobby) {
            lobby.handleParticipantLeftTeam(event);
        }
    }
}
