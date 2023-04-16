package io.github.steaf23.brmultimode;

import io.github.steaf23.bingoreloaded.BingoGamemode;
import io.github.steaf23.bingoreloaded.BingoSession;
import io.github.steaf23.bingoreloaded.BingoSettings;
import io.github.steaf23.bingoreloaded.BingoSettingsBuilder;
import io.github.steaf23.bingoreloaded.cards.CardSize;
import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.data.BingoSettingsData;
import io.github.steaf23.bingoreloaded.gui.EffectOptionFlags;
import io.github.steaf23.bingoreloaded.player.BingoPlayer;
import io.github.steaf23.bingoreloaded.player.PlayerKit;
import io.github.steaf23.bingoreloaded.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;

public class MultiAutoBingoCommand implements CommandExecutor
{
    private final BingoGameManager manager;

    public MultiAutoBingoCommand(BingoGameManager manager)
    {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command autobingoCommand, @NonNull String alias, @NonNull String[] args)
    {
        // AutoBingo should only work for admins or console.
        if (commandSender instanceof Player p && !p.hasPermission("bingo.admin"))
        {
            return false;
        }

        if (args.length < 2)
        {
            return false;
        }
        String worldName = args[0];
        String command = args[1];
        String[] extraArguments = args.length > 2 ? Arrays.copyOfRange(args, 2, args.length - 1) : new String[]{};
        BingoSettingsBuilder settings = null;
        BingoSession session = manager.getSession(worldName);
        if (session != null)
            settings = manager.getSession(worldName).settingsBuilder;

        // All commands besides "create" can only be executed if a game session exists in the given world
        if (!command.equals("create") && !manager.doesSessionExist(worldName))
        {
            sendFailed("Cannot perform command on a world that has not been created (yet)!", worldName);
            return false;
        }

        boolean success = switch(command)
        {
            case "create" -> create(worldName, extraArguments);
            case "destroy" -> destroy(worldName);
            case "start" -> start(worldName);
            case "kit" -> setKit(settings, worldName, extraArguments);
            case "effects" -> setEffect(settings, worldName, extraArguments);
            case "card" -> setCard(settings, worldName, extraArguments);
            case "countdown" -> setCountdown(settings, worldName, extraArguments);
            case "duration" -> setDuration(settings, worldName, extraArguments);
            case "team" -> setPlayerTeam(worldName, extraArguments);
            case "teamsize" -> setTeamSize(settings, worldName, extraArguments);
            case "gamemode" -> setGamemode(settings, worldName, extraArguments);
            case "end" -> end(worldName);
            case "preset" -> preset(settings, worldName, extraArguments);
            default -> {
                Message.log(ChatColor.RED + "Invalid command: '" + command + "' not recognized");
                yield false;
            }
        };

        return success;
    }

    public boolean create(String worldName, String[] extraArguments)
    {
        if (extraArguments.length > 1)
        {
            sendFailed("Expected at most 3 arguments", worldName);
        }

        String presetName = extraArguments.length == 1 ? extraArguments[0] : "";
        manager.createSession(worldName, presetName);
        sendSuccess("Connected Bingo Reloaded to this world!", worldName);
        return true;
    }

    public boolean destroy(String worldName)
    {
        manager.destroySession(worldName);
        sendSuccess("Disconnected Bingo Reloaded from this world!", worldName);
        return true;
    }

    public boolean start(String worldName)
    {
        if (manager.startGame(worldName))
        {
            sendFailed("Could not start game", worldName);
            return false;
        }
        else
        {
            sendSuccess("The game has started!", worldName);
            return true;
        }
    }

    private boolean setKit(BingoSettingsBuilder settings, String worldName, String[] extraArguments)
    {
        if (extraArguments.length != 1)
        {
            sendFailed("Expected 3 arguments!", worldName);
            return false;
        }

        PlayerKit kit = PlayerKit.fromConfig(extraArguments[0]);
        settings.kit(kit);
        sendSuccess("Kit set to " + kit.displayName, worldName);
        return true;
    }

    public boolean setEffect(BingoSettingsBuilder settings, String worldName, String[] extraArguments)
    {
        // autobingo world effect <effect_name> [true | false]
        // If argument count is only 1, enable all, none or just the single effect typed.
        //     Else default enable effect unless the second argument is "false".

        if (extraArguments.length == 0)
        {
            sendFailed("Expected at least 3 arguments!", worldName);
            return false;
        }
        String effect = extraArguments[0];
        boolean enable = extraArguments.length > 1 && extraArguments[1].equals("false") ? false : true;

        if (effect.equals("all"))
        {
            settings.effects(EffectOptionFlags.ALL_ON);
            sendSuccess("Updated active effects to " + EffectOptionFlags.ALL_ON, worldName);
            return true;
        }
        else if (effect.equals("none"))
        {
            settings.effects(EffectOptionFlags.ALL_OFF);
            sendSuccess("Updated active effects to " + EffectOptionFlags.ALL_OFF, worldName);
            return true;
        }

        try
        {
            settings.toggleEffect(EffectOptionFlags.valueOf(effect.toUpperCase()), enable);
            sendSuccess("Updated active effects to " + settings.view().effects(), worldName);
            return true;
        }
        catch (IllegalArgumentException e)
        {
            sendFailed("Invalid effect: " + effect, worldName);
            return false;
        }
    }

