package com.pouffydev.modularity.client.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import com.mojang.serialization.JsonOps;
import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.item.IMaterialItem;
import com.pouffydev.modularity.common.registry.bootstrap.ModulaMaterials;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.geometry.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public record MaterialItemModel(ResourceKey<ToolMaterial> material, Vec2 offset, int index) implements IUnbakedGeometry<MaterialItemModel> {

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides vanillaOverrides) {
        Transformation transforms;
        if (Vec2.ZERO.equals(offset)) {
            transforms = Transformation.identity();
        } else {
            transforms = new Transformation(new Vector3f(offset.x / 16, -offset.y / 16, 0), null, null, null);
        }
        ItemOverrides overrides = new MaterialOverrideHandler(context, index, transforms, modelState);
        return bakeInternal(context, spriteGetter, transforms, Objects.requireNonNullElse(material, ModulaMaterials.UNKNOWN), index, overrides, modelState);
    }

    private static BakedModel bakeInternal(IGeometryBakingContext owner, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transform, ResourceKey<ToolMaterial> material, int index, ItemOverrides overrides, ModelState modelState) {
        Material texture = owner.getMaterial("texture");
        ResourceLocation textureLoc = texture.texture();
        if (material != null) {
            textureLoc = texture.texture().withSuffix("_" + material.location().toString().replace(':', '_'));
        }
        final TextureAtlasSprite baseSprite = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, textureLoc));
        final var builder = CompositeModel.Baked.builder(owner, baseSprite, overrides, owner.getTransforms());
        final var normalRenderTypes = new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
        addQuads(modelState, baseSprite, builder, normalRenderTypes, new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.002f), new Quaternionf()));
        builder.setParticle(baseSprite);
        return builder.build();
    }

    private static void addQuads(ModelState modelState, TextureAtlasSprite trimSprite, CompositeModel.Baked.Builder builder, RenderTypeGroup normalRenderTypes, @Nullable Transformation transformation) {
        var transformedState = transformation == null ? modelState : new SimpleModelState(modelState.getRotation().compose(transformation), modelState.isUvLocked());
        var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, trimSprite);
        var quads = UnbakedGeometryHelper.bakeElements(unbaked, material -> trimSprite, transformedState);
        builder.addQuads(normalRenderTypes, quads);
    }

    private static class MaterialOverrideHandler extends ItemOverrides {
        private final Map<ResourceKey<ToolMaterial>, BakedModel> cache = new ConcurrentHashMap<>();

        private final IGeometryBakingContext owner;
        private final int index;
        private final Transformation itemTransform;
        private final ModelState modelState;

        private MaterialOverrideHandler(IGeometryBakingContext owner, int index, Transformation itemTransform, ModelState modelState) {
            this.owner = owner;
            this.index = index;
            this.itemTransform = itemTransform;
            this.modelState = modelState;
        }

        @Override
        @Nullable
        @ParametersAreNonnullByDefault
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            return cache.computeIfAbsent(IMaterialItem.getMaterialFromStack(stack).getKey(), this::bakeDynamic);
        }

        private BakedModel bakeDynamic(ResourceKey<ToolMaterial> material) {
            return bakeInternal(owner, Material::sprite, itemTransform, material, index, ItemOverrides.EMPTY, modelState);
        }
    }

    public static class Loader implements IGeometryLoader<MaterialItemModel>
    {
        @Override
        public MaterialItemModel read(JsonObject json, JsonDeserializationContext deserializationContext) throws JsonParseException {
            int index = GsonHelper.getAsInt(json, "index", 0);

            ResourceKey<ToolMaterial> material = null;
            if (json.has("material")) {
                material = ResourceKey.create(ModularityRegistries.TOOL_MATERIAL, ResourceLocation.parse(json.get("material").getAsString()));
            }

            Vec2 offset = Vec2.ZERO;
            if (json.has("offset")) {
                offset = getVec2(json, "offset");
            }
            return new MaterialItemModel(material, offset, index);
        }
    }
    public static Vec2 getVec2(JsonObject json, String name) {
        JsonArray array = GsonHelper.getAsJsonArray(json, name);
        if (array.size() != 2) {
            throw new JsonParseException("Expected " + 2 + " " + name + " values, found: " + array.size());
        }
        float[] vec = new float[2];
        for(int i = 0; i < 2; ++i) {
            vec[i] = GsonHelper.convertToFloat(array.get(i), name + "[" + i + "]");
        }
        return new Vec2(vec[0], vec[1]);
    }
}
