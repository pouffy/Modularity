package com.pouffydev.modularity.datagen;

import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.common.registry.bootstrap.ModulaDeconstructors;
import com.pouffydev.modularity.common.registry.bootstrap.ModulaMaterials;
import com.pouffydev.modularity.common.registry.bootstrap.ModulaTiers;
import com.pouffydev.modularity.datagen.client.ModulaItemModelProvider;
import com.pouffydev.modularity.datagen.client.ModulaLangProvider;
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

        boolean client = event.includeClient();
        boolean server = event.includeServer();

        ModulaLangProvider language = new ModulaLangProvider(packOutput, modId, "en_us");
        ModulaItemModelProvider itemModels = new ModulaItemModelProvider(packOutput, modId, fileHelper);
        //Tags

        // add providers
        dataGenerator.addProvider(client, itemModels);

        dataGenerator.addProvider(event.includeClient() && event.includeServer(), language);

        // Built-in data entries
        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(output, lookupProvider, new RegistrySetBuilder()
                        .add(ModularityRegistries.TOOL_TIER, ModulaTiers::bootstrap)
                        .add(ModularityRegistries.TOOL_MATERIAL, ModulaMaterials::bootstrap)
                        .add(ModularityRegistries.TOOL_DECONSTRUCTOR, ModulaDeconstructors::bootstrap)
                        , Set.of(modId))
        );
    }
}
