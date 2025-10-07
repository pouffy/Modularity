package com.pouffydev.modularity.common.tools.parts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public record ToolHilt(float attackSpeed) implements IToolPart {
    public static final MapCodec<ToolHilt> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("speed").forGetter(ToolHilt::attackSpeed)
            ).apply(instance, ToolHilt::new));

    @Override
    public ToolPartType<?> getType() {
        return ModulaToolParts.HILT.get();
    }

    @Override
    public void statsTooltip(Holder<ToolMaterial> material, Consumer<Component> tooltipComponents, boolean advanced) {
        String part = ModulaToolParts.HILT.getKey().location().toLanguageKey("tool_part_type");
        tooltipComponents.accept(Component.translatable(part).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.GRAY));
        var materialStats = material.value().stats();
        if (!materialStats.supportsType(getType())) return;
        var hiltStats = (ToolHilt) materialStats.getPartOfType(getType());
        if (hiltStats == null) return;
        String operator = hiltStats.attackSpeed() >= 0 ? "add" : "subtract";
        String speedKey = "modularity.tooltip.stat.attack_speed." + operator;
        float speedModifier = hiltStats.attackSpeed() >= 0 ? hiltStats.attackSpeed() : -hiltStats.attackSpeed();
        tooltipComponents.accept(Component.translatable(speedKey, String.format("%.2f", speedModifier) + "%").withStyle(ChatFormatting.GRAY));
    }
}
