package com.pouffydev.modularity.api;

import com.mojang.serialization.Lifecycle;
import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tool.SerializableTier;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ModularityRegistries {

    public static final ResourceKey<Registry<ToolMaterial>> TOOL_MATERIAL = createRegistryKey("tool_material");
    public static final ResourceKey<Registry<SerializableTier>> TOOL_TIER = createRegistryKey("tool_tier");
    public static final ResourceKey<Registry<ToolPartType<?>>> TOOL_PART_TYPE = createRegistryKey("tool_part_type");

    public static final Registry<ToolPartType<?>> TOOL_PART_TYPE_REGISTRY = makeSyncedRegistry(TOOL_PART_TYPE);


    private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(Modularity.modularityPath(name));
    }

    /**
     * Creates a {@link Registry} that get synchronised to clients.
     *
     * @param <T> the entry of the registry.
     */
    private static <T> Registry<T> makeSyncedRegistry(ResourceKey<Registry<T>> registryKey) {
        return new RegistryBuilder<>(registryKey).sync(true).create();
    }

    /**
     * Creates a simple {@link Registry} that <B>won't</B> be synced to clients.
     *
     * @param <T> the entry of the registry.
     */
    private static <T> Registry<T> makeRegistry(ResourceKey<Registry<T>> registryKey) {
        return new RegistryBuilder<>(registryKey).create();
    }
    private static <T> Registry<T> registerSimpleWithIntrusiveHolders(ResourceKey<? extends Registry<T>> registryKey) {
        return new MappedRegistry<>(registryKey, Lifecycle.stable(), true);
    }
}
