package io.github.steaf23.bingoreloaded.gameloop;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.data.BingoTranslation;
import io.github.steaf23.bingoreloaded.data.ConfigData;
import io.github.steaf23.bingoreloaded.event.*;
import io.github.steaf23.bingoreloaded.gui.VoteMenu;
import io.github.steaf23.bingoreloaded.gui.base.MenuItem;
import io.github.steaf23.bingoreloaded.gui.base.MenuManager;
import io.github.steaf23.bingoreloaded.settings.PlayerKit;
import io.github.steaf23.bingoreloaded.settings.SettingsPreviewBoard;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PregameLobby implements GamePhase {
    private final BingoSession session;
    private final SettingsPreviewBoard settingsBoard;
    private final Map<UUID, VoteTicket> votes;
    private final ConfigData config;
    private final MenuManager menuManager;
    public PregameLobby(MenuManager menuManager, BingoSession session, ConfigData config) {
        this.menuManager = menuManager;
        this.session = session;
        this.settingsBoard = new SettingsPreviewBoard();
        settingsBoard.showSettings(session.settingsBuilder.view());
        this.votes = new HashMap<>();
        this.config = config;

        BingoReloaded.scheduleTask((t) -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (BingoReloaded.getWorldNameOfDimension(p.getWorld()).equals(session.worldName)) {
                    initializePlayer(p.getPlayer());
                }
            }
        }, 10);

        settingsBoard.setStatus(BingoTranslation.WAIT_STATUS.translate());
    }

    public void voteGamemode(String gamemode, HumanEntity player) {
        if (!config.useVoteSystem) {
            Message.warn("Players cannot vote because useVoteSystem is false!");
            return;
        }

        VoteTicket ticket = votes.getOrDefault(player.getUniqueId(), new VoteTicket());

        if (!gamemode.equals(ticket.gamemode)) {
            ticket.gamemode = gamemode;
        }
        votes.put(player.getUniqueId(), ticket);
    }

    public void voteCard(String card, HumanEntity player) {
        if (!config.useVoteSystem) {
            Message.warn("Players cannot vote because useVoteSystem is false!");
            return;
        }

        VoteTicket ticket = votes.getOrDefault(player.getUniqueId(), new VoteTicket());

        if (!card.equals(ticket.card)) {
            ticket.card = card;
        }
        votes.put(player.getUniqueId(), ticket);
    }

    public void voteKit(String kit, HumanEntity player) {
        if (!config.useVoteSystem) {
            Message.warn("Players cannot vote because useVoteSystem is false!");
            return;
        }

        VoteTicket ticket = votes.getOrDefault(player.getUniqueId(), new VoteTicket());

        if (!kit.equals(ticket.kit)) {
            ticket.kit = kit;
        }
        votes.put(player.getUniqueId(), ticket);
    }

    public VoteTicket getVoteResult() {
        VoteTicket outcome = new VoteTicket();

        Map<String, Integer> gamemodes = new HashMap<>();
        Map<String, Integer> kits = new HashMap<>();
        Map<String, Integer> cards = new HashMap<>();

        for (UUID player : votes.keySet()) {
            VoteTicket ticket = votes.get(player);
            gamemodes.put(ticket.gamemode, gamemodes.getOrDefault(ticket.gamemode, 0) + 1);
            kits.put(ticket.kit, kits.getOrDefault(ticket.kit, 0) + 1);
            cards.put(ticket.card, cards.getOrDefault(ticket.card, 0) + 1);
        }

        outcome.gamemode = getKeyWithHighestValue(gamemodes);
        outcome.kit = getKeyWithHighestValue(kits);
        outcome.card = getKeyWithHighestValue(cards);

        return outcome;
    }

    private String getKeyWithHighestValue(Map<String, Integer> values) {
        String recordKey = "";
        for (var k : values.keySet()) {
            if (recordKey.isEmpty() || values.get(k) > values.get(recordKey)) {
                recordKey = k;
            }
        }
        return recordKey;
    }

    private void giveVoteItem(Player player) {
        player.getInventory().addItem(PlayerKit.VOTE_ITEM);
    }

    private void giveTeamItem(Player player) {
        player.getInventory().addItem(PlayerKit.TEAM_ITEM);
    }

    private void initializePlayer(Player player) {
        settingsBoard.applyToPlayer(player);
        player.getInventory().clear();

        if (config.useVoteSystem && !config.voteUsingCommandsOnly && !config.voteList.isEmpty()) {
            giveVoteItem(player);
        }
        if (!config.selectTeamUsingCommandsOnly) {
            giveTeamItem(player);
        }
    }

    public void handleParticipantJoinedTeam(final ParticipantJoinedTeamEvent event) {
        settingsBoard.setStatus(BingoTranslation.PLAYER_STATUS.translate("" + session.teamManager.getTotalParticipantCount()));
    }

    public void handleParticipantLeftTeam(final ParticipantLeftTeamEvent event) {
        settingsBoard.setStatus(BingoTranslation.PLAYER_STATUS.translate("" + session.teamManager.getTotalParticipantCount()));
    }

    @Override
    public void handlePlayerJoinedSessionWorld(final PlayerJoinedSessionWorldEvent event) {
        initializePlayer(event.getPlayer());
    }

    @Override
    public void handlePlayerLeftSessionWorld(final PlayerLeftSessionWorldEvent event) {
        settingsBoard.clearPlayerBoard(event.getPlayer());
        session.teamManager.removeMemberFromTeam(event.getPlayer());
    }

    @Override
    public void handleSettingsUpdated(final BingoSettingsUpdatedEvent event) {
        settingsBoard.handleSettingsUpdated(event);
    }

    @Override
    public void handlePlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() == Material.AIR)
            return;

        MenuItem item = new MenuItem(event.getItem());

        if (item.getCompareKey().equals("vote")) {
            event.setCancelled(true);
            VoteMenu menu = new VoteMenu(menuManager, config.voteList, this);
            menu.open(event.getPlayer());
        } else if (item.getCompareKey().equals("team")) {
            event.setCancelled(true);
            session.teamManager.openTeamSelector(menuManager, event.getPlayer());
        }
    }

    // Each player can cast a single vote for all categories, To keep track of this a VoteTicket will be made for every player that votes on something
    public static class VoteTicket {
        public String gamemode = "";
        public String kit = "";
        public String card = "";
    }
}
