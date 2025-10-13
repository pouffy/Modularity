package com.pouffydev.modularity.common.tools.parts.stat;

import com.mojang.serialization.MapCodec;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PartStatType<T extends IPartStat<?>>(MapCodec<T> codec) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolPartType<?>> STREAM_CODEC = ByteBufCodecs.registry(ModularityRegistries.TOOL_PART_TYPE);

}
