package io.github.steaf23.bingoreloaded.gui.cards;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.item.tasks.AbstractBingoTask;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.BingoGame;
import io.github.steaf23.bingoreloaded.player.BingoTeam;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class CompleteBingoCard extends BingoCard
{
    public CompleteBingoCard(CardSize size, BingoGame game)
    {
        super(size, game);
        InventoryItem item = new InventoryItem(0, Material.MAP, TranslationData.itemName("menu.card.info_complete"), TranslationData.itemDescription("menu.card.info_complete"));
        addOption(item);
    }

    @Override
    public boolean hasBingo(BingoTeam team)
    {
        return getCompleteCount(team) == size.fullCardSize;
    }

    @Override
    public CompleteBingoCard copy()
    {
        CompleteBingoCard card = new CompleteBingoCard(this.size, game);
        List<AbstractBingoTask> newTasks = new ArrayList<>();
        for (AbstractBingoTask item : tasks)
        {
            newTasks.add(item.copy());
        }
        card.tasks = newTasks;
        return card;
    }
}
