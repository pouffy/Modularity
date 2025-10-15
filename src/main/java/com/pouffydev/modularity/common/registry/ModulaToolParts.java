package com.pouffydev.modularity.common.registry;

import com.mojang.serialization.MapCodec;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.common.RegistryHelper;
import com.pouffydev.modularity.common.tools.parts.blades.SimpleBlade;
import com.pouffydev.modularity.common.tools.parts.fittings.Pommel;
import com.pouffydev.modularity.common.tools.parts.handles.BasicHandle;
import com.pouffydev.modularity.common.tools.parts.main.*;
import com.pouffydev.modularity.common.tools.parts.fittings.Guard;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModulaToolParts {
    public static final DeferredRegister<ToolPartType<?>> TOOL_PARTS = RegistryHelper.createRegister(ModularityRegistries.TOOL_PART_TYPE);

    public static final DeferredHolder<ToolPartType<?>, ToolPartType<DiggerHead>> HEAD = register("head", DiggerHead.CODEC, DiggerHead.STREAM_CODEC);

    //Handles
    public static final DeferredHolder<ToolPartType<?>, ToolPartType<BasicHandle>> BASIC_HANDLE = register("basic_handle", BasicHandle.CODEC, BasicHandle.STREAM_CODEC);

    //Blades
    public static final DeferredHolder<ToolPartType<?>, ToolPartType<SimpleBlade>> SIMPLE_BLADE = register("simple_blade", SimpleBlade.CODEC, SimpleBlade.STREAM_CODEC);

    //Heads
    public static final DeferredHolder<ToolPartType<?>, ToolPartType<AxeHead>> AXE_HEAD = register("axe_head", AxeHead.CODEC, AxeHead.STREAM_CODEC);
    public static final DeferredHolder<ToolPartType<?>, ToolPartType<PickaxeHead>> PICKAXE_HEAD = register("pickaxe_head", PickaxeHead.CODEC, PickaxeHead.STREAM_CODEC);
    public static final DeferredHolder<ToolPartType<?>, ToolPartType<ShovelHead>> SHOVEL_HEAD = register("shovel_head", ShovelHead.CODEC, ShovelHead.STREAM_CODEC);
    public static final DeferredHolder<ToolPartType<?>, ToolPartType<HoeHead>> HOE_HEAD = register("hoe_head", HoeHead.CODEC, HoeHead.STREAM_CODEC);

    //Fittings
    public static final DeferredHolder<ToolPartType<?>, ToolPartType<Guard>> GUARD = register("guard", Guard.CODEC, Guard.STREAM_CODEC);
    public static final DeferredHolder<ToolPartType<?>, ToolPartType<Pommel>> POMMEL = register("pommel", Pommel.CODEC, Pommel.STREAM_CODEC);


    public static <T extends IToolPart, B extends ByteBuf> DeferredHolder<ToolPartType<?>, ToolPartType<T>> register(String name, MapCodec<T> codec, StreamCodec<B, T> streamCodec) {
        return TOOL_PARTS.register(name, () -> new ToolPartType<>(codec, streamCodec));
    }

    public static void staticInit() {}
}
