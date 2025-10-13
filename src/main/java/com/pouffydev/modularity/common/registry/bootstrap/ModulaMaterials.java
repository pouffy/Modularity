package com.pouffydev.modularity.common.registry.bootstrap;

import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.info.MaterialInfo;
import com.pouffydev.modularity.api.material.stats.MaterialStats;
import com.pouffydev.modularity.api.tool.SerializableTier;
import com.pouffydev.modularity.common.tools.parts.ToolHandle;
import com.pouffydev.modularity.common.tools.parts.ToolHead;
import com.pouffydev.modularity.common.tools.parts.ToolGuard;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModulaMaterials {
    public static final ResourceKey<ToolMaterial> UNKNOWN = create("unknown");
    public static final ResourceKey<ToolMaterial> WOOD = create("wood");
    public static final ResourceKey<ToolMaterial> STONE = create("stone");
    public static final ResourceKey<ToolMaterial> IRON = create("iron");
    public static final ResourceKey<ToolMaterial> DIAMOND = create("diamond");
    public static final ResourceKey<ToolMaterial> GOLD = create("gold");
    public static final ResourceKey<ToolMaterial> NETHERITE = create("netherite");
    public static final ResourceKey<ToolMaterial> NETHER_WOOD = create("nether_wood");

    private static ResourceKey<ToolMaterial> create(String namespace, String path) {
        return ResourceKey.create(ModularityRegistries.TOOL_MATERIAL, ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
    private static ResourceKey<ToolMaterial> create(String name) {
        return ResourceKey.create(ModularityRegistries.TOOL_MATERIAL, Modularity.modularityPath(name));
    }

    private static void registerMaterials(BootstrapContext<ToolMaterial> context) {
        register(context, UNKNOWN, new MaterialInfo(TextColor.fromRgb(0xd23de5), "unknown"), new MaterialStats(List.of()));
        register(context, WOOD, new MaterialInfo(TextColor.fromRgb(0x5e4719), "wood"), new MaterialStats(List.of(
                new ToolHead(60, -2.8f, Tiers.WOOD, 1.0F),
                new ToolHandle(0f, 0f, 0f, 0f),
                new ToolGuard(0.15f)
        )));
        register(context, STONE, new MaterialInfo(TextColor.fromRgb(0x7f7f7f), "stone"), new MaterialStats(List.of(
                new ToolHead(131, -2.8f, Tiers.STONE, 1.0F),
                new ToolGuard(-0.15f)
        )));
        register(context, IRON, new MaterialInfo(TextColor.fromRgb(0xc1c1c1), "iron"), new MaterialStats(List.of(
                new ToolHead(250, -2.8f, Tiers.IRON, 1.0F),
                new ToolGuard(-0.3f)
        )));
        register(context, DIAMOND, new MaterialInfo(TextColor.fromRgb(0x2be0d8), "diamond"), new MaterialStats(List.of(
                new ToolHead(1561, -2.8f, Tiers.DIAMOND, 1.0F),
                new ToolGuard(-0.45f)
        )));
        register(context, GOLD, new MaterialInfo(TextColor.fromRgb(0xfad64a), "gold"), new MaterialStats(List.of(
                new ToolHead(32, -2.8f, Tiers.GOLD, 1.0F),
                new ToolGuard(0.25f)
        )));
        register(context, NETHERITE, new MaterialInfo(TextColor.fromRgb(0x5d565d), "netherite"), new MaterialStats(List.of(
                new ToolHead(2031, -2.8f, Tiers.NETHERITE, 1.0F),
                new ToolGuard(-0.5f)
        )));
        register(context, NETHER_WOOD, new MaterialInfo(TextColor.fromRgb(0x603432), "nether_wood"), new MaterialStats(List.of(
                new ToolHandle(0.2f, 0.3f, -0.15f, 0.25f)
        )));
    }

    private static SerializableTier tier(BootstrapContext<ToolMaterial> ctx, ResourceKey<SerializableTier> key) {
        HolderGetter<SerializableTier> tierLookup = ctx.lookup(ModularityRegistries.TOOL_TIER);
        Optional<Holder.Reference<SerializableTier>> tierReference = tierLookup.get(key);
        return tierReference.map(Holder.Reference::value).orElseGet(() -> SerializableTier.EMPTY);
    }

    private static final List<ResourceKey<ToolMaterial>> materials = new ArrayList<>();

    private static void register(BootstrapContext<ToolMaterial> context, ResourceKey<ToolMaterial> key, MaterialInfo info, MaterialStats stats) {
        ToolMaterial material = new ToolMaterial(info, stats);
        context.register(key, material);
        materials.add(key);
    }

    public static void bootstrap(BootstrapContext<ToolMaterial> context) {
        registerMaterials(context);
    }

    public static List<ResourceKey<ToolMaterial>> getMaterials() {
        return materials;
    }
}
