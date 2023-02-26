package io.github.steaf23.bingoreloaded.item.tasks;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.stream.Stream;

public final class BingoStaticStatistic {
    private static final Set<EntityType> validEntityTypes = new HashSet<>();

    public enum StatisticCategory {
        TRAVEL,
        BLOCK_INTERACT,
        CONTAINER_INTERACT,
        DAMAGE,
        ROOT_STATISTIC,
        OTHER,
    }

    public BingoStaticStatistic() {
        Stream<Material> mats = Arrays.stream(Material.values())
                .filter(mat -> mat.name().contains("_SPAWN_EGG"));
        mats.forEach(mat -> {
            if (mat.name().equals("MOOSHROOM_SPAWN_EGG"))
            {
                validEntityTypes.add(EntityType.MUSHROOM_COW);
            }
            else
            {
                validEntityTypes.add(EntityType.valueOf(mat.name().replace("_SPAWN_EGG", "")));
            }
        });
        validEntityTypes.add(EntityType.ENDER_DRAGON);
        validEntityTypes.add(EntityType.IRON_GOLEM);
        validEntityTypes.add(EntityType.SNOWMAN);
    }

    public static List<Statistic> getStatisticsOfCategory(StatisticCategory category) {
        List<Statistic> result = new ArrayList<>();
        for (var stat : Statistic.values()) {
            if (determineStatCategory(stat) == category) {
                result.add(stat);
            }
        }
        return result;
    }

    public static boolean isEntityValidForStatistic(EntityType type) {
        return validEntityTypes.contains(type);
    }

    public static StatisticCategory determineStatCategory(Statistic stat) {
        return switch (stat)
                {
                    case DROP,
                            PICKUP,
                            USE_ITEM,
                            BREAK_ITEM,
                            CRAFT_ITEM,
                            KILL_ENTITY,
                            ENTITY_KILLED_BY,
                            MINE_BLOCK -> StatisticCategory.ROOT_STATISTIC;

                    case DAMAGE_DEALT,
                            DAMAGE_TAKEN,
                            DAMAGE_DEALT_ABSORBED,
                            DAMAGE_DEALT_RESISTED,
                            DAMAGE_RESISTED,
                            DAMAGE_ABSORBED,
                            DAMAGE_BLOCKED_BY_SHIELD -> StatisticCategory.DAMAGE;

                    case TALKED_TO_VILLAGER,
                            TRADED_WITH_VILLAGER,
                            DEATHS,
                            MOB_KILLS,
                            PLAYER_KILLS,
                            FISH_CAUGHT,
                            ANIMALS_BRED,
                            LEAVE_GAME,
                            JUMP,
                            DROP_COUNT,
                            PLAY_ONE_MINUTE,
                            TOTAL_WORLD_TIME,
                            SNEAK_TIME,
                            TIME_SINCE_DEATH,
                            RAID_TRIGGER,
                            ARMOR_CLEANED,
                            BANNER_CLEANED,
                            ITEM_ENCHANTED,
                            TIME_SINCE_REST,
                            RAID_WIN,
                            TARGET_HIT,
                            CLEAN_SHULKER_BOX -> StatisticCategory.OTHER;

                    case CAKE_SLICES_EATEN,
                            CAULDRON_FILLED,
                            BREWINGSTAND_INTERACTION,
                            BEACON_INTERACTION,
                            NOTEBLOCK_PLAYED,
                            CAULDRON_USED,
                            NOTEBLOCK_TUNED,
                            FLOWER_POTTED,
                            RECORD_PLAYED,
                            FURNACE_INTERACTION,
                            CRAFTING_TABLE_INTERACTION,
                            SLEEP_IN_BED,
                            INTERACT_WITH_BLAST_FURNACE,
                            INTERACT_WITH_SMOKER,
                            INTERACT_WITH_LECTERN,
                            INTERACT_WITH_CAMPFIRE,
                            INTERACT_WITH_CARTOGRAPHY_TABLE,
                            INTERACT_WITH_LOOM,
                            INTERACT_WITH_STONECUTTER,
                            BELL_RING,
                            INTERACT_WITH_ANVIL,
                            INTERACT_WITH_GRINDSTONE,
                            INTERACT_WITH_SMITHING_TABLE -> StatisticCategory.BLOCK_INTERACT;

                    case OPEN_BARREL,
                            CHEST_OPENED,
                            ENDERCHEST_OPENED,
                            SHULKER_BOX_OPENED,
                            TRAPPED_CHEST_TRIGGERED,
                            HOPPER_INSPECTED,
                            DROPPER_INSPECTED,
                            DISPENSER_INSPECTED -> StatisticCategory.CONTAINER_INTERACT;

                    case STRIDER_ONE_CM,
                            MINECART_ONE_CM,
                            CLIMB_ONE_CM,
                            FLY_ONE_CM,
                            WALK_UNDER_WATER_ONE_CM,
                            BOAT_ONE_CM,
                            PIG_ONE_CM,
                            HORSE_ONE_CM,
                            CROUCH_ONE_CM,
                            AVIATE_ONE_CM,
                            WALK_ONE_CM,
                            WALK_ON_WATER_ONE_CM,
                            SWIM_ONE_CM,
                            FALL_ONE_CM,
                            SPRINT_ONE_CM -> StatisticCategory.TRAVEL;
                };
    }

