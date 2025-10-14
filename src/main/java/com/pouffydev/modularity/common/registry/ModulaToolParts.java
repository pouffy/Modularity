package com.pouffydev.modularity.common.registry;

import com.mojang.serialization.MapCodec;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.common.RegistryHelper;
import com.pouffydev.modularity.common.tools.parts.ToolHandle;
import com.pouffydev.modularity.common.tools.parts.ToolHead;
import com.pouffydev.modularity.common.tools.parts.ToolGuard;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModulaToolParts {
    public static final DeferredRegister<ToolPartType<?>> TOOL_PARTS = RegistryHelper.createRegister(ModularityRegistries.TOOL_PART_TYPE);

    public static final DeferredHolder<ToolPartType<?>, ToolPartType<ToolHead>> HEAD = register("head", ToolHead.CODEC);

    //Handles
    public static final DeferredHolder<ToolPartType<?>, ToolPartType<ToolHandle>> HANDLE = register("handle", ToolHandle.CODEC);

    //Blades

    //Heads
    public static final DeferredHolder<ToolPartType<?>, ToolPartType<ToolHead>> AXE_HEAD = register("axe_head", ToolHead.CODEC);
    public static final DeferredHolder<ToolPartType<?>, ToolPartType<ToolHead>> PICKAXE_HEAD = register("pickaxe_head", ToolHead.CODEC);
    public static final DeferredHolder<ToolPartType<?>, ToolPartType<ToolHead>> SHOVEL_HEAD = register("shovel_head", ToolHead.CODEC);
    public static final DeferredHolder<ToolPartType<?>, ToolPartType<ToolHead>> HOE_HEAD = register("hoe_head", ToolHead.CODEC);

    //Fittings
    public static final DeferredHolder<ToolPartType<?>, ToolPartType<ToolGuard>> GUARD = register("guard", ToolGuard.CODEC);


    public static <T extends IToolPart> DeferredHolder<ToolPartType<?>, ToolPartType<T>> register(String name, MapCodec<T> codec) {
        return TOOL_PARTS.register(name, () -> new ToolPartType<>(codec));
    }

    public static void staticInit() {}
}
