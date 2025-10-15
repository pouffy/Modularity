package com.pouffydev.modularity.common.tools.parts.stat;

import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.common.util.TooltipUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public abstract class PartStat<T> {
    private final Holder.Reference<PartStat<?>> builtInRegistryHolder;

    protected PartStat() {
        this.builtInRegistryHolder = ModularityRegistries.PART_STAT_REGISTRY.createIntrusiveHolder(this);
    }

    public abstract T getDefaultValue();

    public T clamp(T value) {
        return value;
    }

    public boolean supports(Item item) {
        return true;
    }

    public abstract T read(Tag tag);

    public abstract Tag write(T value);

    public abstract T fromNetwork(FriendlyByteBuf buffer);

    public abstract void toNetwork(FriendlyByteBuf buffer, T value);

    public ResourceLocation getName() {
        return ModularityRegistries.PART_STAT_REGISTRY.getKey(this);
    }

    public ResourceKey<PartStat<?>> getKey() {
        return this.builtInRegistryHolder.getKey();
    }

    public String getTranslationKey() {
        return getName().toLanguageKey("part_stat");
    }

    public MutableComponent getPrefix() {
        return Component.translatable(getTranslationKey());
    }

    public MutableComponent getDescription() {
        return Component.translatable(getTranslationKey() + ".description");
    }

    public Component formatValue(IToolPart.ModifyType modifyType, T value) {
        var fallback = formatValue(value);
        return switch (modifyType) {
            case BONUS -> formatBonus(value) != null ? formatBonus(value) : fallback;
            case MULTIPLIER -> formatMultiplier(value) != null ? formatMultiplier(value) : fallback;
            case PERCENT_BOOST -> formatPercentBoost(value) != null ? formatPercentBoost(value) : fallback;
            case OVERRIDE -> formatOverride(value) != null ? formatOverride(value) : fallback;
        };
    }

    public abstract Component formatValue(T value);

    @Nullable abstract Component formatBonus(T value);

    @Nullable abstract Component formatMultiplier(T value);

    @Nullable abstract Component formatPercentBoost(T value);

    @Nullable abstract Component formatOverride(T value);

    public abstract T modify(IToolPart.ModifyType modifyType, T original, T value);

    static Component formatNumber(String loc, TextColor color, float number) {
        return Component.translatable(loc)
                .append(Component.literal(TooltipUtils.COMMA_FORMAT.format(number)).withStyle(style -> style.withColor(color)));
    }

    static Component formatMultiplier(String loc, TextColor color, float number) {
        return Component.translatable(loc)
                .append(Component.literal(TooltipUtils.MULTIPLIER_FORMAT.format(number)).withStyle(style -> style.withColor(color)));
    }

    static Component formatPercentBoost(String loc, TextColor color, float number) {
        return Component.translatable(loc)
                .append(Component.literal(TooltipUtils.PERCENT_BOOST_FORMAT.format(number)).withStyle(style -> style.withColor(color)));
    }

    static Component formatBonus(String loc, TextColor color, float number) {
        return Component.translatable(loc)
                .append(Component.literal(TooltipUtils.BONUS_FORMAT.format(number)).withStyle(style -> style.withColor(color)));
    }

    public boolean is(TagKey<PartStat<?>> tag) {
        return this.builtInRegistryHolder.is(tag);
    }

    public boolean is(HolderSet<PartStat<?>> entityType) {
        return entityType.contains(this.builtInRegistryHolder);
    }

    public Holder.Reference<PartStat<?>> builtInRegistryHolder() {
        return this.builtInRegistryHolder;
    }

    public Stream<TagKey<PartStat<?>>> getTags() {
        return this.builtInRegistryHolder().tags();
    }
}
