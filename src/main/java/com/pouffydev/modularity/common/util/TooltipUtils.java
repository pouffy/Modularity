package com.pouffydev.modularity.common.util;

import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tool.ModularPart;
import com.pouffydev.modularity.api.tool.part.ToolPartItem;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.settings.KeyMappingLookup;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class TooltipUtils {
    public static final DecimalFormat COMMA_FORMAT = new DecimalFormat("#,###,###.##", DecimalFormatSymbols.getInstance(Locale.US));
    public static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#%");
    public static final DecimalFormat BONUS_FORMAT = new DecimalFormat("#.##");
    public static final DecimalFormat MULTIPLIER_FORMAT = new DecimalFormat("#.##x");
    public static final DecimalFormat PERCENT_BOOST_FORMAT = new DecimalFormat("#%");
    static {
        BONUS_FORMAT.setPositivePrefix("+");
        PERCENT_BOOST_FORMAT.setPositivePrefix("+");
    }

    public static MutableComponent space(int count) {
        return Component.literal(" ".repeat(Math.max(0, count)));
    }

    public static MutableComponent indent(int count, Component component) {
        return space(count).append(component);
    }
    public static MutableComponent title(MutableComponent component) {
        return title(component, TextColor.fromLegacyFormat(ChatFormatting.GRAY));
    }
    public static MutableComponent title(MutableComponent component, TextColor color) {
        return component.withStyle(Style.EMPTY.withColor(color).applyFormats(ChatFormatting.UNDERLINE));
    }

    public static MutableComponent part(Holder<ToolMaterial> material, Holder<ToolPartType<?>> part) {
        TextColor color = material.value().info().color();
        String partKey = part.getKey().location().toLanguageKey("tool_part_type");
        String materialKey = material.getKey().location().toLanguageKey("tool_material");
        MutableComponent title = Component.translatable(materialKey).append(" ").append(Component.translatable(partKey));
        return indent(1, TooltipUtils.title(title, color));
    }

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

    public static void modularStats(List<ModularPart> parts, Consumer<Component> tooltipComponents, TooltipFlag flag) {
        String[] holdDesc = Component.translatable("modularity.tooltip.holdForParts", "$")
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
            for (ModularPart part : parts) {
                var type = part.type();
                var material = part.material().value();
                var partType = material.stats().getPartOfType(type);
                if (partType != null) {
                    partType.statsTooltip(part.material(), tooltipComponents, flag.isAdvanced());
                }
            }
        }
    }
}
