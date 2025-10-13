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

public record ToolHandle(float durability, float miningSpeed, float meleeSpeed, float attackDamage) implements IToolPart {
    public static final MapCodec<ToolHandle> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.optionalFieldOf("durability", 0f).forGetter(ToolHandle::durability),
                    Codec.FLOAT.optionalFieldOf("mining_speed", 0f).forGetter(ToolHandle::miningSpeed),
                    Codec.FLOAT.optionalFieldOf("melee_speed", 0f).forGetter(ToolHandle::meleeSpeed),
                    Codec.FLOAT.optionalFieldOf("attack_damage", 0f).forGetter(ToolHandle::attackDamage)
            ).apply(instance, ToolHandle::new));

    @Override
    public ToolPartType<?> getType() {
        return ModulaToolParts.HANDLE.get();
    }

    @Override
    public void statsTooltip(Holder<ToolMaterial> material, Consumer<Component> tooltip, boolean advanced) {
        tooltip.accept(TooltipUtils.part(material, ModulaToolParts.HANDLE));
        var materialStats = material.value().stats();
        if (!materialStats.supportsType(getType())) return;
        var handleStats = (ToolHandle) materialStats.getPartOfType(getType());
        if (handleStats == null) return;
        tooltip.accept(TooltipUtils.indent(2, IPartStat.formatColoredPercentBoost("modularity.tooltip.stat.durability", handleStats.durability())));
        tooltip.accept(TooltipUtils.indent(2, IPartStat.formatColoredPercentBoost("modularity.tooltip.stat.mining_speed", handleStats.miningSpeed())));
        tooltip.accept(TooltipUtils.indent(2, IPartStat.formatColoredPercentBoost("modularity.tooltip.stat.attack_speed", handleStats.meleeSpeed())));
        tooltip.accept(TooltipUtils.indent(2, IPartStat.formatColoredPercentBoost("modularity.tooltip.stat.attack_damage", handleStats.attackDamage())));
    }

    @Override
    public List<StatModifier<?>> getStats() {
        var modifiers = new ArrayList<StatModifier<?>>();
        whenChanged(modifiers::add, ModulaPartStats.DURABILITY.get(), durability);
        whenChanged(modifiers::add, ModulaPartStats.MINING_SPEED.get(), miningSpeed);
        whenChanged(modifiers::add, ModulaPartStats.ATTACK_SPEED.get(), meleeSpeed);
        whenChanged(modifiers::add, ModulaPartStats.ATTACK_DAMAGE.get(), attackDamage);
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
        var mod = StatModifier.percentBoost(stat, value);
        if (value != 0f)
            modifier.accept(mod);
    }
}
