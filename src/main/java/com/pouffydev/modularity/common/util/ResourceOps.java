package com.pouffydev.modularity.common.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.ResourceLocation;

import java.util.stream.Stream;

public class ResourceOps implements DynamicOps<ResourceLocation> {
    @Override
    public ResourceLocation empty() {
        return ResourceLocation.withDefaultNamespace("error");
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, ResourceLocation input) {
        return outOps.createString(input.toString());
    }

    @Override
    public DataResult<Number> getNumberValue(ResourceLocation input) {
        return DataResult.error(() -> "Not a number: " + input);
    }

    @Override
    public ResourceLocation createNumeric(Number i) {
        return null;
    }

    @Override
    public DataResult<String> getStringValue(ResourceLocation input) {
        return DataResult.success(input.toString());
    }

    @Override
    public ResourceLocation createString(String value) {
        return ResourceLocation.parse(value);
    }

    @Override
    public DataResult<ResourceLocation> mergeToList(ResourceLocation list, ResourceLocation value) {
        return null;
    }

    @Override
    public DataResult<ResourceLocation> mergeToMap(ResourceLocation map, ResourceLocation key, ResourceLocation value) {
        return null;
    }

    @Override
    public DataResult<Stream<Pair<ResourceLocation, ResourceLocation>>> getMapValues(ResourceLocation input) {
        return null;
    }

    @Override
    public ResourceLocation createMap(Stream<Pair<ResourceLocation, ResourceLocation>> map) {
        return null;
    }

    @Override
    public DataResult<Stream<ResourceLocation>> getStream(ResourceLocation input) {
        return null;
    }

    @Override
    public ResourceLocation createList(Stream<ResourceLocation> input) {
        return null;
    }

    @Override
    public ResourceLocation remove(ResourceLocation input, String key) {
        return null;
    }
}
