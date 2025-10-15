package com.pouffydev.modularity.api.data;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.common.util.JsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class MergingJsonDataLoader<B> implements ResourceManagerReloadListener {
    @VisibleForTesting
    protected final Gson gson;
    @VisibleForTesting
    protected final String folder;
    @VisibleForTesting
    protected final Function<ResourceLocation,B> builderConstructor;

    protected MergingJsonDataLoader(Gson gson, String folder, Function<ResourceLocation, B> builderConstructor) {
        this.gson = gson;
        this.folder = folder;
        this.builderConstructor = builderConstructor;
    }

    protected abstract void parse(B builder, ResourceLocation id, JsonElement element) throws JsonSyntaxException;

    protected abstract void finishLoad(Map<ResourceLocation,B> map, ResourceManager manager);

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        Map<ResourceLocation,B> map = new HashMap<>();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : manager.listResourceStacks(folder, fileName -> fileName.getPath().endsWith(".json")).entrySet()) {
            ResourceLocation filePath = entry.getKey();
            ResourceLocation id = JsonHelper.localize(filePath, folder, ".json");

            for (Resource resource : entry.getValue()) {
                try (Reader reader = resource.openAsReader()) {
                    JsonElement json = GsonHelper.fromJson(gson, reader, JsonElement.class);
                    if (json == null) {
                        Modularity.LOGGER.error("Couldn't load data file {} from {} in data pack {} as its null or empty", id, filePath, resource.sourcePackId());
                    } else {
                        B builder = map.computeIfAbsent(id, builderConstructor);
                        parse(builder, id, json);
                    }
                } catch (RuntimeException | IOException ex) {
                    Modularity.LOGGER.error("Couldn't parse data file {} from {} in data pack {}", id, filePath, resource.sourcePackId(), ex);
                }
            }
        }
        finishLoad(map, manager);
    }
}
