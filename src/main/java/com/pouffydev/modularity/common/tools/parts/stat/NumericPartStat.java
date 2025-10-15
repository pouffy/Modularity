package com.pouffydev.modularity.common.tools.parts.stat;

import net.minecraft.network.chat.Component;

public abstract class NumericPartStat<T extends Number> extends PartStat<T> {

    abstract Component formatValue(float value);

    abstract Component formatBonus(float value);

    abstract Component formatMultiplier(float value);

    abstract Component formatPercentBoost(float value);

    abstract Component formatOverride(float value);

    @Override
    public Component formatValue(T value) {
        return formatValue(value.floatValue());
    }

    @Override
    public Component formatBonus(T value) {
        return formatBonus(value.floatValue());
    }

    @Override
    public Component formatMultiplier(T value) {
        return formatMultiplier(value.floatValue());
    }

    @Override
    public Component formatPercentBoost(T value) {
        return formatPercentBoost(value.floatValue());
    }

    @Override
    public Component formatOverride(T value) {
        return formatOverride(value.floatValue());
    }
}
