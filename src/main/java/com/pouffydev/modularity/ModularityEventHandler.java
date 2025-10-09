package com.pouffydev.modularity;

import com.pouffydev.modularity.api.events.CapabilityEvents;
import com.pouffydev.modularity.api.events.PlayerEvents;
import com.pouffydev.modularity.api.events.RegistryEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public class ModularityEventHandler {
    private final IEventBus modEventBus;

    public ModularityEventHandler(IEventBus modEventBus) {
        this.modEventBus = modEventBus;
    }

    public void registerModEvents(IEventBus eventBus) {
        eventBus.register(new RegistryEvents());
        eventBus.register(new CapabilityEvents());
        //eventBus.register(new NetworkEvents());
    }

    public void registerForgeEvents(IEventBus eventBus) {
        eventBus.register(new PlayerEvents());
        //eventBus.register(new ClientEvents());
        //eventBus.register(new EntityEvents());
    }

    public void register() {
        registerModEvents(modEventBus);
        registerForgeEvents(NeoForge.EVENT_BUS);
    }
}
