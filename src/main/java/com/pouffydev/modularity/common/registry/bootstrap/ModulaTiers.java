package com.pouffydev.modularity.common.registry.bootstrap;

import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.tool.SerializableTier;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import org.apache.commons.compress.compressors.lz77support.LZ77Compressor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModulaTiers {
    public static final ResourceKey<SerializableTier> WOOD = create("wood");
    public static final ResourceKey<SerializableTier> STONE = create("stone");
    public static final ResourceKey<SerializableTier> IRON = create("iron");
    public static final ResourceKey<SerializableTier> DIAMOND = create("diamond");
    public static final ResourceKey<SerializableTier> GOLD = create("gold");
    public static final ResourceKey<SerializableTier> NETHERITE = create("netherite");

    private static ResourceKey<SerializableTier> create(String namespace, String path) {
        return ResourceKey.create(ModularityRegistries.TOOL_TIER, ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
    private static ResourceKey<SerializableTier> create(String name) {
        return ResourceKey.create(ModularityRegistries.TOOL_TIER, Modularity.modularityPath(name));
    }

    private static void registerTiers(BootstrapContext<SerializableTier> context) {
        register(context, builder(WOOD, Ingredient.of(ItemTags.PLANKS)));
        register(context, builder(STONE, Ingredient.of(ItemTags.STONE_TOOL_MATERIALS))
                .incorrectToolForDrops(BlockTags.INCORRECT_FOR_STONE_TOOL)
                .uses(131).speed(4.0F).damage(1.0F).enchantability(5));
        register(context, builder(IRON, Ingredient.of(Tags.Items.INGOTS_IRON))
                .incorrectToolForDrops(BlockTags.INCORRECT_FOR_IRON_TOOL)
                .uses(250).speed(6.0F).damage(2.0F).enchantability(14));
        register(context, builder(DIAMOND, Ingredient.of(Tags.Items.GEMS_DIAMOND))
                .incorrectToolForDrops(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)
                .uses(1561).speed(8.0F).damage(3.0F).enchantability(10));
        register(context, builder(GOLD, Ingredient.of(Tags.Items.INGOTS_GOLD))
                .incorrectToolForDrops(BlockTags.INCORRECT_FOR_GOLD_TOOL)
                .uses(32).speed(12.0F).enchantability(22));
        register(context, builder(NETHERITE, Ingredient.of(Tags.Items.INGOTS_NETHERITE))
                .incorrectToolForDrops(BlockTags.INCORRECT_FOR_NETHERITE_TOOL)
                .uses(2031).speed(9.0F).damage(4.0F));
    }

    private static List<ResourceKey<SerializableTier>> tiers = new ArrayList<>();

    private static Builder builder(ResourceKey<SerializableTier> key, Ingredient ingredient) {
        return new Builder(key, ingredient);
    }

    private static void register(BootstrapContext<SerializableTier> context, Builder builder) {
        context.register(builder.key, builder.build());
        tiers.add(builder.key);
    }

    private static class Builder {
        private final ResourceKey<SerializableTier> key;
        private final Ingredient ingredient;
        private TagKey<Block> incorrectToolForDrops = BlockTags.INCORRECT_FOR_WOODEN_TOOL;
        private int uses = 59;
        private float speed = 2.0F;
        private float damage = 0.0F;
        private int enchantability = 15;

        public Builder(ResourceKey<SerializableTier> key, Ingredient ingredient) {
            this.key = key;
            this.ingredient = ingredient;
        }

        public Builder incorrectToolForDrops(TagKey<Block> incorrectToolForDrops) {
            this.incorrectToolForDrops = incorrectToolForDrops;
            return this;
        }

        public Builder uses(int uses) {
            if (uses <= 0) throw new IllegalArgumentException("Durability must be greater that 0!");
            this.uses = uses;
            return this;
        }

        public Builder speed(float speed) {
            if (speed <= 0.0F) throw new IllegalArgumentException("Speed must be greater than 0!");
            this.speed = speed;
            return this;
        }

        public Builder damage(float damage) {
            if (damage < 0.0F) throw new IllegalArgumentException("Damage cannot be less that 0!");
            this.damage = damage;
            return this;
        }

        public Builder enchantability(int enchantability) {
            if (enchantability < 0) throw new IllegalArgumentException("Enchantability cannot be less that 0!");
            this.enchantability = enchantability;
            return this;
        }
        public SerializableTier build() {
            return new SerializableTier(incorrectToolForDrops, uses, speed, damage, enchantability, ingredient);
        }
    }

    public static void bootstrap(BootstrapContext<SerializableTier> context) {
        registerTiers(context);
    }

    public static Collection<ResourceKey<SerializableTier>> getTiers() {
        return tiers;
    }
}
