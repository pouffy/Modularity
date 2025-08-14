package com.pouffydev.modularity;

import com.pouffydev.modularity.client.render.MaterialItemModel;
import com.pouffydev.modularity.client.render.ModularItemModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

import static com.pouffydev.modularity.Modularity.MODULARITY;

@EventBusSubscriber(value = Dist.CLIENT, modid = MODULARITY, bus = EventBusSubscriber.Bus.MOD)
public class ModularityClient {

    @SubscribeEvent
    public static void clientInit(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(Modularity.modularityPath("modular"), new ModularItemModel.Loader());
        event.register(Modularity.modularityPath("material"), new MaterialItemModel.Loader());
    }
}
