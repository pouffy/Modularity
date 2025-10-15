package com.pouffydev.modularity.common.tools.parts.blades;

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
import com.pouffydev.modularity.common.tools.parts.main.SimpleBlade;
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

public record SimpleBlade(int durability, Tier tier, float attack) implements IRepairableToolPart {

    public static SimpleBlade EMPTY = new SimpleBlade(0, SerializableTier.EMPTY, 0f);

    public static final MapCodec<SimpleBlade> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("durability", 0).forGetter(SimpleBlade::durability),
                    HarvestTiers.TIER_CODEC.fieldOf("tier").forGetter(SimpleBlade::tier),
                    ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("attack", 0f).forGetter(SimpleBlade::attack)
            ).apply(instance, SimpleBlade::new));

    public static final StreamCodec<FriendlyByteBuf, SimpleBlade> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SimpleBlade::durability,
            HarvestTiers.TIER_STREAM_CODEC, SimpleBlade::tier,
            ByteBufCodecs.FLOAT, SimpleBlade::attack,
            SimpleBlade::new
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
        modifiers.add(StatModifier.bonus(ModulaPartStats.ATTACK_DAMAGE.get(), attack));
        modifiers.add(StatModifier.override(ModulaPartStats.HARVEST_TIER.get(), tier));
        return modifiers;
    }
}
