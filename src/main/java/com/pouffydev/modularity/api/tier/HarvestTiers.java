package com.pouffydev.modularity.api.tier;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.common.tools.data.StatsData;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HarvestTiers {
    private static final Map<Tier, Component> harvestLevelNames = Maps.newHashMap();

    private static MutableComponent makeLevelKey(Tier tier) {
        String key = Objects.requireNonNull(TierSortingRegistry.getName(tier)).toLanguageKey("harvest_tier");
        return Component.translatable("stat", key).withStyle(style -> style);
    }

    public static Component getName(Tier tier) {
        return harvestLevelNames.computeIfAbsent(tier, n ->  makeLevelKey(tier));
    }

    public static Tier max(Tier a, Tier b) {
        List<Tier> sorted = TierSortingRegistry.getSortedTiers();
        if (sorted.indexOf(b) > sorted.indexOf(a)) {
            return b;
        }
        return a;
    }

    public static Tier min(Tier a, Tier b) {
        List<Tier> sorted = TierSortingRegistry.getSortedTiers();
        if (sorted.indexOf(b) < sorted.indexOf(a)) {
            return b;
        }
        return a;
    }

    public static Tier minTier() {
        List<Tier> sortedTiers = TierSortingRegistry.getSortedTiers();
        if (sortedTiers.isEmpty()) {
            Modularity.LOGGER.error("No sorted tiers exist, this should not happen");
            return Tiers.WOOD;
        }
        return sortedTiers.getFirst();
    }

    public static final Codec<Tier> TIER_CODEC = ResourceLocation.CODEC.comapFlatMap(
            (location) -> {
                DataResult<Tier> result;
                Tier tier = TierSortingRegistry.byName(location);
                if (tier != null) {
                    result = DataResult.success(tier);
                } else {
                    result = DataResult.error(() -> "Not a sorted tier: " + location.toString());
                }
                return result;
            }, TierSortingRegistry::getName);
}
