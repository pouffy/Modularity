package com.pouffydev.modularity.api.material.parts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;

import java.util.function.Function;
import java.util.stream.Stream;

public class ToolPartType<T extends IToolPart> {

    private final Holder.Reference<ToolPartType<?>> builtInRegistryHolder;
    public final MapCodec<T> codec;
    public final StreamCodec<? extends ByteBuf, T> streamCodec;

    public <B extends ByteBuf> ToolPartType(MapCodec<T> codec, StreamCodec<B, T> streamCodec) {
        this.streamCodec = streamCodec;
        this.builtInRegistryHolder = ModularityRegistries.TOOL_PART_TYPE_REGISTRY.createIntrusiveHolder(this);
        this.codec = codec;
    }

    public MapCodec<T> getCodec() {
        return codec;
    }

    @SuppressWarnings("unchecked")
    public <B extends ByteBuf> StreamCodec<B, T> getStreamCodec() {
        return (StreamCodec<B, T>) streamCodec;
    }

    public static final Codec<ToolPartType<?>> CODEC = ModularityRegistries.TOOL_PART_TYPE_REGISTRY.byNameCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolPartType<?>> STREAM_CODEC = ByteBufCodecs.registry(ModularityRegistries.TOOL_PART_TYPE);

    public boolean is(TagKey<ToolPartType<?>> tag) {
        return this.builtInRegistryHolder.is(tag);
    }

    public boolean is(HolderSet<ToolPartType<?>> entityType) {
        return entityType.contains(this.builtInRegistryHolder);
    }

    public Holder.Reference<ToolPartType<?>> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    public Stream<TagKey<ToolPartType<?>>> getTags() {
        return this.builtInRegistryHolder().tags();
    }
}
