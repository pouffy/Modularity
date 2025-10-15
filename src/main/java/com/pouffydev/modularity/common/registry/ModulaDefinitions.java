package com.pouffydev.modularity.common.registry;

import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tool.ModularDefinition;
import com.pouffydev.modularity.common.RegistryHelper;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModulaDefinitions {
    public static final DeferredRegister<ModularDefinition> DEFINITIONS = RegistryHelper.createRegister(ModularityRegistries.MODULAR_DEFINITION);

    public static final DeferredHolder<ModularDefinition, ModularDefinition> SWORD = register("sword", new String[]{"blade", "handle", "guard", "pommel"}, new TagKey[]{});

    public static DeferredHolder<ModularDefinition, ModularDefinition> register(String name, String[] names, TagKey<ToolPartType<?>>[] tags) {
        return DEFINITIONS.register(name, () -> new ModularDefinition(names, tags));
    }
}
