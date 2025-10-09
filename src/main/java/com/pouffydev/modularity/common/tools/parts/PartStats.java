package com.pouffydev.modularity.common.tools.parts;

import com.pouffydev.modularity.common.tools.parts.stat.FloatPartStat;
import com.pouffydev.modularity.common.tools.parts.stat.IPartStat;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class PartStats {
    private static final Map<ResourceLocation,IPartStat<?>> ALL_STATS = new HashMap<>();


    public static <T extends IPartStat<?>> T register(T partStat) {
        if (ALL_STATS.containsKey(partStat.getName())) {
            throw new IllegalArgumentException("Attempt to register duplicate part stat " + partStat.getName());
        }
        ALL_STATS.put(partStat.getName(), partStat);
        return partStat;
    }
}
