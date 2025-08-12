package com.pouffydev.modularity.api.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.common.util.ModularityCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public record SerializableTier(TagKey<Block> incorrectBlocksForDrops, int uses, float speed, float attackDamageBonus, int enchantmentValue, Ingredient repairIngredient) implements Tier {

    public static final Codec<SerializableTier> DIRECT_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    TagKey.codec(Registries.BLOCK).fieldOf("incorrect_tool_for_drops_tag").forGetter(SerializableTier::incorrectBlocksForDrops),
                    ExtraCodecs.POSITIVE_INT.fieldOf("uses").forGetter(SerializableTier::uses),
                    ExtraCodecs.POSITIVE_FLOAT.fieldOf("speed").forGetter(SerializableTier::speed),
                    ModularityCodecs.NON_NEGATIVE_FLOAT.fieldOf("attack_damage_bonus").forGetter(SerializableTier::attackDamageBonus),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("enchantability").forGetter(SerializableTier::enchantmentValue),
                    Ingredient.CODEC_NONEMPTY.fieldOf("repair").forGetter(SerializableTier::repairIngredient)
            ).apply(instance, SerializableTier::new));

    public static final Codec<Holder<SerializableTier>> CODEC = RegistryFixedCodec.create(ModularityRegistries.TOOL_TIER);

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
}
