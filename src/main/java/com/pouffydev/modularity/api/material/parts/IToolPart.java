package com.pouffydev.modularity.api.material.parts;

import com.mojang.serialization.Codec;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public interface IToolPart {
    Codec<IToolPart> DIRECT_CODEC = Codec.lazyInitialized(ModularityRegistries.TOOL_PART_TYPE_REGISTRY::byNameCodec)
            .dispatch(IToolPart::getType, ToolPartType::codec);

    ToolPartType<?> getType();

    default void statsTooltip(Holder<ToolMaterial> material, Consumer<Component> tooltipComponents, boolean advanced) {}

    default boolean canRepair() {
        return isMainPart();
    }

    default boolean isMainPart() {
        return false;
    };
}
