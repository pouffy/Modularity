package com.pouffydev.modularity.common.tools.parts.stat;

import net.minecraft.network.chat.Component;

public interface INumericPartStat<T extends Number> extends IPartStat<T> {

    Component formatValue(float value);

    @Override
    default Component formatValue(T value) {
        return formatValue(value.floatValue());
    }
}
