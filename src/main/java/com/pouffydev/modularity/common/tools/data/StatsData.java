package com.pouffydev.modularity.common.tools.data;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.common.tools.parts.stat.NumericPartStat;
import com.pouffydev.modularity.common.tools.parts.stat.PartStat;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record StatsData(Map<PartStat<?>,Object> stats) {

    public static final Codec<StatsData> CODEC = Codec.PASSTHROUGH.comapFlatMap((dynamic) -> {
        Tag tag = dynamic.convert(NbtOps.INSTANCE).getValue();
        DataResult<StatsData> result;
        if (tag instanceof CompoundTag compoundtag) {
            result = DataResult.success(compoundtag == dynamic.getValue() ? StatsData.readFromNBT(compoundtag.copy()) : StatsData.readFromNBT(compoundtag));
        } else {
            result = DataResult.error(() -> "Not a compound tag: " + tag);
        }
        return result;
    }, (statsData) -> new Dynamic<>(NbtOps.INSTANCE, statsData.serializeToNBT()));

    public static final StreamCodec<ByteBuf, StatsData> STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG.map(StatsData::readFromNBT, StatsData::serializeToNBT);

    static final Set<String> ERRORED_IDS = new HashSet<>();
    public static final StatsData EMPTY = new StatsData(ImmutableMap.of());

    public static Builder builder() {
        return new Builder();
    }
    public static Builder builder(Map<PartStat<?>,Object> existingStats) {
        return new Builder(existingStats);
    }

    public StatsData(Map<PartStat<?>,Object> stats) {
        this.stats = stats;
    }

    public ImmutableMap<PartStat<?>,Object> getStats() {
        return ImmutableMap.copyOf(stats);
    }
    public Set<PartStat<?>> getContainedStats() {
        return stats.keySet();
    }

    public boolean hasStat(PartStat<?> stat) {
        return stats.containsKey(stat);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(PartStat<T> stat) {
        return (T)stats.getOrDefault(stat, stat.getDefaultValue());
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(PartStat<T> stat, T fallback) {
        if (hasStat(stat)) {
            return (T)stats.get(stat);
        }
        return fallback;
    }

    @Nullable
    static PartStat<?> readStatIdFromNBT(String name) {
        ResourceLocation statName = ResourceLocation.tryParse(name);
        if (statName != null) {
            PartStat<?> stat = ModularityRegistries.PART_STAT_REGISTRY.get(statName);
            if (stat != null) {
                return stat;
            }
        }
        if (!StatsData.ERRORED_IDS.contains(name)) {
            StatsData.ERRORED_IDS.add(name);
            Modularity.LOGGER.error("Ignoring unknown stat " + name + " in tool stat data");
        }
        return null;
    }

    public static StatsData readFromNBT(@Nullable CompoundTag nbt) {
        if (nbt == null) {
            return EMPTY;
        }
        ImmutableMap.Builder<PartStat<?>, Object> builder = ImmutableMap.builder();
        // simply try each key as a tool stat
        for (String key : nbt.getAllKeys()) {
            Tag tag = nbt.get(key);
            if (tag != null) {
                PartStat<?> stat = readStatIdFromNBT(key);
                if (stat != null) {
                    Object value = stat.read(tag);
                    if (value != null) {
                        builder.put(stat, value);
                    }
                }
            }
        }
        return new StatsData(builder.build());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <T> Tag serialize(PartStat<T> stat, Object value) {
        return stat.write((T) value);
    }

    public CompoundTag serializeToNBT() {
        CompoundTag nbt = new CompoundTag();
        for (Map.Entry<PartStat<?>,Object> entry : stats.entrySet()) {
            PartStat<?> stat = entry.getKey();
            Tag serialized = serialize(stat, entry.getValue());
            if (serialized != null) {
                nbt.put(stat.getName().toString(), serialized);
            }
        }
        return nbt;
    }

    @SuppressWarnings("unchecked")
    private static <T> void toNetwork(FriendlyByteBuf buffer, PartStat<T> stat, Object value) {
        stat.toNetwork(buffer, (T) value);
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        buffer.writeVarInt(stats.size());
        for (Map.Entry<PartStat<?>,Object> entry : stats.entrySet()) {
            PartStat<?> stat = entry.getKey();
            buffer.writeUtf(stat.getName().toString());
            toNetwork(buffer, stat, entry.getValue());
        }
    }

    public static StatsData fromNetwork(RegistryFriendlyByteBuf buffer) {
        ImmutableMap.Builder<PartStat<?>, Object> builder = ImmutableMap.builder();
        int max = buffer.readVarInt();
        for (int i = 0; i < max; i++) {
            PartStat<?> stat = ByteBufCodecs.holderRegistry(ModularityRegistries.PART_STAT).decode(buffer).value();
            builder.put(stat, stat.fromNetwork(buffer));
        }
        return new StatsData(builder.build());
    }

    public static class Builder {
        private final Map<PartStat<?>, Object> builder = new HashMap<>();
        public Builder() {

        }

        public Builder(Map<PartStat<?>,Object> existingStats) {
            super();
            builder.putAll(existingStats);
        }

        public <T> Builder set(PartStat<T> stat, T value) {
            builder.remove(stat);
            builder.put(stat, stat.clamp(value));
            return this;
        }

        public Builder set(NumericPartStat<Float> stat, float value) {
            return set(stat, (Float)value);
        }

        public StatsData build() {
            if (builder.isEmpty()) {
                return EMPTY;
            }
            return new StatsData(builder);
        }
    }
}
