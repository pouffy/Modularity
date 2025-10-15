package com.pouffydev.modularity.common.registry;

import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.common.RegistryHelper;
import com.pouffydev.modularity.common.tools.parts.stat.FloatPartStat;
import com.pouffydev.modularity.common.tools.parts.stat.PartStat;
import com.pouffydev.modularity.common.tools.parts.stat.TierStat;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModulaPartStats {
    public static final DeferredRegister<PartStat<?>> STATS = RegistryHelper.createRegister(ModularityRegistries.PART_STAT);

    //General
    public static final DeferredHolder<PartStat<?>, FloatPartStat> DURABILITY = registerFloat("durability", 0xFF47CC47, 1, 1, Integer.MAX_VALUE);

    //Combat
    public static final DeferredHolder<PartStat<?>, FloatPartStat> CRIT_CHANCE = registerFloat("crit_chance", 0xFFFFB162, 0, 0, 1024f);
    public static final DeferredHolder<PartStat<?>, FloatPartStat> ATTACK_DAMAGE = registerFloat("attack_damage", 0xFFD76464, 0, 0, 2048f);
    public static final DeferredHolder<PartStat<?>, FloatPartStat> ATTACK_SPEED = registerFloat("attack_speed", 0xFF8547CC, 1, 0, 1024f);

    //Harvest
    public static final DeferredHolder<PartStat<?>, TierStat> HARVEST_TIER = registerSimple("harvest_tier", TierStat::new);
    public static final DeferredHolder<PartStat<?>, FloatPartStat> MINING_SPEED = registerFloat("mining_speed", 0xFF78A0CD, 1, 0.1f, 2048f);

    public static <T extends PartStat<?>> DeferredHolder<PartStat<?>, T> registerSimple(String name, Supplier<T> supplier) {
        return STATS.register(name, supplier);
    }

    public static DeferredHolder<PartStat<?>, FloatPartStat> registerFloat(String name, int color, float defaultValue, float minValue, float maxValue) {
        return STATS.register(name, () -> new FloatPartStat(color, defaultValue, minValue, maxValue));
    }

    public static void staticInit() {}
}
