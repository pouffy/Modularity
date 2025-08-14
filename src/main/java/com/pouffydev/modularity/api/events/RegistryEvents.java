package com.pouffydev.modularity.api.events;

import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.tool.SerializableTier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

public class RegistryEvents {

    @SubscribeEvent
    public void newDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ModularityRegistries.TOOL_TIER, SerializableTier.DIRECT_CODEC, SerializableTier.DIRECT_CODEC);
        event.dataPackRegistry(ModularityRegistries.TOOL_MATERIAL, ToolMaterial.DIRECT_CODEC, ToolMaterial.DIRECT_CODEC);
    }

    @SubscribeEvent
    public void newRegistry(NewRegistryEvent event) {
        event.register(ModularityRegistries.TOOL_PART_TYPE_REGISTRY);
    }
}
