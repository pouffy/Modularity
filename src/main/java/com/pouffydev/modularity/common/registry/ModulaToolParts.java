package com.pouffydev.modularity.common.registry;

import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.common.RegistryHelper;
import com.pouffydev.modularity.common.tools.parts.ToolHead;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModulaToolParts {
    public static final DeferredRegister<ToolPartType<?>> TOOL_PARTS = RegistryHelper.createRegister(ModularityRegistries.TOOL_PART_TYPE);

    //public static final DeferredHolder<ToolPartType<?>, ToolPartType<ToolHead>> HEAD = register("head", () -> new ToolPartType<>(new ToolHead()));

    public static <T extends IToolPart> DeferredHolder<ToolPartType<?>, ToolPartType<T>> register(String name, Supplier<ToolPartType<T>> supplier) {
        return TOOL_PARTS.register(name, supplier);
    }

    public static void staticInit() {}
}
