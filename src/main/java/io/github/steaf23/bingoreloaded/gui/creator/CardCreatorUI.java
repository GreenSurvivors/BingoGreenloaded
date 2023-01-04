package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.data.BingoCardsData;
import io.github.steaf23.bingoreloaded.data.BingoTasksData;
import io.github.steaf23.bingoreloaded.gui.AbstractGUIInventory;
import io.github.steaf23.bingoreloaded.gui.FilterType;
import io.github.steaf23.bingoreloaded.gui.KeyboardUI;
import io.github.steaf23.bingoreloaded.gui.ListPickerUI;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

// This class is used to navigate through the cards and lists.
// Uses a double ListPicker, one for cards and one for lists.
public class CardCreatorUI extends AbstractGUIInventory
{
    public static final InventoryItem CARD = new InventoryItem(11, Material.FILLED_MAP, TITLE_PREFIX + "Edit Cards", "Click to view and edit bingo cards!");
    public static final InventoryItem LIST = new InventoryItem(15, Material.PAPER, TITLE_PREFIX + "Edit Lists", "Click to view and edit bingo lists!");

    public CardCreatorUI(AbstractGUIInventory parent)
    {
        super(27, "Card Creator", parent);
        fillOptions(new InventoryItem[]{CARD, LIST});
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (slotClicked == CARD.getSlot())
        {
            List<InventoryItem> items = new ArrayList<>();
            for (String card : BingoCardsData.getCardNames())
            {
                InventoryItem item = new InventoryItem(Material.FILLED_MAP, card);
                items.add(item);
            }

            ListPickerUI cardPicker = new ListPickerUI(items, "Choose A Card", this, FilterType.DISPLAY_NAME)
            {
                private static final InventoryItem CREATE_CARD = new InventoryItem(51, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "New Card");

                @Override
                public void open(HumanEntity player)
                {
                    fillOptions(new InventoryItem[]{CREATE_CARD});
                    super.open(player);
                }

                @Override
                public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
                {
                    if (slotClicked == CREATE_CARD.getSlot())
                    {
                        createCard(player);
                    }
                    super.delegateClick(event, slotClicked, player, clickType);
                }

                @Override
                public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
                {
                    openCardEditor(clickedOption.getItemMeta().getDisplayName(), player);
                }
            };
            cardPicker.open(player);
        }
        else if (slotClicked == LIST.getSlot())
        {
            List<InventoryItem> items = new ArrayList<>();
            for (String list : BingoTasksData.getListNames())
            {
                InventoryItem item = new InventoryItem(Material.PAPER, list,"This list contains " + BingoTasksData.getTaskCount(list) + " tasks");
                items.add(item);
            }

            ListPickerUI listPicker = new ListPickerUI(items, "Choose A List", this, FilterType.DISPLAY_NAME)
            {
                private static final InventoryItem CREATE_LIST = new InventoryItem(51, Material.EMERALD, "" + ChatColor.GREEN + ChatColor.BOLD + "New List");

                @Override
                public void open(HumanEntity player)
                {
                    fillOptions(new InventoryItem[]{CREATE_LIST});
                    super.open(player);
                }

                @Override
                public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
                {
                    if (slotClicked == CREATE_LIST.getSlot())
                    {
                        createList(player);
                    }
                    super.delegateClick(event, slotClicked, player, clickType);
                }

                @Override
                public void onOptionClickedDelegate(InventoryClickEvent event, InventoryItem clickedOption, Player player)
                {
                    openListEditor(clickedOption.getItemMeta().getDisplayName(), player);
                }
            };
            listPicker.open(player);
        }
    }

    private void openCardEditor(String cardName, Player player)
    {
        CardEditorUI editor = new CardEditorUI(cardName, this);
        editor.open(player);
    }

    private void openListEditor(String listName, Player player)
    {
        ListEditorUI editor = new ListEditorUI(listName, this);
        editor.openItemPicker(player);
    }

    public void createCard(Player player)
    {
        final String name;
        KeyboardUI keys = new KeyboardUI(this)
        {
            @Override
            public void storeResult()
            {
                if (!getKeyword().isBlank())
                    openCardEditor(getKeyword().toLowerCase(), player);
            }
        };
        keys.open(player);
    }

    public void createList(Player player)
    {
        final String name;
        KeyboardUI keys = new KeyboardUI(this)
        {
            @Override
            public void storeResult()
            {
                if (!getKeyword().isBlank())
                    openListEditor(getKeyword().toLowerCase(), player);
            }
        };
        keys.open(player);
    }
}