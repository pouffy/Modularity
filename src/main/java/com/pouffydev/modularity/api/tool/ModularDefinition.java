package com.pouffydev.modularity.api.tool;

import com.mojang.serialization.Codec;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.tags.TagKey;

import java.util.HashMap;
import java.util.Map;

public class ModularDefinition {

    public Map<String, TagKey<ToolPartType<?>>> parts;

    public static final Codec<Holder<ModularDefinition>> CODEC = RegistryFixedCodec.create(ModularityRegistries.MODULAR_DEFINITION);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ModularDefinition>> STREAM_CODEC = ByteBufCodecs.holderRegistry(ModularityRegistries.MODULAR_DEFINITION);


    public ModularDefinition() {
        this.parts = new HashMap<>();
    }

    public ModularDefinition(String[] names, TagKey<ToolPartType<?>>[] tags) {
        if (names.length != tags.length) throw new IllegalArgumentException("names and tags must have same length");
        Map<String, TagKey<ToolPartType<?>>> parts = new HashMap<>();
        for (int i = 0; i < names.length; i++) {
            parts.put(names[i], tags[i]);
        }
        this.parts = parts;
    }

    public ModularDefinition addPart(String name, TagKey<ToolPartType<?>> tag) {
        this.parts.put(name, tag);
        return this;
    }

    public boolean supports(String part, Holder<ToolPartType<?>> holder) {
        if (this.parts.containsKey(part)) {
            return holder.is(this.parts.get(part));
        }
        return false;
    }
}
