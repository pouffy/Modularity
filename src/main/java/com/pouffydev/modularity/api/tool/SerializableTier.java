package com.pouffydev.modularity.api.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.tier.TierSortingRegistry;
import com.pouffydev.modularity.common.tools.parts.ToolHead;
import com.pouffydev.modularity.common.util.ModularityCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

public record SerializableTier(TagKey<Block> incorrectBlocksForDrops, int uses, float speed, float attackDamageBonus, int enchantmentValue, Ingredient repairIngredient, Sorting sorting) implements Tier {

    public static SerializableTier EMPTY = new SerializableTier(null, 0, 0f, 0f, 0, Ingredient.EMPTY, Sorting.EMPTY);

    public static final Codec<SerializableTier> DIRECT_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    TagKey.codec(Registries.BLOCK).fieldOf("incorrect_tool_for_drops_tag").forGetter(SerializableTier::incorrectBlocksForDrops),
                    ExtraCodecs.POSITIVE_INT.fieldOf("uses").forGetter(SerializableTier::uses),
                    ExtraCodecs.POSITIVE_FLOAT.fieldOf("speed").forGetter(SerializableTier::speed),
                    ModularityCodecs.NON_NEGATIVE_FLOAT.fieldOf("attack_damage_bonus").forGetter(SerializableTier::attackDamageBonus),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("enchantability").forGetter(SerializableTier::enchantmentValue),
                    Ingredient.CODEC_NONEMPTY.fieldOf("repair").forGetter(SerializableTier::repairIngredient),
                    Sorting.CODEC.optionalFieldOf("sorting", Sorting.EMPTY).forGetter(SerializableTier::sorting)
            ).apply(instance, SerializableTier::new));

    public static final Codec<Holder<SerializableTier>> CODEC = RegistryFixedCodec.create(ModularityRegistries.TOOL_TIER);

    public void sort(ResourceLocation location) {
        TierSortingRegistry.registerTier(this, location, Collections.singletonList(sorting().befores), Collections.singletonList(sorting().afters));
    }

    @Override
    public int getUses() {
        return uses();
    }

    @Override
    public float getSpeed() {
        return speed();
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamageBonus();
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return incorrectBlocksForDrops();
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue();
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient();
    }

    public static Component getName(@Nullable ResourceKey<SerializableTier> key) {
        if (key == null) return Component.empty();
        String name = key.location().toLanguageKey("tool_tier");
        return Component.translatable("modularity.tooltip.stat.tier").append(Component.translatable(name));
    }

    public record Sorting(List<ResourceLocation> befores, List<ResourceLocation> afters) {
        public static final Sorting EMPTY = new Sorting(List.of(), List.of());

        public static final Codec<Sorting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.listOf().fieldOf("befores").forGetter(Sorting::befores),
                ResourceLocation.CODEC.listOf().fieldOf("afters").forGetter(Sorting::afters)
                ).apply(instance, Sorting::new));
    }
}
