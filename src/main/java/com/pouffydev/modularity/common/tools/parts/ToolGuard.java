package com.pouffydev.modularity.common.tools.parts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.common.registry.ModulaPartStats;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import com.pouffydev.modularity.common.tools.parts.stat.IPartStat;
import com.pouffydev.modularity.common.util.TooltipUtils;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record ToolGuard(float attackSpeed) implements IToolPart {
    public static final MapCodec<ToolGuard> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.optionalFieldOf("speed", 0f).forGetter(ToolGuard::attackSpeed)
            ).apply(instance, ToolGuard::new));

    @Override
    public ToolPartType<?> getType() {
        return ModulaToolParts.GUARD.get();
    }

    @Override
    public void statsTooltip(Holder<ToolMaterial> material, Consumer<Component> tooltip, boolean advanced) {
        tooltip.accept(TooltipUtils.part(material, ModulaToolParts.GUARD));
        var materialStats = material.value().stats();
        if (!materialStats.supportsType(getType())) return;
        var hiltStats = (ToolGuard) materialStats.getPartOfType(getType());
        if (hiltStats == null) return;
        tooltip.accept(TooltipUtils.indent(2, IPartStat.formatColoredBonus("modularity.tooltip.stat.attack_speed", hiltStats.attackSpeed())));
    }

    @Override
    public List<StatModifier<?>> getStats() {
        var modifiers = new ArrayList<StatModifier<?>>();
        whenChanged(modifiers::add, ModulaPartStats.ATTACK_SPEED.get(), attackSpeed);
        return modifiers;
    }

    /**
     * Simple test to only add the stats if the values aren't the default empty values.
     * Prevents components being added for stats that aren't altered.
     * @param modifier Adds the modifier to the list.
     * @param stat The part stat to modify
     * @param value The value to apply
     */
    void whenChanged(Consumer<StatModifier<?>> modifier, IPartStat<Float> stat, float value) {
        var mod = StatModifier.bonus(stat, value);
        if (value != 0f)
            modifier.accept(mod);
    }
}
