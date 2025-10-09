package com.pouffydev.modularity.datagen.server;

import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.common.registry.bootstrap.ModulaMaterials;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModulaMaterialTagProvider extends TagsProvider<ToolMaterial> {
    public ModulaMaterialTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, ModularityRegistries.TOOL_MATERIAL, lookupProvider);
    }

    public static TagKey<ToolMaterial> netherMetals = TagKey.create(ModularityRegistries.TOOL_MATERIAL, Modularity.modularityPath("nether_metals"));

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(netherMetals).add(
                ModulaMaterials.NETHERITE
        );
    }
}
