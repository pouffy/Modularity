package com.pouffydev.modularity.api.material.item;

import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.common.registry.ModulaDataComponents;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MaterialItem extends Item implements IMaterialItem {
    public MaterialItem(Properties properties) {
        super(properties);
    }

    public String getItemName(ItemStack stack) {
        String itemName = Component.translatable(stack.getDescriptionId()).getString();
        Holder<ToolMaterial> material = getMaterial(stack);
        String materialName = "";
        if (material != null) {
            String materialLangKey = Util.makeDescriptionId("tool_material", ResourceLocation.parse(material.value().info().assetName()));
            materialName = Component.translatable(materialLangKey).getString() + " ";
        }

        return materialName + itemName;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(getItemName(stack));
    }

    @Override
    public Holder<ToolMaterial> getMaterial(ItemStack stack) {
        if (!stack.has(ModulaDataComponents.MATERIAL)) return null;
        return stack.get(ModulaDataComponents.MATERIAL);
    }
}
