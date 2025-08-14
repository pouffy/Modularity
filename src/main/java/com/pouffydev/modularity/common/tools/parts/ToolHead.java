package com.pouffydev.modularity.common.tools.parts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.material.parts.IRepairableToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tool.SerializableTier;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;

public record ToolHead(int durability, float miningSpeed, Holder<SerializableTier> tier, float attack) implements IRepairableToolPart {

    public static final MapCodec<ToolHead> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("durability").forGetter(ToolHead::durability),
                    Codec.FLOAT.fieldOf("speed").forGetter(ToolHead::miningSpeed),
                    SerializableTier.CODEC.fieldOf("tier").forGetter(ToolHead::tier),
                    ExtraCodecs.POSITIVE_FLOAT.fieldOf("attack").forGetter(ToolHead::attack)
            ).apply(instance, ToolHead::new));

    @Override
    public ToolPartType<?> getType() {
        return ModulaToolParts.HEAD.get();
    }

    @Override
    public boolean isMainPart() {
        return true;
    }
}
