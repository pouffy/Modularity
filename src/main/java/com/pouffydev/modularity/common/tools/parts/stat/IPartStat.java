package com.pouffydev.modularity.common.tools.parts.stat;

import com.mojang.serialization.Codec;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.common.util.TooltipUtils;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public interface IPartStat<T> {

    ResourceLocation getName();

    T getDefaultValue();

    default T clamp(T value) {
        return value;
    }

    default boolean supports(Item item) {
        return true;
    }

    T read(Tag tag);

    Tag write(T value);

    T fromNetwork(FriendlyByteBuf buffer);

    void toNetwork(FriendlyByteBuf buffer, T value);

    default String getTranslationKey() {
        return getName().toLanguageKey("tool_stat");
    }

    default MutableComponent getPrefix() {
        return Component.translatable(getTranslationKey());
    }

    default MutableComponent getDescription() {
        return Component.translatable(getTranslationKey() + ".description");
    }

    Component formatValue(T value);

    T modify(IToolPart.ModifyType modifyType, T original, T value);

    static Component formatNumber(String loc, TextColor color, int number) {
        return formatNumber(loc, color, (float) number);
    }

    static Component formatNumber(String loc, TextColor color, float number) {
        return Component.translatable(loc)
                .append(Component.literal(TooltipUtils.COMMA_FORMAT.format(number)).withStyle(style -> style.withColor(color)));
    }

    static Component formatNumberPercent(String loc, TextColor color, float number) {
        return Component.translatable(loc)
                .append(Component.literal(TooltipUtils.PERCENT_FORMAT.format(number)).withStyle(style -> style.withColor(color)));
    }

    static Component formatColored(String loc, float number, float offset, DecimalFormat format) {
        float hue = Mth.positiveModulo(offset + number, 2f);
        return Component.translatable(loc).append(Component.literal(format.format(number)).withStyle(style -> style.withColor(TextColor.fromRgb(Mth.hsvToRgb(hue / 1.5f, 1.0f, 0.75f)))));
    }

    static Component formatColoredMultiplier(String loc, float number) {
        return formatColored(loc, number, -0.5f, TooltipUtils.MULTIPLIER_FORMAT);
    }

    static Component formatColoredBonus(String loc, float number) {
        return formatColored(loc, number, 0.5f, TooltipUtils.BONUS_FORMAT);
    }

    static Component formatColoredPercentBoost(String loc, float number) {
        return formatColored(loc, number, 0.5f, TooltipUtils.PERCENT_BOOST_FORMAT);
    }
}
