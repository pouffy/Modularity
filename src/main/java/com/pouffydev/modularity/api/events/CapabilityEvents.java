package com.pouffydev.modularity.api.events;

import com.pouffydev.modularity.api.material.item.MaterialItem;
import com.pouffydev.modularity.api.tool.ModularItem;
import com.pouffydev.modularity.api.tool.ModularItemWrapper;
import com.pouffydev.modularity.api.tool.part.ToolPartItem;
import com.pouffydev.modularity.common.registry.ModulaCapabilities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CapabilityEvents {

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof MaterialItem) {
                event.registerItem(ModulaCapabilities.MODULAR_ITEM, (stack, v) -> new ModularItemWrapper.Single(stack), item);
            }
            if (item instanceof ModularItem) {
                event.registerItem(ModulaCapabilities.MODULAR_ITEM, (stack, v) -> new ModularItemWrapper.Multipart(stack), item);
            }
        }
    }
}
