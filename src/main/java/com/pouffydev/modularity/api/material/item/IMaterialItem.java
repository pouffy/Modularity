package com.pouffydev.modularity.api.material.item;

import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.common.registry.ModulaDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public interface IMaterialItem extends ItemLike {

    Holder<ToolMaterial> getMaterial(ItemStack stack);

    default ItemStack withMaterialForDisplay(Holder<ToolMaterial> material) {
        ItemStack stack = new ItemStack(this);
        stack.set(ModulaDataComponents.MATERIAL, material);
        return stack;
    }

    default ItemStack withMaterial(Holder<ToolMaterial> material) {
        if (canUseMaterial(material)) {
            return withMaterialForDisplay(material);
        }
        return new ItemStack(this);
    }

    default boolean canUseMaterial(ResourceKey<ToolMaterial> materialKey) {
        return true;
    }

    default boolean canUseMaterial(Holder<ToolMaterial> materialHolder) {
        return canUseMaterial(materialHolder.getKey());
    }

    static Holder<ToolMaterial> getMaterialFromStack(ItemStack stack) {
        if ((stack.getItem() instanceof IMaterialItem)) {
            return ((IMaterialItem) stack.getItem()).getMaterial(stack);
        }
        return null;
    }

    default void addMaterials(Consumer<ItemStack> items, HolderLookup.Provider lookupProvider) {
        var materialLookup = lookupProvider.lookupOrThrow(ModularityRegistries.TOOL_MATERIAL);
        for (Holder<ToolMaterial> material : materialLookup.listElements().toList()) {
            if (canUseMaterial(material)) {
                items.accept(this.withMaterial(material));
            }
        }
    }
}
