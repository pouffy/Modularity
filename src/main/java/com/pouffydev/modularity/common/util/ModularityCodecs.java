package com.pouffydev.modularity.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.function.Function;

public class ModularityCodecs {

    public static final Codec<Float> NON_NEGATIVE_FLOAT = floatRangeMinInclusiveWithMessage(0, Float.MAX_VALUE, (value) -> "Value must be non-negative: " + value);

    private static Codec<Float> floatRangeMinExclusiveWithMessage(float min, float max, Function<Float, String> errorMessage) {
        return Codec.FLOAT.validate((value) -> value.compareTo(min) > 0 && value.compareTo(max) <= 0 ? DataResult.success(value) : DataResult.error(() -> errorMessage.apply(value)));
    }
    private static Codec<Float> floatRangeMinInclusiveWithMessage(float min, float max, Function<Float, String> errorMessage) {
        return Codec.FLOAT.validate((value) -> value.compareTo(min) >= 0 && value.compareTo(max) <= 0 ? DataResult.success(value) : DataResult.error(() -> errorMessage.apply(value)));
    }
}
