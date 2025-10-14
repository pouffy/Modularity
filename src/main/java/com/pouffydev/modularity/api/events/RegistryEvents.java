package com.pouffydev.modularity.api.events;

import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.tier.TierSortingRegistry;
import com.pouffydev.modularity.api.assembly.deconstruction.ToolDeconstructor;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.tool.SerializableTier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public class RegistryEvents {

    @SubscribeEvent
    public void newDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ModularityRegistries.TOOL_TIER, SerializableTier.DIRECT_CODEC, SerializableTier.DIRECT_CODEC);
        event.dataPackRegistry(ModularityRegistries.TOOL_MATERIAL, ToolMaterial.DIRECT_CODEC, ToolMaterial.DIRECT_CODEC);
        event.dataPackRegistry(ModularityRegistries.TOOL_DECONSTRUCTOR, ToolDeconstructor.DIRECT_CODEC, ToolDeconstructor.DIRECT_CODEC);
    }

    @SubscribeEvent
    public void newRegistry(NewRegistryEvent event) {
        event.register(ModularityRegistries.TOOL_PART_TYPE_REGISTRY);
        event.register(ModularityRegistries.PART_STAT_REGISTRY);
        event.register(ModularityRegistries.MODULAR_DEFINITION_REGISTRY);
    }

    @SubscribeEvent
    public void onRegisterLate(RegisterEvent event) {
        if (event.getRegistryKey() == ModularityRegistries.TOOL_TIER) {
            var tierReg = event.getRegistry(ModularityRegistries.TOOL_TIER);
            if (tierReg == null) return;
            // Sort datapack generated tiers
            tierReg.forEach(tier -> tier.sort(tierReg.getKey(tier)));
        }
    }


}
