package com.pouffydev.modularity.common.tools.parts.stat;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FloatPartStat implements INumericPartStat<Float> {

    private final ResourceLocation name;

    private final TextColor color;
    private final float defaultValue;
    private final float minValue;
    private final float maxValue;
    private final DataComponentType<Float> dataComponentType;

    public FloatPartStat(ResourceLocation name, int color, float defaultValue, float minValue, float maxValue, DataComponentType<Float> dataComponentType) {
        this.name = name;
        this.color = TextColor.fromRgb(color);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.dataComponentType = dataComponentType;
    }

    @Override
    public ResourceLocation getName() {
        return null;
    }

    @Override
    public Float getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Float clamp(Float value) {
        return Mth.clamp(value, getMinValue(), getMaxValue());
    }

    public float getMinValue() {
        return minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public TextColor getColor() {
        return color;
    }

    @Override
    public @Nullable Float read(ItemStack stack) {
        return stack.getOrDefault(dataComponentType, getDefaultValue());
    }

    @Override
    public void write(ItemStack stack, Float value) {
        stack.set(dataComponentType, value);
    }

    @Override
    public Codec<Float> codec() {
        return Codec.FLOAT;
    }

    @Override
    public Component formatValue(float value) {
        return IPartStat.formatNumber(getTranslationKey(), getColor(), value);
    }

    @Override
    public String toString() {
        return "FloatPartStat{" + name + '}';
    }
}
