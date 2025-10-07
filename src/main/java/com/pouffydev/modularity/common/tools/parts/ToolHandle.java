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

public record ToolHandle(float durabilityMultiplier) implements IToolPart {
    public static final MapCodec<ToolHandle> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("durability").forGetter(ToolHandle::durabilityMultiplier)
            ).apply(instance, ToolHandle::new));

    @Override
    public ToolPartType<?> getType() {
        return ModulaToolParts.HANDLE.get();
    }

    @Override
    public void statsTooltip(Holder<ToolMaterial> material, Consumer<Component> tooltipComponents, boolean advanced) {
        String part = ModulaToolParts.HANDLE.getKey().location().toLanguageKey("tool_part_type");
        tooltipComponents.accept(Component.translatable(part).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.GRAY));
        var materialStats = material.value().stats();
        if (!materialStats.supportsType(getType())) return;
        var handleStats = (ToolHandle) materialStats.getPartOfType(getType());
        if (handleStats == null) return;
        String operator = handleStats.durabilityMultiplier() >= 0 ? "add" : "subtract";
        String durabilityKey = "modularity.tooltip.stat.durability." + operator;
        float durabilityModifier = handleStats.durabilityMultiplier() >= 0 ? handleStats.durabilityMultiplier() : -handleStats.durabilityMultiplier();
        tooltipComponents.accept(Component.translatable(durabilityKey, durabilityModifier + "x").withStyle(ChatFormatting.DARK_GREEN));
    }
}
