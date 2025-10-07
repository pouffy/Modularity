package com.pouffydev.modularity.common.util;

import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tool.part.ToolPartItem;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.settings.KeyMappingLookup;

import java.util.function.Consumer;

public class TooltipUtils {
    public static void material(Holder<ToolMaterial> material, Consumer<Component> tooltipComponents, boolean advanced) {
        Component materialName = Component.empty();
        if (material != null) {
            String materialLangKey = Util.makeDescriptionId("tool_material", material.getKey().location());
            materialName = Component.translatable(materialLangKey).withStyle(Style.EMPTY.withColor(material.value().info().color()));
        }
        if (!materialName.getString().isEmpty()) {
            tooltipComponents.accept(materialName);
            if (advanced && material != null && material.getKey() != null) {
                MutableComponent loc = Component.literal("[%s]".formatted(material.getKey().location().toString()));
                tooltipComponents.accept(loc.withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public static void singleStats(Holder<ToolMaterial> material, ToolPartType<?> partType, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        String[] holdDesc = Component.translatable("modularity.tooltip.holdForStats", "$")
                .getString()
                .split("\\$");
        MutableComponent keyShift = Component.translatable("modularity.tooltip.keyShift");
        MutableComponent tabBuilder = Component.empty();
        tabBuilder.append(Component.literal(holdDesc[0]).withStyle(ChatFormatting.DARK_GRAY));
        tabBuilder.append(keyShift.plainCopy()
                .withStyle(flag.hasShiftDown() ? ChatFormatting.WHITE : ChatFormatting.GRAY));
        tabBuilder.append(Component.literal(holdDesc[1]).withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.accept(tabBuilder);
        if (flag.hasShiftDown()) {
            if (material != null) {
                material.value().stats().getPartOfType(partType).statsTooltip(material, tooltipComponents, flag.isAdvanced());
            }
        }
    }
}
