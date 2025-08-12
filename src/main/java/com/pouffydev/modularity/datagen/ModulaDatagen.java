package com.pouffydev.modularity.datagen;

import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.common.registry.bootstrap.ModulaTiers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModulaDatagen {
    public static void gatherDataEvent(GatherDataEvent event) {
        Modularity.LOGGER.info("[Modularity] Data Generation starts.");
        String modId = Modularity.MODULARITY;
        DataGenerator dataGenerator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        PackOutput packOutput = dataGenerator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        //Tags

        // add providers

        // Built-in data entries
        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(output, lookupProvider, new RegistrySetBuilder()
                        .add(ModularityRegistries.TOOL_TIER, ModulaTiers::bootstrap)
                        , Set.of(modId, "minecraft"))
        );
    }
}
