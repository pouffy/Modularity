package com.pouffydev.modularity.api.material.item;

import com.pouffydev.modularity.api.material.ToolMaterial;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface IMaterialItem extends ItemLike {

    Holder<ToolMaterial> getMaterial(ItemStack stack);

    default ItemStack withMaterialForDisplay(Holder<ToolMaterial> materialKey) {
        ItemStack stack = new ItemStack(this);

        return stack;
    }

    default ItemStack withMaterial(Holder<ToolMaterial> material) {
        if (canUseMaterial(material)) {
            return withMaterialForDisplay(material);
        }
        return new ItemStack(this);
    }

    default boolean canUseMaterial(ResourceKey<ToolMaterial> key) {
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
}
