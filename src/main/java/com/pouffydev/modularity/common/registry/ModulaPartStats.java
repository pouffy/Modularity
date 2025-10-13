package com.pouffydev.modularity.common.registry;

import com.mojang.serialization.MapCodec;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.common.RegistryHelper;
import com.pouffydev.modularity.common.tools.parts.stat.IPartStat;
import com.pouffydev.modularity.common.tools.parts.stat.PartStat;
import com.pouffydev.modularity.common.tools.parts.stat.PartStatType;
import com.pouffydev.modularity.common.tools.parts.stat.TierStat;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModulaPartStats {
    public static final DeferredRegister<IPartStat<?>> STATS = RegistryHelper.createRegister(ModularityRegistries.PART_STAT);

    public static final DeferredHolder<IPartStat<?>, PartStat> DURABILITY = registerFloat("durability", 0xFF47CC47, 1, 1, Integer.MAX_VALUE);
    public static final DeferredHolder<IPartStat<?>, PartStat> ATTACK_DAMAGE = registerFloat("attack_damage", 0xFFD76464, 0, 0, 2048f);
    public static final DeferredHolder<IPartStat<?>, PartStat> ATTACK_SPEED = registerFloat("attack_speed", 0xFF8547CC, 1, 0, 1024f);
    public static final DeferredHolder<IPartStat<?>, PartStat> MINING_SPEED = registerFloat("mining_speed", 0xFF78A0CD, 1, 0.1f, 2048f);

    public static final DeferredHolder<IPartStat<?>, TierStat> HARVEST_TIER = registerSimple("harvest_tier", TierStat::new);

    public static <T extends IPartStat<?>> DeferredHolder<IPartStat<?>, T> registerSimple(String name, Supplier<T> supplier) {
        return STATS.register(name, supplier);
    }

    public static DeferredHolder<IPartStat<?>, PartStat> registerFloat(String name, int color, float defaultValue, float minValue, float maxValue) {
        return STATS.register(name, () -> new PartStat(color, defaultValue, minValue, maxValue));
    }

    public static void staticInit() {}
}
