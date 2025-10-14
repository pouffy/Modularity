package com.pouffydev.modularity.api;

import com.mojang.serialization.Lifecycle;
import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.assembly.deconstruction.ToolDeconstructor;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tool.ModularDefinition;
import com.pouffydev.modularity.api.tool.SerializableTier;
import com.pouffydev.modularity.common.registry.bootstrap.ModulaDeconstructors;
import com.pouffydev.modularity.common.registry.bootstrap.ModulaMaterials;
import com.pouffydev.modularity.common.registry.bootstrap.ModulaTiers;
import com.pouffydev.modularity.common.tools.parts.stat.IPartStat;
import com.pouffydev.modularity.common.tools.parts.stat.PartStatType;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ModularityRegistries {

    public static final ResourceKey<Registry<ToolMaterial>> TOOL_MATERIAL = createRegistryKey("tool_material");
    public static final ResourceKey<Registry<SerializableTier>> TOOL_TIER = createRegistryKey("tool_tier");
    public static final ResourceKey<Registry<ToolPartType<?>>> TOOL_PART_TYPE = createRegistryKey("tool_part_type");
    public static final ResourceKey<Registry<IPartStat<?>>> PART_STAT = createRegistryKey("part_stat");

    public static final ResourceKey<Registry<ToolDeconstructor>> TOOL_DECONSTRUCTOR = createRegistryKey("tool_deconstructor");

    public static final ResourceKey<Registry<ModularDefinition>> MODULAR_DEFINITION = createRegistryKey("modular_definition");

    public static final Registry<ToolPartType<?>> TOOL_PART_TYPE_REGISTRY = makeSyncedRegistry(TOOL_PART_TYPE);
    public static final Registry<IPartStat<?>> PART_STAT_REGISTRY = makeSyncedRegistry(PART_STAT);
    public static final Registry<ModularDefinition> MODULAR_DEFINITION_REGISTRY = makeSyncedRegistry(MODULAR_DEFINITION);


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

    private static final RegistrySetBuilder BUILDER;

    public static HolderLookup.Provider createLookup() {
        RegistryAccess.Frozen registryaccess$frozen = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        return BUILDER.build(registryaccess$frozen);
    }

    static {
        BUILDER = (new RegistrySetBuilder())
                .add(TOOL_MATERIAL, ModulaMaterials::bootstrap)
                .add(TOOL_TIER, ModulaTiers::bootstrap)
                .add(TOOL_DECONSTRUCTOR, ModulaDeconstructors::bootstrap)
                ;
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
