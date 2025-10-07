package com.pouffydev.modularity.common.registry;

import com.mojang.serialization.Codec;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.tool.ModularDefinition;
import com.pouffydev.modularity.api.tool.ModularPart;
import com.pouffydev.modularity.common.RegistryHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModulaDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = RegistryHelper.createRegister(Registries.DATA_COMPONENT_TYPE);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ModularDefinition>> MODULAR_DEFINITION = COMPONENTS.register("modular_definition", () -> DataComponentType.<ModularDefinition>builder().persistent(ModularDefinition.CODEC).networkSynchronized(ModularDefinition.STREAM_CODEC).build());

    //Modular Stuff
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ModularPart>>> MULTIPART = COMPONENTS.register("multipart", () -> DataComponentType.<List<ModularPart>>builder().persistent(ModularPart.LIST_CODEC).networkSynchronized(ModularPart.LIST_STREAM_CODEC).build());

    //For singular material items
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<ToolMaterial>>> MATERIAL = COMPONENTS.register("material", () -> DataComponentType.<Holder<ToolMaterial>>builder().persistent(ToolMaterial.CODEC).networkSynchronized(ToolMaterial.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ModularPart>> PART = COMPONENTS.register("part", () -> DataComponentType.<ModularPart>builder().persistent(ModularPart.DIRECT_CODEC).networkSynchronized(ModularPart.STREAM_CODEC).build());

    //Utility Components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> REINIT_ATTRIBUTES = COMPONENTS.register("reinit_attributes", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build());
    public static void staticInit() {}
}
