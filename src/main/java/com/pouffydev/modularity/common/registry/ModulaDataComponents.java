package com.pouffydev.modularity.common.registry;

import com.pouffydev.modularity.api.tool.ModularDefinition;
import com.pouffydev.modularity.common.RegistryHelper;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModulaDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = RegistryHelper.createRegister(Registries.DATA_COMPONENT_TYPE);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ModularDefinition>> MODULAR_DEFINITION = COMPONENTS.register("modular_definition", () -> DataComponentType.<ModularDefinition>builder().persistent(ModularDefinition.CODEC).networkSynchronized(ModularDefinition.STREAM_CODEC).build());

    public static void staticInit() {}
}