    public static Material getMaterial(BingoStatistic statistic) {
        return switch (statistic.stat()) {
                    case DAMAGE_DEALT -> Material.DIAMOND_SWORD;
                    case DAMAGE_TAKEN -> Material.IRON_CHESTPLATE;
                    case DEATHS -> Material.SKELETON_SKULL;
                    case MOB_KILLS -> Material.CREEPER_HEAD;
                    case PLAYER_KILLS -> Material.PLAYER_HEAD;
                    case FISH_CAUGHT -> Material.TROPICAL_FISH;
                    case ANIMALS_BRED -> Material.WHEAT;
                    case LEAVE_GAME -> Material.BARRIER;
                    case JUMP -> Material.RABBIT_FOOT;
                    case DROP_COUNT, HOPPER_INSPECTED -> Material.HOPPER;
                    case PLAY_ONE_MINUTE -> Material.CLOCK;
                    case TOTAL_WORLD_TIME -> Material.FILLED_MAP;
                    case WALK_ONE_CM -> Material.LEATHER_BOOTS;
                    case WALK_ON_WATER_ONE_CM -> Material.ICE;
                    case FALL_ONE_CM -> Material.LAVA_BUCKET;
                    case SNEAK_TIME -> Material.SCULK_SHRIEKER;
                    case CLIMB_ONE_CM -> Material.EMERALD_ORE;
                    case FLY_ONE_CM -> Material.COMMAND_BLOCK;
                    case WALK_UNDER_WATER_ONE_CM -> Material.GOLDEN_BOOTS;
                    case MINECART_ONE_CM -> Material.MINECART;
                    case BOAT_ONE_CM -> Material.OAK_BOAT;
                    case PIG_ONE_CM -> Material.CARROT_ON_A_STICK;
                    case HORSE_ONE_CM -> Material.SADDLE;
                    case SPRINT_ONE_CM -> Material.FEATHER;
                    case CROUCH_ONE_CM -> Material.SCULK_SENSOR;
                    case AVIATE_ONE_CM -> Material.ELYTRA;
                    case TIME_SINCE_DEATH -> Material.RECOVERY_COMPASS;
                    case TALKED_TO_VILLAGER -> Material.POPPY;
                    case TRADED_WITH_VILLAGER -> Material.EMERALD;
                    case CAKE_SLICES_EATEN -> Material.CAKE;
                    case CAULDRON_FILLED -> Material.CAULDRON;
                    case CAULDRON_USED -> Material.WATER_BUCKET;
                    case ARMOR_CLEANED -> Material.LEATHER_CHESTPLATE;
                    case BANNER_CLEANED -> Material.WHITE_BANNER;
                    case BREWINGSTAND_INTERACTION -> Material.BREWING_STAND;
                    case BEACON_INTERACTION -> Material.BEACON;
                    case DROPPER_INSPECTED -> Material.DROPPER;
                    case DISPENSER_INSPECTED -> Material.DISPENSER;
                    case NOTEBLOCK_PLAYED, NOTEBLOCK_TUNED -> Material.NOTE_BLOCK;
                    case FLOWER_POTTED -> Material.FLOWER_POT;
                    case TRAPPED_CHEST_TRIGGERED -> Material.TRAPPED_CHEST;
                    case ENDERCHEST_OPENED -> Material.ENDER_CHEST;
                    case ITEM_ENCHANTED -> Material.ENCHANTING_TABLE;
                    case RECORD_PLAYED -> Material.MUSIC_DISC_CAT;
                    case FURNACE_INTERACTION -> Material.FURNACE;
                    case CRAFTING_TABLE_INTERACTION -> Material.CRAFTING_TABLE;
                    case CHEST_OPENED -> Material.CHEST;
                    case SLEEP_IN_BED -> Material.RED_BED;
                    case SHULKER_BOX_OPENED -> Material.SHULKER_BOX;
                    case TIME_SINCE_REST -> Material.YELLOW_BED;
                    case SWIM_ONE_CM -> Material.BUBBLE_CORAL;
                    case DAMAGE_DEALT_ABSORBED -> Material.DAMAGED_ANVIL;
                    case DAMAGE_DEALT_RESISTED -> Material.NETHERITE_SWORD;
                    case DAMAGE_BLOCKED_BY_SHIELD -> Material.SHIELD;
                    case DAMAGE_ABSORBED -> Material.SPONGE;
                    case DAMAGE_RESISTED -> Material.DIAMOND_CHESTPLATE;
                    case CLEAN_SHULKER_BOX -> Material.SHULKER_SHELL;
                    case OPEN_BARREL -> Material.BARREL;
                    case INTERACT_WITH_BLAST_FURNACE -> Material.BLAST_FURNACE;
                    case INTERACT_WITH_SMOKER -> Material.SMOKER;
                    case INTERACT_WITH_LECTERN -> Material.LECTERN;
                    case INTERACT_WITH_CAMPFIRE -> Material.CAMPFIRE;
                    case INTERACT_WITH_CARTOGRAPHY_TABLE -> Material.CARTOGRAPHY_TABLE;
                    case INTERACT_WITH_LOOM -> Material.LOOM;
                    case INTERACT_WITH_STONECUTTER -> Material.STONECUTTER;
                    case BELL_RING -> Material.BELL;
                    case RAID_TRIGGER -> Material.CROSSBOW;
                    case RAID_WIN -> Material.TOTEM_OF_UNDYING;
                    case INTERACT_WITH_ANVIL -> Material.ANVIL;
                    case INTERACT_WITH_GRINDSTONE -> Material.GRINDSTONE;
                    case TARGET_HIT -> Material.TARGET;
                    case INTERACT_WITH_SMITHING_TABLE -> Material.SMITHING_TABLE;
                    case STRIDER_ONE_CM -> Material.WARPED_FUNGUS_ON_A_STICK;
                    case DROP,
                            PICKUP,
                            MINE_BLOCK,
                            USE_ITEM,
                            BREAK_ITEM,
                            CRAFT_ITEM,
                            KILL_ENTITY,
                            ENTITY_KILLED_BY -> rootStatMaterial(statistic);
                };
    }

    private static Material rootStatMaterial(BingoStatistic statistic)
    {
        if (statistic.materialType() != null &&
                (statistic.stat().getType() == Statistic.Type.ITEM || statistic.stat().getType() == Statistic.Type.BLOCK))
        {
            return statistic.materialType();
        }
        else if (statistic.entityType() != null &&
                statistic.stat().getType() == Statistic.Type.ENTITY)
        {
            if (statistic.entityType() == EntityType.MUSHROOM_COW)
            {
                return Material.MOOSHROOM_SPAWN_EGG;
            }
            else if (statistic.entityType() == EntityType.IRON_GOLEM)
            {
                return Material.POPPY;
            }
            else if (statistic.entityType() == EntityType.SNOWMAN)
            {
                return Material.CARVED_PUMPKIN;
            }
            else if (statistic.entityType() == EntityType.ENDER_DRAGON)
            {
                return Material.DRAGON_EGG;
            }

            return Material.valueOf(statistic.entityType().name() + "_SPAWN_EGG");
        }

        return Material.GLOBE_BANNER_PATTERN;
    }

}
