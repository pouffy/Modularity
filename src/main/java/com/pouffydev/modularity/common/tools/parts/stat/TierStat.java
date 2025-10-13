package com.pouffydev.modularity.common.tools.parts.stat;

import com.mojang.serialization.Codec;
import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.tier.HarvestTiers;
import com.pouffydev.modularity.api.tier.TierSortingRegistry;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.tool.SerializableTier;
import com.pouffydev.modularity.common.registry.ModulaDataComponents;
import io.netty.handler.codec.DecoderException;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;

import java.util.Objects;

public class TierStat implements IPartStat<Tier> {
    @Override
    public ResourceLocation getName() {
        return ModularityRegistries.PART_STAT_REGISTRY.getKey(this);
    }

    @Override
    public Tier getDefaultValue() {
        return HarvestTiers.minTier();
    }

    @Override
    public Tier read(Tag tag) {
        if (tag.getId() == Tag.TAG_STRING) {
            ResourceLocation tierId = ResourceLocation.tryParse(tag.getAsString());
            if (tierId != null) {
                return TierSortingRegistry.byName(tierId);
            }
        }
        return null;
    }

    @Override
    public Tag write(Tier value) {
        ResourceLocation id = TierSortingRegistry.getName(value);
        if (id != null) {
            return StringTag.valueOf(id.toString());
        }
        return null;
    }

    @Override
    public Tier fromNetwork(FriendlyByteBuf buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        Tier tier = TierSortingRegistry.byName(id);
        if (tier != null) {
            return tier;
        }
        throw new DecoderException("Unknown tool tier " + id);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, Tier value) {
        buffer.writeResourceLocation(Objects.requireNonNull(TierSortingRegistry.getName(value)));
    }

    @Override
    public Component formatValue(Tier value) {
        return Component.translatable(getName().toLanguageKey("part_stat")).append(HarvestTiers.getName(value));
    }

    @Override
    public Tier modify(IToolPart.ModifyType modifyType, Tier original, Tier value) {
        return HarvestTiers.max(original, value);
    }

    @Override
    public String toString() {
        return "TierStat{" + getName() + '}';
    }
}
