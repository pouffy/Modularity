package com.pouffydev.modularity.client.render;

import com.google.gson.JsonObject;
import com.pouffydev.modularity.Modularity;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModularLoaderBuilder extends CustomLoaderBuilder<ItemModelBuilder> {
    public ModularLoaderBuilder(ItemModelBuilder parent, ExistingFileHelper existingFileHelper) {
        super(Modularity.modularityPath("modular"), parent, existingFileHelper, false);
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        return super.toJson(json);
    }
}
