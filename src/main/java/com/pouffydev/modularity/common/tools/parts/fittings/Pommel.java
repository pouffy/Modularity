package com.pouffydev.modularity.common.tools.parts.fittings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.common.registry.ModulaPartStats;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import com.pouffydev.modularity.common.util.TooltipUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record Pommel(float attackSpeed) implements IToolPart {
    public static final MapCodec<Pommel> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.optionalFieldOf("attack_speed", 0f).forGetter(Pommel::attackSpeed)
            ).apply(instance, Pommel::new));

    public static final StreamCodec<ByteBuf, Pommel> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, Pommel::attackSpeed,
            Pommel::new
    );

    @Override
    public ToolPartType<?> getType() {
        return ModulaToolParts.POMMEL.get();
    }

    @Override
    public void statsTooltip(Holder<ToolMaterial> material, Consumer<Component> tooltip, boolean advanced) {
        tooltip.accept(TooltipUtils.part(material, ModulaToolParts.POMMEL));
        var materialStats = material.value().stats();
        if (!materialStats.supportsType(getType())) return;
        for(StatModifier<?> modifier : getStats()) {
            tooltip.accept(TooltipUtils.indent(2, modifier.format()));
        }
    }

    @Override
    public List<StatModifier<?>> getStats() {
        var modifiers = new ArrayList<StatModifier<?>>();
        modifiers.add(StatModifier.bonus(ModulaPartStats.ATTACK_SPEED.get(), attackSpeed));
        return modifiers;
    }
}
