package com.pouffydev.modularity.api.material.parts;

import com.mojang.serialization.MapCodec;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public record ToolPartType<T extends IToolPart>(MapCodec<T> codec) {

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolPartType<?>> STREAM_CODEC = ByteBufCodecs.registry(ModularityRegistries.TOOL_PART_TYPE);

}
