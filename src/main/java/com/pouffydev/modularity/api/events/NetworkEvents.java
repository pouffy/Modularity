package com.pouffydev.modularity.api.events;

import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.material.stats.MaterialStatsManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkEvents {

    @SubscribeEvent
    private void registerPackets(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Modularity.MODULARITY).versioned("1.0");

        registrar.playToClient(MaterialStatsManager.UpdateMaterialStatsPayload.TYPE, MaterialStatsManager.UpdateMaterialStatsPayload.STREAM_CODEC, MaterialStatsManager.UpdateMaterialStatsPayload::handle);
    }
}
