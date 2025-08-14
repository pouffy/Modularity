package com.pouffydev.modularity.api.material.stats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.material.parts.IToolPart;

import java.util.List;

public record MaterialStats(List<IToolPart> parts) {
    public static final Codec<MaterialStats> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IToolPart.DIRECT_CODEC.listOf().fieldOf("parts").forGetter(MaterialStats::parts)
    ).apply(instance, MaterialStats::new));
}
