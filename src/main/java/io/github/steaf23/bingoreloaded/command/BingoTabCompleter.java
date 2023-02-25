package io.github.steaf23.bingoreloaded.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BingoTabCompleter implements TabCompleter
{
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args)
    {
        if (!(sender instanceof Player player) || player.hasPermission("bingo.admin"))
        {
            switch (args.length)
            {
                case 1 -> {
                    return List.of("join", "getcard", "back", "leave", "stats", "end", "kit", "creator", "deathmatch");
                }
                case 2 -> {
                    switch (args[0])
                    {
                        case "kit" -> {
                            return List.of("add", "remove", "item");
                        }
                    }
                }
                case 3 -> {
                    switch (args[1])
                    {
                        case "add", "remove" -> {
                            return List.of("1", "2", "3", "4", "5");
                        }
                        case "item" -> {
                            return List.of("wand");
                        }
                    }
                }
            }
        }

        switch (args.length)
        {
            case 1 -> {
                return List.of("join", "getcard", "back", "leave", "stats");
            }
        }
        return null;
    }
}
