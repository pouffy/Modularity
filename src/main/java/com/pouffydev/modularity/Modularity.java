package com.pouffydev.modularity;

import com.pouffydev.modularity.common.registry.ModulaCreativeTab;
import com.pouffydev.modularity.common.registry.ModulaDataComponents;
import com.pouffydev.modularity.common.registry.ModulaItems;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import com.pouffydev.modularity.datagen.ModulaDatagen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Modularity.MODULARITY)
public class Modularity {
    private static Modularity INSTANCE;

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODULARITY = "modularity";
    public static final RandomSource RANDOM = RandomSource.create();

    private final IEventBus modEventBus;

    public Modularity(IEventBus modEventBus, ModContainer modContainer) {
        this.modEventBus = modEventBus;
        INSTANCE = this;
        new ModularityEventHandler(modEventBus).register();
        onCtor(modEventBus, modContainer);
        this.modEventBus.addListener(ModulaDatagen::gatherDataEvent);
        this.modEventBus.register(this);
    }

    public static void onCtor(IEventBus modEventBus, ModContainer modContainer) {
        ModulaDataComponents.staticInit();
        ModulaToolParts.staticInit();
        ModulaItems.staticInit();
        ModulaCreativeTab.staticInit();
    }

    @SubscribeEvent
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        LOGGER.info("Thank you for using Modularity! :3");
    }

    public static IEventBus getEventBus() {
        return INSTANCE.modEventBus;
    }

    public static ResourceLocation modularityPath(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODULARITY, path);
    }
}