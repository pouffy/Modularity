package com.pouffydev.modularity.common.registry.bootstrap;

import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.assembly.deconstruction.ToolDeconstructor;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.tool.SerializableTier;
import com.pouffydev.modularity.common.registry.ModulaItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public class ModulaDeconstructors {
    public static final ResourceKey<ToolDeconstructor> WOODEN_TOOLS = create("wooden_tools");
    public static final ResourceKey<ToolDeconstructor> STONE_TOOLS = create("stone_tools");
    public static final ResourceKey<ToolDeconstructor> IRON_TOOLS = create("iron_tools");
    public static final ResourceKey<ToolDeconstructor> DIAMOND_TOOLS = create("diamond_tools");
    public static final ResourceKey<ToolDeconstructor> GOLDEN_TOOLS = create("golden_tools");
    public static final ResourceKey<ToolDeconstructor> NETHERITE_TOOLS = create("netherite_tools");

    private static ResourceKey<ToolDeconstructor> create(String namespace, String path) {
        return ResourceKey.create(ModularityRegistries.TOOL_DECONSTRUCTOR, ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
    private static ResourceKey<ToolDeconstructor> create(String name) {
        return ResourceKey.create(ModularityRegistries.TOOL_DECONSTRUCTOR, Modularity.modularityPath(name));
    }

    private static void registerDeconstructors(BootstrapContext<ToolDeconstructor> ctx) {
        ctx.register(WOODEN_TOOLS, new ToolDeconstructor(List.of(
                shovel(ctx, Items.WOODEN_SHOVEL, ModulaMaterials.WOOD, ModulaMaterials.WOOD),
                pickaxe(ctx, Items.WOODEN_PICKAXE, ModulaMaterials.WOOD, ModulaMaterials.WOOD),
                axe(ctx, Items.WOODEN_AXE, ModulaMaterials.WOOD, ModulaMaterials.WOOD),
                hoe(ctx, Items.WOODEN_HOE, ModulaMaterials.WOOD, ModulaMaterials.WOOD),
                sword(ctx, Items.WOODEN_SWORD, ModulaMaterials.WOOD, ModulaMaterials.WOOD, ModulaMaterials.WOOD)
        )));
        ctx.register(STONE_TOOLS, new ToolDeconstructor(List.of(
                shovel(ctx, Items.STONE_SHOVEL, ModulaMaterials.STONE, ModulaMaterials.WOOD),
                pickaxe(ctx, Items.STONE_PICKAXE, ModulaMaterials.STONE, ModulaMaterials.WOOD),
                axe(ctx, Items.STONE_AXE, ModulaMaterials.STONE, ModulaMaterials.WOOD),
                hoe(ctx, Items.STONE_HOE, ModulaMaterials.STONE, ModulaMaterials.WOOD),
                sword(ctx, Items.STONE_SWORD, ModulaMaterials.STONE, ModulaMaterials.STONE, ModulaMaterials.WOOD)
        )));
        ctx.register(IRON_TOOLS, new ToolDeconstructor(List.of(
                shovel(ctx, Items.IRON_SHOVEL, ModulaMaterials.IRON, ModulaMaterials.WOOD),
                pickaxe(ctx, Items.IRON_PICKAXE, ModulaMaterials.IRON, ModulaMaterials.WOOD),
                axe(ctx, Items.IRON_AXE, ModulaMaterials.IRON, ModulaMaterials.WOOD),
                hoe(ctx, Items.IRON_HOE, ModulaMaterials.IRON, ModulaMaterials.WOOD),
                sword(ctx, Items.IRON_SWORD, ModulaMaterials.IRON, ModulaMaterials.IRON, ModulaMaterials.WOOD)
        )));
        ctx.register(DIAMOND_TOOLS, new ToolDeconstructor(List.of(
                shovel(ctx, Items.DIAMOND_SHOVEL, ModulaMaterials.DIAMOND, ModulaMaterials.WOOD),
                pickaxe(ctx, Items.DIAMOND_PICKAXE, ModulaMaterials.DIAMOND, ModulaMaterials.WOOD),
                axe(ctx, Items.DIAMOND_AXE, ModulaMaterials.DIAMOND, ModulaMaterials.WOOD),
                hoe(ctx, Items.DIAMOND_HOE, ModulaMaterials.DIAMOND, ModulaMaterials.WOOD),
                sword(ctx, Items.DIAMOND_SWORD, ModulaMaterials.DIAMOND, ModulaMaterials.DIAMOND, ModulaMaterials.WOOD)
        )));
        ctx.register(GOLDEN_TOOLS, new ToolDeconstructor(List.of(
                shovel(ctx, Items.GOLDEN_SHOVEL, ModulaMaterials.GOLD, ModulaMaterials.WOOD),
                pickaxe(ctx, Items.GOLDEN_PICKAXE, ModulaMaterials.GOLD, ModulaMaterials.WOOD),
                axe(ctx, Items.GOLDEN_AXE, ModulaMaterials.GOLD, ModulaMaterials.WOOD),
                hoe(ctx, Items.GOLDEN_HOE, ModulaMaterials.GOLD, ModulaMaterials.WOOD),
                sword(ctx, Items.GOLDEN_SWORD, ModulaMaterials.GOLD, ModulaMaterials.GOLD, ModulaMaterials.WOOD)
        )));
        ctx.register(NETHERITE_TOOLS, new ToolDeconstructor(List.of(
                shovel(ctx, Items.NETHERITE_SHOVEL, ModulaMaterials.NETHERITE, ModulaMaterials.NETHER_WOOD),
                pickaxe(ctx, Items.NETHERITE_PICKAXE, ModulaMaterials.NETHERITE, ModulaMaterials.NETHER_WOOD),
                axe(ctx, Items.NETHERITE_AXE, ModulaMaterials.NETHERITE, ModulaMaterials.NETHER_WOOD),
                hoe(ctx, Items.NETHERITE_HOE, ModulaMaterials.NETHERITE, ModulaMaterials.NETHER_WOOD),
                sword(ctx, Items.NETHERITE_SWORD, ModulaMaterials.NETHERITE, ModulaMaterials.NETHERITE, ModulaMaterials.NETHER_WOOD)
        )));
    }

    private static Holder<ToolMaterial> material(BootstrapContext<ToolDeconstructor> ctx, ResourceKey<ToolMaterial> key) {
        HolderGetter<ToolMaterial> materialLookup = ctx.lookup(ModularityRegistries.TOOL_MATERIAL);
        Optional<Holder.Reference<ToolMaterial>> materialReference = materialLookup.get(key);
        return materialReference.orElse(null);
    }

    private static ToolDeconstructor.ToolEntry shovel(BootstrapContext<ToolDeconstructor> ctx, Item tool, ResourceKey<ToolMaterial> head, ResourceKey<ToolMaterial> handle) {
        return new ToolDeconstructor.ToolEntry(tool, List.of(
                new ToolDeconstructor.PartEntry(ModulaItems.SHOVEL_HEAD.asItem(), material(ctx, head)),
                new ToolDeconstructor.PartEntry(ModulaItems.TOOL_HANDLE.asItem(), material(ctx, handle))
        ));
    }

    private static ToolDeconstructor.ToolEntry pickaxe(BootstrapContext<ToolDeconstructor> ctx, Item tool, ResourceKey<ToolMaterial> head, ResourceKey<ToolMaterial> handle) {
        return new ToolDeconstructor.ToolEntry(tool, List.of(
                new ToolDeconstructor.PartEntry(ModulaItems.PICKAXE_HEAD.asItem(), material(ctx, head)),
                new ToolDeconstructor.PartEntry(ModulaItems.TOOL_HANDLE.asItem(), material(ctx, handle))
        ));
    }

    private static ToolDeconstructor.ToolEntry axe(BootstrapContext<ToolDeconstructor> ctx, Item tool, ResourceKey<ToolMaterial> head, ResourceKey<ToolMaterial> handle) {
        return new ToolDeconstructor.ToolEntry(tool, List.of(
                new ToolDeconstructor.PartEntry(ModulaItems.AXE_HEAD.asItem(), material(ctx, head)),
                new ToolDeconstructor.PartEntry(ModulaItems.TOOL_HANDLE.asItem(), material(ctx, handle))
        ));
    }

    private static ToolDeconstructor.ToolEntry hoe(BootstrapContext<ToolDeconstructor> ctx, Item tool, ResourceKey<ToolMaterial> head, ResourceKey<ToolMaterial> handle) {
        return new ToolDeconstructor.ToolEntry(tool, List.of(
                new ToolDeconstructor.PartEntry(ModulaItems.HOE_HEAD.asItem(), material(ctx, head)),
                new ToolDeconstructor.PartEntry(ModulaItems.TOOL_HANDLE.asItem(), material(ctx, handle))
        ));
    }

    private static ToolDeconstructor.ToolEntry sword(BootstrapContext<ToolDeconstructor> ctx, Item tool, ResourceKey<ToolMaterial> blade, ResourceKey<ToolMaterial> hilt, ResourceKey<ToolMaterial> handle) {
        return new ToolDeconstructor.ToolEntry(tool, List.of(
                new ToolDeconstructor.PartEntry(ModulaItems.SWORD_BLADE.asItem(), material(ctx, blade)),
                new ToolDeconstructor.PartEntry(ModulaItems.HILT.asItem(), material(ctx, hilt)),
                new ToolDeconstructor.PartEntry(ModulaItems.TOOL_HANDLE.asItem(), material(ctx, handle))
        ));
    }

    public static void bootstrap(BootstrapContext<ToolDeconstructor> context) {
        registerDeconstructors(context);
    }
}
