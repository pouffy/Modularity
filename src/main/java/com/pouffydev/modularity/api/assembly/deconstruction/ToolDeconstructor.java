package com.pouffydev.modularity.api.assembly.deconstruction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.tool.ModularPart;
import com.pouffydev.modularity.api.tool.part.ToolPartItem;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;

public record ToolDeconstructor(List<ToolEntry> tools) {
    public static final Codec<ToolDeconstructor> DIRECT_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ToolEntry.CODEC.listOf().fieldOf("tools").forGetter(ToolDeconstructor::tools)
            ).apply(instance, ToolDeconstructor::new));

    public static final Codec<Holder<ToolDeconstructor>> CODEC = RegistryFixedCodec.create(ModularityRegistries.TOOL_DECONSTRUCTOR);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ToolDeconstructor>> STREAM_CODEC = ByteBufCodecs.holderRegistry(ModularityRegistries.TOOL_DECONSTRUCTOR);

    public record ToolEntry(Item item, List<PartEntry> parts) {
        public static final Codec<ToolEntry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ToolEntry::item),
                        PartEntry.CODEC.listOf().fieldOf("parts").forGetter(ToolEntry::parts)
                ).apply(instance, ToolEntry::new));
    }

    public record PartEntry(Item part, Holder<ToolMaterial> material) {
        public static final Codec<PartEntry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BuiltInRegistries.ITEM.byNameCodec().fieldOf("part").forGetter(PartEntry::part),
                        ToolMaterial.CODEC.fieldOf("material").forGetter(PartEntry::material)
                ).apply(instance, PartEntry::new));
    }


}
