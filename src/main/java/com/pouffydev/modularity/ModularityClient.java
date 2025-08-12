package com.pouffydev.modularity;

import net.minecraft.world.item.armortrim.TrimPattern;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import static com.pouffydev.modularity.Modularity.MODULARITY;

@EventBusSubscriber(value = Dist.CLIENT, modid = MODULARITY, bus = EventBusSubscriber.Bus.MOD)
public class ModularityClient {

    @SubscribeEvent
    public static void clientInit(FMLClientSetupEvent event) {

    }
}
