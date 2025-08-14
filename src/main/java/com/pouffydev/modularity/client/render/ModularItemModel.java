package com.pouffydev.modularity.client.render;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.item.IMaterialItem;
import com.pouffydev.modularity.api.tool.IModularItem;
import com.pouffydev.modularity.api.tool.ModularItemWrapper;
import com.pouffydev.modularity.api.tool.ModularPart;
import com.pouffydev.modularity.common.registry.ModulaCapabilities;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.geometry.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public record ModularItemModel(Map<ModularPart, ResourceLocation> parts) implements IUnbakedGeometry<ModularItemModel> {

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        final TextureAtlasSprite baseSprite = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, Modularity.modularityPath("item/blank")));
        final var itemContext = StandaloneGeometryBakingContext.builder(context).withGui3d(false).withUseBlockLight(false).build(Modularity.modularityPath("modular_override"));
        final var builder = CompositeModel.Baked.builder(itemContext, baseSprite, new ModularOverrideHandler(overrides, baker, itemContext), context.getTransforms());
        final var normalRenderTypes = new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
        for (var partEntry : parts().entrySet()) {
            final ResourceLocation location = partEntry.getValue();
            final TextureAtlasSprite sprite = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, location));
            if (sprite != null) {
                addQuads(modelState, sprite, builder, normalRenderTypes, new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.002f), new Quaternionf()));
            }
        }
        builder.setParticle(baseSprite);
        return builder.build();
    }

    private static void addQuads(ModelState modelState, TextureAtlasSprite trimSprite, CompositeModel.Baked.Builder builder, RenderTypeGroup normalRenderTypes, @Nullable Transformation transformation) {
        var transformedState = transformation == null ? modelState : new SimpleModelState(modelState.getRotation().compose(transformation), modelState.isUvLocked());
        var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, trimSprite);
        var quads = UnbakedGeometryHelper.bakeElements(unbaked, material -> trimSprite, transformedState);
        builder.addQuads(normalRenderTypes, quads);
    }

    public static class Loader implements IGeometryLoader<ModularItemModel>
    {
        @Override
        public ModularItemModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException
        {
            return new ModularItemModel(null);
        }
    }

    private static class ModularOverrideHandler extends ItemOverrides {
        private final ItemOverrides nested;
        private final ModelBaker baker;
        private final IGeometryBakingContext owner;

        private ModularOverrideHandler(ItemOverrides nested, ModelBaker baker, IGeometryBakingContext owner) {
            this.nested = nested;
            this.baker = baker;
            this.owner = owner;
        }

        @Override
        @Nullable
        @ParametersAreNonnullByDefault
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            BakedModel overridden = nested.resolve(originalModel, stack, level, entity, seed);
            if (overridden != originalModel || level == null) return overridden;
            final IModularItem modularItem = stack.getCapability(ModulaCapabilities.MODULAR_ITEM);
            if (modularItem != null) {
                Map<ModularPart, ResourceLocation> partSpriteMap = new HashMap<>();
                for (ModularPart part : modularItem.getParts()) {
                    partSpriteMap.put(part, part.getTextureLocation(stack));
                }
                ModularItemModel unbaked = new ModularItemModel(partSpriteMap);
                BakedModel bakedModel = unbaked.bake(owner, baker, Material::sprite, BlockModelRotation.X0_Y0, this);
                return bakedModel;
            } else {
                return originalModel;
            }
        }
    }
}
