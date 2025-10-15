package com.pouffydev.modularity.common.tools.parts.main;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.IRepairableToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tier.HarvestTiers;
import com.pouffydev.modularity.api.tool.SerializableTier;
import com.pouffydev.modularity.common.registry.ModulaPartStats;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import com.pouffydev.modularity.common.util.TooltipUtils;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Tier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record AxeHead(int durability, float miningSpeed, Tier tier, float attack) implements IRepairableToolPart {

    public static AxeHead EMPTY = new AxeHead(0, 0f, SerializableTier.EMPTY, 0f);

    public static final MapCodec<AxeHead> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("durability", 0).forGetter(AxeHead::durability),
                    Codec.FLOAT.optionalFieldOf("speed", 0f).forGetter(AxeHead::miningSpeed),
                    HarvestTiers.TIER_CODEC.fieldOf("tier").forGetter(AxeHead::tier),
                    ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("attack", 0f).forGetter(AxeHead::attack)
            ).apply(instance, AxeHead::new));

    public static final StreamCodec<FriendlyByteBuf, AxeHead> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AxeHead::durability,
            ByteBufCodecs.FLOAT, AxeHead::miningSpeed,
            HarvestTiers.TIER_STREAM_CODEC, AxeHead::tier,
            ByteBufCodecs.FLOAT, AxeHead::attack,
            AxeHead::new
    );

    @Override
    public ToolPartType<?> getType() {
        return ModulaToolParts.AXE_HEAD.get();
    }

    @Override
    public void statsTooltip(Holder<ToolMaterial> material, Consumer<Component> tooltip, boolean advanced) {
        tooltip.accept(TooltipUtils.part(material, ModulaToolParts.AXE_HEAD));
        var materialStats = material.value().stats();
        if (!materialStats.supportsType(getType())) return;
        for(StatModifier<?> modifier : getStats()) {
            tooltip.accept(TooltipUtils.indent(2, modifier.format()));
        }
    }


    @Override
    public boolean isMainPart() {
        return true;
    }

    @Override
    public List<StatModifier<?>> getStats() {
        var modifiers = new ArrayList<StatModifier<?>>();
        modifiers.add(StatModifier.bonus(ModulaPartStats.DURABILITY.get(), (float)durability));
        modifiers.add(StatModifier.bonus(ModulaPartStats.MINING_SPEED.get(), miningSpeed));
        modifiers.add(StatModifier.bonus(ModulaPartStats.ATTACK_DAMAGE.get(), attack));
        modifiers.add(StatModifier.override(ModulaPartStats.HARVEST_TIER.get(), tier));
        return modifiers;
    }
}
