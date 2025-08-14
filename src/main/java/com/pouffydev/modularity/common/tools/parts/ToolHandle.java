package com.pouffydev.modularity.common.tools.parts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.material.parts.IRepairableToolPart;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.common.registry.ModulaToolParts;

public record ToolHandle(float durabilityMultiplier) implements IToolPart {
    public static final MapCodec<ToolHandle> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("durability").forGetter(ToolHandle::durabilityMultiplier)
            ).apply(instance, ToolHandle::new));

    @Override
    public ToolPartType<?> getType() {
        return ModulaToolParts.HANDLE.get();
    }
}