    private boolean setCard(BingoSettingsBuilder settings, String worldName, String[] extraArguments)
    {
        if (extraArguments.length == 0)
        {
            sendFailed("Expected at least 3 arguments!", worldName);
            return false;
        }

        String cardName = extraArguments[0];
        int seed = extraArguments.length > 1 ? toInt(extraArguments[1], 0) : 0;

        BingoCardsData cardsData = new BingoCardsData();
        if (cardsData.getCardNames().contains(cardName))
        {
            settings.card(cardName).cardSeed(seed);
            sendSuccess("Playing card set to " + cardName + " with" +
                    (seed == 0 ? " no seed" : " seed " + seed), worldName);
            return true;
        }
        sendFailed("No card named '" + cardName + "' was found!", worldName);
        return false;
    }

    public boolean setCountdown(BingoSettingsBuilder settings, String worldName, String[] extraArguments)
    {
        if (extraArguments.length != 1)
        {
            sendFailed("Expected 3 arguments!", worldName);
            return false;
        }

        boolean enableCountdown = extraArguments[0].equals("true");
        settings.enableCountdown(enableCountdown);
        sendSuccess((enableCountdown ? "Enabled" : "Disabled") + " countdown mode", worldName);
        return true;
    }

    public boolean setDuration(BingoSettingsBuilder settings, String worldName, String[] extraArguments)
    {
        if (extraArguments.length != 1)
        {
            sendFailed("Expected 3 arguments!", worldName);
            return false;
        }

        int gameDuration = toInt(extraArguments[0], 0);
        if (gameDuration > 0)
        {
            settings.countdownGameDuration(gameDuration);
            sendSuccess("Set game duration for countdown mode to " + gameDuration, worldName);
            return true;
        }
        sendFailed("Cannot set duration to " + gameDuration, worldName);
        return true;
    }

    public boolean setPlayerTeam(String worldName, String[] extraArguments)
    {
        if (extraArguments.length != 2)
        {
            sendFailed("Expected 4 arguments!", worldName);
            return false;
        }

        if (!manager.doesSessionExist(worldName))
        {
            sendFailed("Cannot add player to team, world '" + worldName + "' is not a bingo world!", worldName);
            return false;
        }

        BingoSession session = manager.getSession(worldName);
        String playerName = extraArguments[0];
        String teamName = extraArguments[1];

        Player player = Bukkit.getPlayer(playerName);
        if (player == null)
        {
            sendFailed("Cannot add " + playerName + " to team, player does not exist/ is not online!", worldName);
            return false;
        }

        if (teamName.toLowerCase().equals("none"))
        {
            BingoPlayer bPlayer = session.teamManager.getBingoPlayer(player);
            if (bPlayer == null)
            {
                sendFailed(playerName + " did not join any teams!", worldName);
                return false;
            }

            session.teamManager.removePlayerFromTeam(bPlayer);
            sendSuccess("Player " + playerName + " removed from all teams", worldName);
            return true;
        }

        if (!session.teamManager.addPlayerToTeam(player, teamName))
        {
            return false;
        }
        sendSuccess("Player " + playerName + " added to team " + teamName + "", worldName);
        return true;
    }

    public boolean setTeamSize(BingoSettingsBuilder settings, String worldName, String[] extraArguments)
    {
        if (extraArguments.length != 1)
        {
            sendFailed("Expected 3 arguments!", worldName);
            return false;
        }

        int teamSize = Math.min(64, Math.max(1, toInt(extraArguments[0], 1)));

        settings.maxTeamSize(teamSize);
        manager.getSession(worldName).teamManager.setMaxTeamSize(teamSize);
        sendSuccess("Set maximum team size to " + teamSize + " players", worldName);
        return true;
    }

    public boolean setGamemode(BingoSettingsBuilder settings, String worldName, String[] extraArguments)
    {
        if (extraArguments.length == 0)
        {
            sendFailed("Expected at least 3 arguments!", worldName);
            return false;
        }

        try
        {
            settings.mode(BingoGamemode.fromDataString(extraArguments[0]));
            switch (extraArguments[1])
            {
                case "3":
                    settings.cardSize(CardSize.X3);
                    break;
                default:
                    settings.cardSize(CardSize.X5);
                    break;
            }
        }
        catch (IllegalArgumentException e)
        {
            sendFailed("Cannot set gamemode to '" + extraArguments[0] + "', unknown gamemode!", worldName);
            return false;
        }
        BingoSettings view = settings.view();
        sendSuccess("Set gamemode to " + view.mode().name + " " + view.size() + "x" + view.size(), worldName);
        return true;
    }

    public boolean end(String worldName)
    {
        if (manager.endGame(worldName))
        {
            sendFailed("Could not end the game", worldName);
            return false;
        }
        else
        {
            sendSuccess("Game forcefully ended!", worldName);
            return true;
        }
    }

    public boolean preset(BingoSettingsBuilder settings, String worldName, String[] extraArguments)
    {
        if (extraArguments.length != 2)
        {
            sendFailed("Expected 4 arguments!", worldName);
            return false;
        }

        BingoSettingsData settingsData = new BingoSettingsData();

        String path = extraArguments[0];
        if (path.isBlank())
        {
            sendFailed("Please enter a valid preset name", worldName);
            return false;
        }
        if (extraArguments[0] == "save")
        {
            settingsData.saveSettings(path, settings);
        }
        else if (extraArguments[0] == "load")
        {
            manager.getSession(worldName).settingsBuilder.fromOther(settingsData.getSettings(path, manager.getSession(worldName)));
        }

        return true;
    }

    /**
     * @param in
     * @param defaultValue
     * @return Integer the string represents or defaultValue if a conversion failed.
     */
    private int toInt(String in, int defaultValue)
    {
        try
        {
            return Integer.parseInt(in);
        }
        catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }

    private void sendFailed(String message, String worldName)
    {
        Message.log(ChatColor.RED + message, worldName);
    }

    private void sendSuccess(String message, String worldName)
    {
        Message.log(ChatColor.GREEN + message, worldName);
    }
}