package com.pouffydev.modularity.client.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.material.ToolMaterial;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MaterialLoaderBuilder extends CustomLoaderBuilder<ItemModelBuilder> {
    private final Vec2 offset;
    private final int index;
    private final ResourceKey<ToolMaterial> material;

    public MaterialLoaderBuilder(ItemModelBuilder parent, ExistingFileHelper existingFileHelper, Vec2 offset) {
        this(parent, existingFileHelper, offset, -1, null);
    }

    public MaterialLoaderBuilder(ItemModelBuilder parent, ExistingFileHelper existingFileHelper, Vec2 offset, int index, ResourceKey<ToolMaterial> material) {
        super(Modularity.modularityPath("material"), parent, existingFileHelper, false);
        this.offset = offset;
        this.index = index;
        this.material = material;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        if (offset != null) {
            JsonArray array = new JsonArray();
            array.add(offset.x);
            array.add(offset.y);
            json.add("offset", array);
        }
        if (index != -1) {
            json.addProperty("index", index);
        }
        if (material != null) {
            json.addProperty("material", material.location().toString());
        }
        return super.toJson(json);
    }
}
