package com.pouffydev.modularity.api.material.parts;

import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class ToolPartType<T extends IToolPart> {

    private final T defaultStats;
    private final boolean canRepair;

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolPartType<?>> STREAM_CODEC = ByteBufCodecs.registry(ModularityRegistries.TOOL_PART_TYPE);


    public ToolPartType(T defaultStats) {
        this.defaultStats = defaultStats;
        this.canRepair = defaultStats instanceof IRepairableToolPart;
    }

    public ToolPartType(Function<ToolPartType<T>,T> defaultStatsProvider) {
        this.defaultStats = defaultStatsProvider.apply(this);
        this.canRepair = defaultStats instanceof IRepairableToolPart;
    }
}
