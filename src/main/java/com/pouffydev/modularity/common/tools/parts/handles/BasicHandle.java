package com.pouffydev.modularity.common.tools.parts.handles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.common.registry.ModulaPartStats;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import com.pouffydev.modularity.common.tools.parts.fittings.Pommel;
import com.pouffydev.modularity.common.util.TooltipUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record BasicHandle(float durability, float miningSpeed, float meleeSpeed, float attackDamage) implements IToolPart {
    public static final MapCodec<BasicHandle> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.optionalFieldOf("durability", 0f).forGetter(BasicHandle::durability),
                    Codec.FLOAT.optionalFieldOf("mining_speed", 0f).forGetter(BasicHandle::miningSpeed),
                    Codec.FLOAT.optionalFieldOf("melee_speed", 0f).forGetter(BasicHandle::meleeSpeed),
                    Codec.FLOAT.optionalFieldOf("attack_damage", 0f).forGetter(BasicHandle::attackDamage)
            ).apply(instance, BasicHandle::new));

    public static final StreamCodec<ByteBuf, BasicHandle> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, BasicHandle::durability,
            ByteBufCodecs.FLOAT, BasicHandle::miningSpeed,
            ByteBufCodecs.FLOAT, BasicHandle::meleeSpeed,
            ByteBufCodecs.FLOAT, BasicHandle::attackDamage,
            BasicHandle::new
    );

    @Override
    public ToolPartType<?> getType() {
        return ModulaToolParts.BASIC_HANDLE.get();
    }

    @Override
    public void statsTooltip(Holder<ToolMaterial> material, Consumer<Component> tooltip, boolean advanced) {
        tooltip.accept(TooltipUtils.part(material, ModulaToolParts.BASIC_HANDLE));
        var materialStats = material.value().stats();
        if (!materialStats.supportsType(getType())) return;
        for(StatModifier<?> modifier : getStats()) {
            tooltip.accept(TooltipUtils.indent(2, modifier.format()));
        }
    }

    @Override
    public List<StatModifier<?>> getStats() {
        var modifiers = new ArrayList<StatModifier<?>>();
        modifiers.add(StatModifier.percentBoost(ModulaPartStats.DURABILITY.get(), durability));
        modifiers.add(StatModifier.percentBoost(ModulaPartStats.MINING_SPEED.get(), miningSpeed));
        modifiers.add(StatModifier.percentBoost(ModulaPartStats.ATTACK_SPEED.get(), meleeSpeed));
        modifiers.add(StatModifier.percentBoost(ModulaPartStats.ATTACK_DAMAGE.get(), attackDamage));
        return modifiers;
    }
}
