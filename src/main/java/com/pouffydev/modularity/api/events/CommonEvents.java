package com.pouffydev.modularity.api.events;

import com.pouffydev.modularity.api.tier.TierSortingRegistry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public class CommonEvents {

    @SubscribeEvent
    public void resourceReloadListeners(AddReloadListenerEvent event) {
        event.addListener(TierSortingRegistry.getReloadListener());
    }
}
