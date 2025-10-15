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

public record Guard(float critChance) implements IToolPart {
    public static final MapCodec<Guard> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.optionalFieldOf("crit_chance", 0f).forGetter(Guard::critChance)
            ).apply(instance, Guard::new));

    public static final StreamCodec<ByteBuf, Guard> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, Guard::critChance,
            Guard::new
    );

    @Override
    public ToolPartType<?> getType() {
        return ModulaToolParts.GUARD.get();
    }

    @Override
    public void statsTooltip(Holder<ToolMaterial> material, Consumer<Component> tooltip, boolean advanced) {
        tooltip.accept(TooltipUtils.part(material, ModulaToolParts.GUARD));
        var materialStats = material.value().stats();
        if (!materialStats.supportsType(getType())) return;
        for(StatModifier<?> modifier : getStats()) {
            tooltip.accept(TooltipUtils.indent(2, modifier.format()));
        }
    }

    @Override
    public List<StatModifier<?>> getStats() {
        var modifiers = new ArrayList<StatModifier<?>>();
        modifiers.add(StatModifier.bonus(ModulaPartStats.CRIT_CHANCE.get(), critChance));
        return modifiers;
    }
}
