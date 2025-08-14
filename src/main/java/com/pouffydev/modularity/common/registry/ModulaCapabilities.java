package com.pouffydev.modularity.common.registry;

import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.tool.IModularItem;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public class ModulaCapabilities {
    public static final ItemCapability<IModularItem, @Nullable Void> MODULAR_ITEM = ItemCapability.createVoid(create("modular_item"), IModularItem.class);

    private static ResourceLocation create(String path) {
        return Modularity.modularityPath(path);
    }
}
