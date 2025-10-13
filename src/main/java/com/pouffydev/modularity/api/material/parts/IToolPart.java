package com.pouffydev.modularity.api.material.parts;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.common.tools.data.StatsData;
import com.pouffydev.modularity.common.tools.parts.stat.IPartStat;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.openjdk.nashorn.api.tree.SwitchTree;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface IToolPart {
    Codec<IToolPart> DIRECT_CODEC = Codec.lazyInitialized(ModularityRegistries.TOOL_PART_TYPE_REGISTRY::byNameCodec)
            .dispatch(IToolPart::getType, ToolPartType::codec);

    ToolPartType<?> getType();

    default void statsTooltip(Holder<ToolMaterial> material, Consumer<Component> tooltipComponents, boolean advanced) {}

    default boolean canRepair() {
        return isMainPart();
    }

    default boolean isMainPart() {
        return false;
    };

    List<StatModifier<?>> getStats();

    class StatModifier<T> {
        public final ModifyType modifyType;
        public final Pair<IPartStat<T>, T> value;

        public StatModifier(ModifyType modifyType, IPartStat<T> stat, T value) {
            this.modifyType = modifyType;
            this.value = Pair.of(stat, value);
        }

        public static <T> StatModifier<T> bonus(IPartStat<T> stat, T value) {
            return new StatModifier<>(ModifyType.BONUS, stat, value);
        }
        public static <T> StatModifier<T> multiplier(IPartStat<T> stat, T value) {
            return new StatModifier<>(ModifyType.MULTIPLIER, stat, value);
        }
        public static <T> StatModifier<T> percentBoost(IPartStat<T> stat, T value) {
            return new StatModifier<>(ModifyType.PERCENT_BOOST, stat, value);
        }
        public static <T> StatModifier<T> override(IPartStat<T> stat, T value) {
            return new StatModifier<>(ModifyType.OVERRIDE, stat, value);
        }

        public T apply(T original) {
            return value.getFirst().modify(modifyType, original, value.getSecond());
        }

        public void write(StatsData stats, StatsData.Builder toolStatsBuilder) {
            var stat = value.getFirst();
            T original = stats.getOrDefault(stat, stat.getDefaultValue());
            toolStatsBuilder.set(stat, apply(original));
        }

        public boolean test(IPartStat<?> stat) {
            return stat.equals(this.value.getFirst());
        }
    }

    enum ModifyType {
        BONUS, MULTIPLIER, PERCENT_BOOST, OVERRIDE
    }
}
