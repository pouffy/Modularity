package com.pouffydev.modularity.common.tools.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tool.ModularDefinition;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Map;

public record ToolData(Holder<ModularDefinition> definition, Map<String, ToolPartType<?>> parts) {

    public static final Codec<ToolData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ModularDefinition.CODEC.fieldOf("definition").forGetter(ToolData::definition),
            Codec.unboundedMap(Codec.STRING, ToolPartType.CODEC)
                    .validate((m) -> m.isEmpty() ? DataResult.error(() -> "Tool parts cannot be empty") : DataResult.success(m))
                    .fieldOf("parts").forGetter(ToolData::parts)
    ).apply(instance, ToolData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolData> STREAM_CODEC = StreamCodec.composite(
            ModularDefinition.STREAM_CODEC, ToolData::definition,
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ByteBufCodecs.STRING_UTF8, ToolPartType.STREAM_CODEC), ToolData::parts,
            ToolData::new
    );
}
