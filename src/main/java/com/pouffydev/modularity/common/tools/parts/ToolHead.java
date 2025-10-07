package com.pouffydev.modularity.common.tools.parts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.IRepairableToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tool.SerializableTier;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Consumer;

public record ToolHead(int durability, float miningSpeed, Holder<SerializableTier> tier, float attack) implements IRepairableToolPart {

    public static final MapCodec<ToolHead> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("durability").forGetter(ToolHead::durability),
                    Codec.FLOAT.fieldOf("speed").forGetter(ToolHead::miningSpeed),
                    SerializableTier.CODEC.fieldOf("tier").forGetter(ToolHead::tier),
                    ExtraCodecs.POSITIVE_FLOAT.fieldOf("attack").forGetter(ToolHead::attack)
            ).apply(instance, ToolHead::new));

    @Override
    public ToolPartType<?> getType() {
        return ModulaToolParts.HEAD.get();
    }

    @Override
    public void statsTooltip(Holder<ToolMaterial> material, Consumer<Component> tooltipComponents, boolean advanced) {
        String part = ModulaToolParts.HEAD.getKey().location().toLanguageKey("tool_part_type");
        tooltipComponents.accept(CommonComponents.space().append(Component.translatable(part).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.GRAY)));
        var materialStats = material.value().stats();
        if (!materialStats.supportsType(getType())) return;
        String durabilityKey = "modularity.tooltip.stat.durability";
        String speedKey = "modularity.tooltip.stat.mining_speed";
        String attackKey = "modularity.tooltip.stat.attack_damage";
        String tierKey = "modularity.tooltip.stat.tier";
        var headStats = (ToolHead) materialStats.getPartOfType(getType());
        if (headStats == null) return;
        SerializableTier tier = headStats.tier().value();
        String tierName = headStats.tier().getKey().location().toLanguageKey("tool_tier");
        Component durability = Component.literal(String.valueOf(headStats.durability())).withStyle(ChatFormatting.BLUE);
        Component speed = Component.literal(String.format("%.2f", headStats.miningSpeed() + tier.getSpeed())).withStyle(ChatFormatting.BLUE);
        Component attack = Component.literal(String.format("%.2f", headStats.attack() + tier.getAttackDamageBonus())).withStyle(ChatFormatting.BLUE);
        tooltipComponents.accept(CommonComponents.space().append(CommonComponents.space())
                .append(Component.translatable(tierKey, Component.translatable(tierName).withStyle(ChatFormatting.BLUE)).withStyle(ChatFormatting.GRAY)));
        tooltipComponents.accept(CommonComponents.space().append(CommonComponents.space())
                .append(Component.translatable(durabilityKey, durability).withStyle(ChatFormatting.GRAY)));
        tooltipComponents.accept(CommonComponents.space().append(CommonComponents.space())
                .append(Component.translatable(speedKey, speed).withStyle(ChatFormatting.GRAY)));
        tooltipComponents.accept(CommonComponents.space().append(CommonComponents.space())
                .append(Component.translatable(attackKey, attack).withStyle(ChatFormatting.GRAY)));
    }


    @Override
    public boolean isMainPart() {
        return true;
    }
}
