package com.pouffydev.modularity.api.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.info.MaterialInfo;
import com.pouffydev.modularity.api.material.stats.MaterialStats;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;

public record ToolMaterial(MaterialInfo info, MaterialStats stats) {
    public static final Codec<ToolMaterial> DIRECT_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    MaterialInfo.DIRECT_CODEC.fieldOf("info").forGetter(ToolMaterial::info),
                    MaterialStats.DIRECT_CODEC.fieldOf("stats").forGetter(ToolMaterial::stats)
            ).apply(instance, ToolMaterial::new));

    public static final Codec<Holder<ToolMaterial>> CODEC = RegistryFixedCodec.create(ModularityRegistries.TOOL_MATERIAL);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ToolMaterial>> STREAM_CODEC = ByteBufCodecs.holderRegistry(ModularityRegistries.TOOL_MATERIAL);
}
