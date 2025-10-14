package com.pouffydev.modularity.datagen.server;

import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModulaPartTagProvider extends IntrinsicHolderTagsProvider<ToolPartType<?>> {
    public ModulaPartTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, ModularityRegistries.TOOL_PART_TYPE, provider, (partType) -> partType.builtInRegistryHolder().key(), Modularity.MODULARITY, existingFileHelper);
    }

    public static TagKey<ToolPartType<?>> handles = create("handles");

    public static TagKey<ToolPartType<?>> swordBlades = create("sword/blades");
    public static TagKey<ToolPartType<?>> swordGuards = create("sword/guards");
    public static TagKey<ToolPartType<?>> swordPommels = create("sword/pommels");


    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(handles).add(
                ModulaToolParts.HANDLE.get()
        );
    }

    private static TagKey<ToolPartType<?>> create(String name) {
        return TagKey.create(ModularityRegistries.TOOL_PART_TYPE, Modularity.modularityPath(name));
    }
}
