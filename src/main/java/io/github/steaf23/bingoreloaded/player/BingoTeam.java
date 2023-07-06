package io.github.steaf23.bingoreloaded.player;

import io.github.steaf23.bingoreloaded.gui.cards.BingoCard;
import io.github.steaf23.bingoreloaded.item.ItemText;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;

public class BingoTeam {
    public final Team team;
    private final FlexColor color;
    public BingoCard card;
    public Set<BingoPlayer> players;
    public boolean outOfTheGame = false;

    public BingoTeam(Team team, BingoCard card, FlexColor color) {
        this.team = team;
        this.card = card;
        this.color = color;
        this.players = new HashSet<>();
    }

    public String getName() {
        return color.name;
    }

    public FlexColor getColor() {
        return color;
    }

    public ItemText getColoredName() {
        return new ItemText(color.getTranslatedName(), color.chatColor, ChatColor.BOLD);
    }

    public Set<BingoPlayer> getPlayers() {
        return players;
    }

    public void addPlayer(BingoPlayer player) {
        players.add(player);
        team.addEntry(player.playerId().toString());
    }

    public void removePlayer(BingoPlayer player) {
        players.remove(player);
        team.removeEntry(player.playerId().toString());
    }
}