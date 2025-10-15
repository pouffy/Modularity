package com.pouffydev.modularity.common.tools.parts.stat;

import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.common.util.ModUtil;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class FloatPartStat extends NumericPartStat<Float> {

    private final TextColor color;
    private final float defaultValue;
    private final float minValue;
    private final float maxValue;

    public FloatPartStat(int color, float defaultValue, float minValue, float maxValue) {
        super();
        this.color = TextColor.fromRgb(color);
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public Float getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Float clamp(Float value) {
        return Mth.clamp(value, getMinValue(), getMaxValue());
    }

    @Override
    public Float modify(IToolPart.ModifyType modifyType, Float original, Float value) {
        return switch (modifyType) {
            case BONUS -> original + value;
            case MULTIPLIER -> original * value;
            case PERCENT_BOOST -> original + (original * value);
            case OVERRIDE -> value;
        };
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

    @Nullable
    @Override
    public Float read(Tag tag) {
        if (ModUtil.isNumeric(tag)) {
            return ((NumericTag) tag).getAsFloat();
        }
        return null;
    }

    @Override
    public Tag write(Float value) {
        return FloatTag.valueOf(value);
    }

    @Override
    public Float fromNetwork(FriendlyByteBuf buffer) {
        return buffer.readFloat();
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, Float value) {
        buffer.writeFloat(value);
    }

    @Override
    public Component formatValue(float value) {
        return PartStat.formatNumber(getTranslationKey(), getColor(), value);
    }

    @Override
    public Component formatBonus(float value) {
        return PartStat.formatBonus(getTranslationKey(), getColor(), value);
    }

    @Override
    public Component formatMultiplier(float value) {
        return PartStat.formatMultiplier(getTranslationKey(), getColor(), value);
    }

    @Override
    public Component formatPercentBoost(float value) {
        return PartStat.formatPercentBoost(getTranslationKey(), getColor(), value);
    }

    @Override
    public Component formatOverride(float value) {
        return PartStat.formatNumber(getTranslationKey(), getColor(), value);
    }

    @Override
    public String toString() {
        return "FloatPartStat{" + getName() + '}';
    }
}
