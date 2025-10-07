package com.pouffydev.modularity.api.material.stats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;

import java.util.List;

public record MaterialStats(List<IToolPart> parts) {
    public static final Codec<MaterialStats> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IToolPart.DIRECT_CODEC.listOf().fieldOf("parts").forGetter(MaterialStats::parts)
    ).apply(instance, MaterialStats::new));

    public boolean supportsType(ToolPartType<?> type) {
        return parts.stream().anyMatch(part -> part.getType() == type);
    }

    public <T extends IToolPart> IToolPart getPartOfType(ToolPartType<T> type) {
        if (!supportsType(type)) return null;
        return parts.stream().filter(part -> part.getType() == type).findFirst().orElse(null);
    }
}
