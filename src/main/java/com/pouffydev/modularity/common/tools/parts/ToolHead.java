package com.pouffydev.modularity.common.tools.parts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.IRepairableToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tier.HarvestTiers;
import com.pouffydev.modularity.api.tier.TierSortingRegistry;
import com.pouffydev.modularity.api.tool.SerializableTier;
import com.pouffydev.modularity.common.registry.ModulaPartStats;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import com.pouffydev.modularity.common.tools.parts.stat.IPartStat;
import com.pouffydev.modularity.common.util.TooltipUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Tier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public record ToolHead(int durability, float miningSpeed, Tier tier, float attack) implements IRepairableToolPart {

    public static ToolHead EMPTY = new ToolHead(0, 0f, SerializableTier.EMPTY, 0f);

    public static final MapCodec<ToolHead> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("durability", 0).forGetter(ToolHead::durability),
                    Codec.FLOAT.optionalFieldOf("speed", 0f).forGetter(ToolHead::miningSpeed),
                    HarvestTiers.TIER_CODEC.fieldOf("tier").forGetter(ToolHead::tier),
                    ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("attack", 0f).forGetter(ToolHead::attack)
            ).apply(instance, ToolHead::new));

    @Override
    public ToolPartType<?> getType() {
        return ModulaToolParts.HEAD.get();
    }

    @Override
    public void statsTooltip(Holder<ToolMaterial> material, Consumer<Component> tooltip, boolean advanced) {
        tooltip.accept(TooltipUtils.part(material, ModulaToolParts.HEAD));
        var materialStats = material.value().stats();
        if (!materialStats.supportsType(getType())) return;
        String tierKey = "modularity.tooltip.stat.tier";
        var headStats = (ToolHead) materialStats.getPartOfType(getType());
        if (headStats == null) return;
        Tier tier = headStats.tier();
        String tierName = Objects.requireNonNull(TierSortingRegistry.getName(tier)).toLanguageKey("tool_tier");
        Component durability = IPartStat.formatNumber("modularity.tooltip.stat.durability", TextColor.fromLegacyFormat(ChatFormatting.BLUE), headStats.durability());
        Component speed = IPartStat.formatNumber("modularity.tooltip.stat.mining_speed", TextColor.fromLegacyFormat(ChatFormatting.BLUE), headStats.miningSpeed() + tier.getSpeed());
        Component attack = IPartStat.formatNumber("modularity.tooltip.stat.attack_damage", TextColor.fromLegacyFormat(ChatFormatting.BLUE), headStats.attack() + tier.getAttackDamageBonus());
        tooltip.accept(TooltipUtils.indent(2, Component.translatable(tierKey).append(Component.translatable(tierName).withStyle(ChatFormatting.BLUE))));
        tooltip.accept(TooltipUtils.indent(2, durability));
        tooltip.accept(TooltipUtils.indent(2, speed));
        tooltip.accept(TooltipUtils.indent(2, attack));
    }


    @Override
    public boolean isMainPart() {
        return true;
    }

    @Override
    public List<StatModifier<?>> getStats() {
        var modifiers = new ArrayList<StatModifier<?>>();
        whenChanged(modifiers::add, ModulaPartStats.DURABILITY.get(), (float)durability);
        whenChanged(modifiers::add, ModulaPartStats.MINING_SPEED.get(), miningSpeed);
        whenChanged(modifiers::add, ModulaPartStats.ATTACK_DAMAGE.get(), attack);
        modifiers.add(StatModifier.override(ModulaPartStats.HARVEST_TIER.get(), tier));
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
        if (value != 0)
            modifier.accept(mod);
    }
}
