package io.github.steaf23.bingoreloaded.data;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.util.Message;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class InventoryData {
    private final static String
            INVENTORY = "inventory",
            ENDERCHEST = "enderchest",
            STATS = "stats",
            EXP = "xp",
            LEVEL = "level",
            HEALTH = "health",
            HUNGER = "hunger",
            GAMEMODE = "gamemode",
            ATTRIBUTES = "attributes",
            ATTRIBUTE_BASE_VALUE = "base",
            ATTRIBUTE_MODIFIERS = "modifiers",
            ATTRIBUTE_TYPE = "type",
            ACTIVE_INVENTORY = "activeInventory";

    private static InventoryData instance;

    private final ItemStack[] defaultInventory = new ItemStack[InventoryType.PLAYER.getDefaultSize()];
    private final ItemStack[] defaultEnderInv = new ItemStack[InventoryType.ENDER_CHEST.getDefaultSize()];
    private float defaultExp = 0.0f;
    private int defaultLevel = 0;
    private float defaultHealth = 20.0f;
    private int defaultFood = 20;
    private String defaultGameModeStr = GameMode.ADVENTURE.name();
    private String defaultIdentifier = "default";

    public static InventoryData inst() {
        if (instance == null)
            instance = new InventoryData();
        return instance;
    }

    /**
     * Combining all arguments to a single FileConfiguration key.
     * @param args key arguments in order
     * @return String
     */
    private static @NotNull String buildKey(String... args) {
        return String.join(".", args);
    }

    public void setDefaults(ItemStack inventoryContent, float exp, int level, float health, int food, String gameModeStr, String identifier) {
        Arrays.fill(defaultInventory, inventoryContent);
        Arrays.fill(defaultEnderInv, inventoryContent);

        this.defaultExp = exp;
        this.defaultLevel = level;

        this.defaultHealth = health;
        this.defaultFood = food;
        this.defaultGameModeStr = gameModeStr;
        this.defaultIdentifier = identifier;
    }

    public String getDefaultIdentifier() {
        return defaultIdentifier;
    }

    /**
     * Saves the inventory of a player
     * @param player     player whose inventory is about to be saved
     * @param identifier the identifier what inventory should be saved.
     */
    public void savePlayerData(Player player, String identifier) {
        String fileName = "saved_inventories" + File.separator + player.getUniqueId() + ".yml";
        File file = new File(BingoReloaded.get().getDataFolder(), fileName);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        //saves the inventory, enderchest and states of a player under the identifier
        cfg.set(buildKey(identifier, INVENTORY), player.getInventory().getContents());
        cfg.set(buildKey(identifier, ENDERCHEST), player.getEnderChest().getContents());
        cfg.set(buildKey(identifier, STATS, EXP), player.getExp());
        cfg.set(buildKey(identifier, STATS, LEVEL), player.getLevel());
        cfg.set(buildKey(identifier, STATS, HEALTH), player.getHealth());
        cfg.set(buildKey(identifier, STATS, HUNGER), player.getFoodLevel());
        cfg.set(buildKey(identifier, GAMEMODE), player.getGameMode().name());

        //save attributes
        cfg.set(buildKey(identifier, ATTRIBUTES), Arrays.stream(Attribute.values()).map(attribute -> {
            AttributeInstance attributeInstance = player.getAttribute(attribute);
            if (attributeInstance != null) {
                Map<String, Object> attributeMap = new HashMap<>();
                attributeMap.put(ATTRIBUTE_TYPE, attribute.name());
                attributeMap.put(ATTRIBUTE_BASE_VALUE, attributeInstance.getBaseValue());
                attributeMap.put(ATTRIBUTE_MODIFIERS, attributeInstance.getModifiers().stream().map(AttributeModifier::serialize).toList());

                return attributeMap;
            } else {
                return new HashMap<>();
            }
        }).filter(s -> !s.isEmpty()).toList());

        // save modified configuration
        cfg.options().setHeader(Collections.singletonList(String.format(
                fileName.replace(".yml", "").replace("_", " ") + " configuration for %s (%s)",
                BingoReloaded.get().getName(),
                BingoReloaded.get().getDescription().getVersion())));
        cfg.options().parseComments(true);
        try {
            cfg.save(file);
        } catch (IOException e) {
            Message.log("Could not save " + fileName + " configuration file. " + e.getMessage());
        }
    }

    /**
     * loads the inventory and stats of a player, depending on the identifier
     * @param player     player whose inventory is about to be loaded
     * @param identifier the identifier what inventory should be loaded.
     */
    public void loadPlayerData(Player player, String identifier) {
        String fileName = "saved_inventories" + File.separator + player.getUniqueId() + ".yml";
        File file = new File(BingoReloaded.get().getDataFolder(), fileName);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        //set active
        cfg.set(ACTIVE_INVENTORY, identifier);

        // save modified configuration
        cfg.options().setHeader(Collections.singletonList(String.format(
                fileName.replace(".yml", "").replace("_", " ") + " configuration for %s (%s)",
                BingoReloaded.get().getName(),
                BingoReloaded.get().getDescription().getVersion())));
        cfg.options().parseComments(true);
        try {
            cfg.save(file);
        } catch (IOException e) {
            Message.log("Could not save " + fileName + " configuration file. " + e.getMessage());
        }

        List<?> inventoryListLoaded = cfg.getList(buildKey(identifier, INVENTORY));
        List<?> enderListLoaded = cfg.getList(buildKey(identifier, ENDERCHEST));

        if (inventoryListLoaded == null) {
            player.getInventory().setContents(defaultInventory);
        } else {
            List<ItemStack> inventoryList = new ArrayList<>();

            for (Object obj : inventoryListLoaded) {
                if (obj instanceof ItemStack || obj == null) {
                    inventoryList.add((ItemStack) obj);
                }
            }

            player.getInventory().setContents(inventoryList.toArray(new ItemStack[0]));
        }

        if (enderListLoaded == null) {
            player.getEnderChest().setContents(defaultEnderInv);
        } else {
            List<ItemStack> enderList = new ArrayList<>();

            for (Object obj : enderListLoaded) {
                if (obj instanceof ItemStack || obj == null) {
                    enderList.add((ItemStack) obj);
                }
            }

            player.getEnderChest().setContents(enderList.toArray(new ItemStack[0]));
        }

        player.updateInventory();

        player.setExp(Math.max(0, (float) cfg.getDouble(buildKey(identifier, STATS, EXP), defaultExp)));
        player.setLevel(Math.max(0, cfg.getInt(buildKey(identifier, STATS, LEVEL), defaultLevel)));
        player.setHealth(Math.max(0, Math.min(defaultHealth, cfg.getDouble(buildKey(identifier, STATS, HEALTH), defaultHealth))));
        player.setFoodLevel(Math.max(0, Math.min(defaultFood, cfg.getInt(buildKey(identifier, STATS, HUNGER), defaultFood))));

        String GameModeStr = cfg.getString(buildKey(identifier, GAMEMODE));
        GameMode gameMode = GameMode.valueOf(defaultGameModeStr);

        if (GameModeStr != null && Arrays.stream(GameMode.values()).map(Enum::name).anyMatch(GameModeStr::equals)) {
            gameMode = GameMode.valueOf(GameModeStr);
        }

        player.setGameMode(gameMode);
    }

    /**
     * loads the inventory identifier a player
     * @param player player whose inventory identifier is about to be loaded
     */
    public String loadIdentifier(Player player) {
        File file = new File(BingoReloaded.get().getDataFolder(), "saved_inventories" + File.separator + player.getUniqueId() + ".yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        //get active inventory identifier
        return cfg.getString(ACTIVE_INVENTORY, defaultIdentifier);
    }
}
