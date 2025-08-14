package com.pouffydev.modularity.api.material.parts;

import com.mojang.serialization.Codec;
import com.pouffydev.modularity.api.ModularityRegistries;

public interface IToolPart {
    Codec<IToolPart> DIRECT_CODEC = Codec.lazyInitialized(ModularityRegistries.TOOL_PART_TYPE_REGISTRY::byNameCodec)
            .dispatch(IToolPart::getType, ToolPartType::codec);

    ToolPartType<?> getType();

    default boolean canRepair() {
        return isMainPart();
    }

    default boolean isMainPart() {
        return false;
    };
}
