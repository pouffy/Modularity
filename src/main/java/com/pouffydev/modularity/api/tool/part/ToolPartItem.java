package com.pouffydev.modularity.api.tool.part;

import com.pouffydev.modularity.api.material.parts.ToolPartType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ToolPartItem extends Item {
    public final ResourceKey<ToolPartType<?>> toolPartType;

    public ToolPartItem(Properties properties, ResourceKey<ToolPartType<?>> toolPartType) {
        super(properties);
        this.toolPartType = toolPartType;
    }
}
