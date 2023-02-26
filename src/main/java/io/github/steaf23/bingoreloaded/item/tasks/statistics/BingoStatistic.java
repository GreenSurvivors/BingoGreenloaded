package io.github.steaf23.bingoreloaded.item.tasks.statistics;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@SerializableAs("Bingo.Statistic")
public record BingoStatistic (@NotNull Statistic stat, @Nullable EntityType entityType, @Nullable Material materialType) implements ConfigurationSerializable {

    public BingoStaticStatistic.StatisticCategory getCategory() {
        return BingoStaticStatistic.determineStatCategory(stat);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("statistic", stat.name());
        result.put("entity", entityType == null ? "" : entityType.name());
        result.put("item", materialType == null ? "" : materialType.name());
        return result;
    }

    public static BingoStatistic deserialize(@NotNull Map<String, Object> data) {
        Statistic stat = Statistic.valueOf((String)data.get("statistic"));

        String entityStr = (String) data.getOrDefault("entity", null);
        EntityType entity = null;
        if (entityStr != null && !entityStr.isEmpty())
            entity = EntityType.valueOf((String)data.get("entity"));

        String materialStr = (String) data.getOrDefault("item", null);
        Material material = null;
        if (materialStr != null && !materialStr.isEmpty())
            material = Material.valueOf((String)data.get("item"));

        return new BingoStatistic(stat, entity, material);
    }

    public boolean hasMaterialComponent()
    {
        return materialType != null;
    }

    public boolean hasEntityComponent()
    {
        return entityType != null;
    }
}
