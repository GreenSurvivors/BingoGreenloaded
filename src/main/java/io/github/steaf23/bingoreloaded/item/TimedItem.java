package io.github.steaf23.bingoreloaded.item;

import io.github.steaf23.bingoreloaded.MessageSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class TimedItem
{
    public final InventoryItem item;
    private final long maxCooldownTime;
    private final Map<String, Long> playerTimes = new HashMap<>();

    public TimedItem(InventoryItem stack, long maxCooldownTime)
    {
        this.item = stack;
        this.maxCooldownTime = maxCooldownTime;
    }

    public boolean tryUse(Player player)
    {
        String name = player.getName();
        boolean success = false;

        if (!playerTimes.containsKey(name))
        {
            playerTimes.put(name, System.currentTimeMillis());
            use(player);
            success = true;
        }
        else if (playerTimes.get(name) + maxCooldownTime < System.currentTimeMillis())
        {
            playerTimes.put(name, System.currentTimeMillis());
            use(player);
            success = true;
        }

        if (! success)
        {
            double seconds = getTimeLeft(player);
            MessageSender.sendPlayer("game.item.cooldown", player, "" + seconds);
        }

        return success;
    }

    public abstract void use(Player player);

    public double getTimeLeft(Player player)
    {
        return ((playerTimes.get(player.getName()) + maxCooldownTime) - System.currentTimeMillis()) / 1000.0;
    }
}
