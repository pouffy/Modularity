package com.pouffydev.modularity.common.tools.parts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.common.registry.ModulaToolParts;

public record ToolHilt(float attackSpeed) implements IToolPart {
    public static final MapCodec<ToolHilt> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("speed").forGetter(ToolHilt::attackSpeed)
            ).apply(instance, ToolHilt::new));

    @Override
    public ToolPartType<?> getType() {
        return ModulaToolParts.HILT.get();
    }
}
