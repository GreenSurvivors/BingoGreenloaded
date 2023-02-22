package io.github.steaf23.bingoreloaded.gui;

import io.github.steaf23.bingoreloaded.BingoSettings;
import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.util.GUIPreset5x9;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ExtraBingoMenu extends MenuInventory
{

    private static final int TEAM_MAX = 64;
    private static final int DURATION_MAX = 60;
    private final BingoSettings settings;
    private final InventoryItem exit = new InventoryItem(36,
            Material.BARRIER, TITLE_PREFIX + TranslationData.translate("menu.prev"));
    private final InventoryItem maxTeamMembers = new InventoryItem(GUIPreset5x9.THREE_CENTER.positions[0],
            Material.ENDER_EYE, TITLE_PREFIX + "Maximum Team Size");
    private final InventoryItem countdown = new InventoryItem(GUIPreset5x9.THREE_CENTER.positions[1],
            Material.CLOCK, TITLE_PREFIX + "Enable Countdown Timer");
    private final InventoryItem gameDuration = new InventoryItem(GUIPreset5x9.THREE_CENTER.positions[2],
            Material.RECOVERY_COMPASS, TITLE_PREFIX + "Countdown Duration");

    public ExtraBingoMenu(MenuInventory parent, BingoSettings settings)
    {
        super(45, TranslationData.translate("menu.options.title"), parent);
        this.settings = settings;
        var meta = countdown.getItemMeta();
        countdown.highlight(settings.enableCountdown);
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (slotClicked == exit.getSlot())
        {
            close(player);
        }
        else if (slotClicked == maxTeamMembers.getSlot())
        {
            if (clickType == ClickType.LEFT)
            {
                maxTeamMembers.setAmount(Math.min(TEAM_MAX, maxTeamMembers.getAmount() + 1));
            }
            else if (clickType == ClickType.RIGHT)
            {
                maxTeamMembers.setAmount(Math.max(1, maxTeamMembers.getAmount() - 1));
            }

            maxTeamMembers.setDescription(
                    "§7" + maxTeamMembers.getAmount() + " Players per team",
                    "§rUse the mouse buttons to increase/ decrease",
                    "the maximum amount of players per team");
            addOption(maxTeamMembers);
        }
        else if (slotClicked == countdown.getSlot())
        {
            settings.enableCountdown = !settings.enableCountdown;
            countdown.highlight(settings.enableCountdown);
            addOption(countdown);
        }
        else if (slotClicked == gameDuration.getSlot())
        {
            if (clickType == ClickType.LEFT)
            {
                gameDuration.setAmount(Math.min(DURATION_MAX, gameDuration.getAmount() + 1));
            }
            else if (clickType == ClickType.RIGHT)
            {
                gameDuration.setAmount(Math.max(1, gameDuration.getAmount() - 1));
            }

            gameDuration.setDescription(
                    "§7Timer set to " + gameDuration.getAmount() + " minute(s) for Countdown Bingo",
                    "§rUse the mouse buttons to increase/ decrease",
                    "the amount of minutes that Countdown bingo will last.");
            addOption(gameDuration);
        }
    }

    @Override
    public void handleClose(final InventoryCloseEvent event)
    {
        settings.maxTeamSize = maxTeamMembers.getAmount();
        settings.countdownGameDuration = gameDuration.getAmount();
        super.handleClose(event);
    }

    @Override
    public void handleOpen(InventoryOpenEvent event)
    {
        super.handleOpen(event);
        maxTeamMembers.setAmount(Math.max(0, Math.min(TEAM_MAX, settings.maxTeamSize)));
        maxTeamMembers.setDescription(
                "§7" + maxTeamMembers.getAmount() + " Players per team",
                "§r§oUse the mouse buttons to increase/ decrease",
                "the maximum amount of players per team");

        gameDuration.setAmount(Math.max(0, Math.min(DURATION_MAX, settings.countdownGameDuration)));
        gameDuration.setDescription(
                "§7Timer set to " + gameDuration.getAmount() + " minute(s) for Countdown Bingo",
                "§r§oUse the mouse buttons to increase/ decrease",
                "the amount of minutes that Countdown bingo will last.");

        fillOptions(
                exit,
                maxTeamMembers,
                gameDuration,
                countdown
        );
    }
}
