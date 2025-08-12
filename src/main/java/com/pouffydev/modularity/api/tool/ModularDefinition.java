package com.pouffydev.modularity.api.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ModularDefinition(List<ModularPart> parts) {
    public static final Codec<ModularDefinition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ModularPart.LIST_CODEC.fieldOf("parts").forGetter(ModularDefinition::parts)
            ).apply(instance, ModularDefinition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModularDefinition> STREAM_CODEC = StreamCodec.composite(
            ModularPart.LIST_STREAM_CODEC, ModularDefinition::parts,
            ModularDefinition::new
    );
}
