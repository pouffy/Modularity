package com.pouffydev.modularity.api.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public record ModularPart(ToolPartType<?> type, Holder<ToolMaterial> material) {
    public static final Codec<ModularPart> DIRECT_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ModularityRegistries.TOOL_PART_TYPE_REGISTRY.byNameCodec().fieldOf("part").forGetter(ModularPart::type),
                    ToolMaterial.CODEC.fieldOf("material").forGetter(ModularPart::material)
            ).apply(instance, ModularPart::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModularPart> STREAM_CODEC = StreamCodec.composite(
            ToolPartType.STREAM_CODEC, ModularPart::type,
            ToolMaterial.STREAM_CODEC, ModularPart::material,
            ModularPart::new
    );

    public static final Codec<List<ModularPart>> LIST_CODEC = DIRECT_CODEC.listOf();
    public static final StreamCodec<RegistryFriendlyByteBuf, List<ModularPart>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list(32));

    // Use for getting the sprite for multipart items
    public ResourceLocation getTextureLocation(ItemStack stack) {
        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
        ResourceLocation materialLoc = Objects.requireNonNull(material().getKey()).location();
        String partName = Objects.requireNonNull(ModularityRegistries.TOOL_PART_TYPE_REGISTRY.getKey(type)).getPath();
        String materialFile = materialLoc.toString().replace(':', '_');
        return ResourceLocation.fromNamespaceAndPath(itemKey.getNamespace(), "item/"+itemKey.getPath()+"/"+partName+"/"+materialFile);
    }

    // Use for getting the sprite for single items
    public ResourceLocation getSingleTextureLocation(ItemStack stack) {
        ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(stack.getItem());
        ResourceLocation materialLoc = Objects.requireNonNull(material().getKey()).location();
        String materialFile = materialLoc.toString().replace(':', '_');
        return ResourceLocation.fromNamespaceAndPath(itemKey.getNamespace(), "item/"+itemKey.getPath()+"/"+materialFile);
    }
}
