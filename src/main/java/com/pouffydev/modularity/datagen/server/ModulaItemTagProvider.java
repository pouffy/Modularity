package com.pouffydev.modularity.datagen.server;

import com.pouffydev.modularity.common.registry.ModulaItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class ModulaItemTagProvider extends ItemTagsProvider {
    public ModulaItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        registerMinecraftTags();
    }

    private void registerMinecraftTags() {
        tag(ItemTags.SWORDS).add(ModulaItems.SWORD.get());
        tag(ItemTags.PICKAXES).add(ModulaItems.PICKAXE.get());
        tag(ItemTags.AXES).add(ModulaItems.AXE.get());
        tag(ItemTags.SHOVELS).add(ModulaItems.SHOVEL.get());
        tag(ItemTags.HOES).add(ModulaItems.HOE.get());
    }
}
