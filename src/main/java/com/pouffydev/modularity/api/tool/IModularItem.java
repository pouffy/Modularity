package com.pouffydev.modularity.api.tool;

import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IModularItem {
    ItemStack getModularItem();

    List<ModularPart> getParts();

    List<ModularPart> getPartsForMaterial(ResourceKey<ToolMaterial> material);

    Holder<ToolMaterial> getMaterialForPart(ToolPartType<?> partType);

    void setMaterial(ToolPartType<?> partType, Holder<ToolMaterial> material);
}
