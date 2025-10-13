package com.pouffydev.modularity.common.util;

import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tool.ModularItem;
import com.pouffydev.modularity.api.tool.ModularPart;
import com.pouffydev.modularity.api.tool.SerializableTier;
import com.pouffydev.modularity.common.registry.ModulaDataComponents;
import com.pouffydev.modularity.common.registry.ModulaPartStats;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import com.pouffydev.modularity.common.registry.bootstrap.ModulaMaterials;
import com.pouffydev.modularity.common.tools.data.StatsData;
import com.pouffydev.modularity.common.tools.parts.ToolHead;
import com.pouffydev.modularity.common.tools.parts.stat.IPartStat;
import com.pouffydev.modularity.datagen.server.ModulaMaterialTagProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.function.TriFunction;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

import static com.pouffydev.modularity.common.registry.ModulaDataComponents.TOOL_STATS;

public class ToolHelpers {

    public static ToolHead getToolHead(ItemStack stack) {
        ToolHead head = ToolHead.EMPTY;
        if (stack.getItem() instanceof ModularItem) {
            var material = ModularItem.getMainMaterial(stack);
            if (material != null) {
                head = (ToolHead) material.value().stats().getPartOfType(ModulaToolParts.HEAD.get());
            }
        }
        return head;
    }

    public static <T> T getStat(ItemStack stack, IPartStat<T> stat) {
        return getStat(stack, stat, stat.getDefaultValue());
    }

    public static <T> T getStat(ItemStack stack, IPartStat<T> stat, T defaultValue) {
        StatsData statsData = stack.getOrDefault(TOOL_STATS, StatsData.EMPTY);
        T value = statsData.hasStat(stat) ? statsData.get(stat) : defaultValue;
        if (stack.has(ModulaDataComponents.REINIT_COMPONENTS.get())) {
            var modifiers = getStatModifiers(stack, stat);
            StatsData.Builder statsBuilder = new StatsData.Builder(statsData.getStats());
            for (var mod : modifiers) {
                statsBuilder.set(stat, mod.apply(value));
            }
            stack.set(TOOL_STATS, statsBuilder.build());
        }
        return value;
    }

    public static float getStatWithBase(ItemStack stack, IPartStat<Float> stat, float defaultValue, float baseValue) {
        StatsData statsData = stack.getOrDefault(TOOL_STATS, StatsData.EMPTY);
        float value = statsData.hasStat(stat) ? statsData.get(stat) : defaultValue;
        if (stack.has(ModulaDataComponents.REINIT_COMPONENTS.get())) {
            var modifiers = getStatModifiers(stack, stat);
            StatsData.Builder statsBuilder = new StatsData.Builder(statsData.getStats());
            for (var mod : modifiers) {
                statsBuilder.set(stat, mod.apply(value));
            }
            stack.set(TOOL_STATS, statsBuilder.build());
        }
        return baseValue + value;
    }

    public static boolean hasPart(ItemStack stack, ToolPartType<?> partType) {
        if (stack.getItem() instanceof ModularItem) {
            var parts = ModularItem.getPartsFromStack(stack);
            if (parts != null) {
                return parts.stream().anyMatch(p -> p.type().equals(partType));
            }
        }
        return false;
    }

    public static Optional<ModularPart> getPart(ItemStack stack, ToolPartType<?> partType) {
        if (hasPart(stack, partType)) {
                var parts = ModularItem.getPartsFromStack(stack);
            if (parts != null) {
                return parts.stream().filter(p -> p.type().equals(partType)).findFirst();
            }
        }
        return Optional.empty();
    }

    public static List<IToolPart.StatModifier<?>> getAllStatModifiers(ItemStack stack) {
        List<IToolPart.StatModifier<?>> statModifiers = new ArrayList<>();
        var parts = ModularItem.getPartsFromStack(stack);
        for (var partType : parts) {
            var part = partType.material().value().stats().getPartOfType(partType.type());
            if (part == null) continue;
            statModifiers.addAll(part.getStats().stream().toList());
        }
        return statModifiers;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<IToolPart.StatModifier<T>> getStatModifiers(ItemStack stack, IPartStat<T> type) {
        List<IToolPart.StatModifier<T>> statModifiers = new ArrayList<>();
        var parts = ModularItem.getPartsFromStack(stack);
        for (var partType : parts) {
            var part = partType.material().value().stats().getPartOfType(partType.type());
            if (part == null) continue;
            part.getStats().stream().filter(mod -> mod.test(type)).toList()
                    .forEach(s -> statModifiers.add((IToolPart.StatModifier<T>) s));
        }
        return statModifiers;
    }

    public static void durability(ItemStack stack, boolean init) {
        Tier tier = getStat(stack, ModulaPartStats.HARVEST_TIER.get());
        int durability = Math.round(getStat(stack, ModulaPartStats.DURABILITY.get(), (float)tier.getUses()));
        if (init && !stack.has(DataComponents.DAMAGE))
            stack.set(DataComponents.DAMAGE, 0);
        stack.set(DataComponents.MAX_DAMAGE, durability);
        stack.set(DataComponents.MAX_STACK_SIZE, 1);
    }

    public static float attackSpeed(ItemStack stack, float original) {
        return getStatWithBase(stack, ModulaPartStats.ATTACK_SPEED.get(), 1, original);
    }

    public static float miningSpeed(ItemStack stack, float original) {
        return getStatWithBase(stack, ModulaPartStats.MINING_SPEED.get(), 1, original);
    }

    public static float attackDamage(ItemStack stack, float original) {
        return getStatWithBase(stack, ModulaPartStats.ATTACK_DAMAGE.get(), 0, original);
    }

    public static void addAttributes(ItemStack stack, ItemAttributeModifiers attributeModifiers) {
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, attributeModifiers);
    }

    public static void initCommonComponents(ItemStack stack, TriFunction<Tier, Float, Float, ItemAttributeModifiers> attributesFunc, float damage, float speed, @Nullable TagKey<Block> tag) {
        refreshStats(stack);
        Tier tier = ToolHelpers.getStat(stack, ModulaPartStats.HARVEST_TIER.get());
        float attackDamage = ToolHelpers.attackDamage(stack, damage);
        float attackSpeed = ToolHelpers.attackSpeed(stack, speed);
        ToolHelpers.addAttributes(stack, attributesFunc.apply(tier, attackDamage, attackSpeed));
        if (tag != null)
            ToolHelpers.simpleTool(stack, tag);
        ToolHelpers.durability(stack, true);
        stack.remove(ModulaDataComponents.REINIT_COMPONENTS);
    }

    public static void refreshStats(ItemStack stack) {
        StatsData statsData = stack.getOrDefault(TOOL_STATS, StatsData.EMPTY);
        StatsData.Builder statsBuilder = new StatsData.Builder(statsData.getStats());
        getAllStatModifiers(stack).forEach(s -> s.write(statsData, statsBuilder));
        stack.set(TOOL_STATS, statsBuilder.build());
    }

    public static void simpleTool(ItemStack stack, TagKey<Block> mineableTag) {
        stack.set(DataComponents.TOOL, getStat(stack, ModulaPartStats.HARVEST_TIER.get()).createToolProperties(mineableTag));
    }

    // Netherite tools use nether wood handles
    public static ResourceKey<ToolMaterial> tabHandleMaterial(Holder<ToolMaterial> headMaterial) {
        if (headMaterial.is(ModulaMaterialTagProvider.netherMetals)) {
            return ModulaMaterials.NETHER_WOOD;
        }
        return ModulaMaterials.WOOD;
    }

    public static ModularPart resolveForPart(Holder<ToolMaterial> priority, HolderLookup<ToolMaterial> materialLookup, ToolPartType<?> part) {
        Holder<ToolMaterial> resolved;
        if (priority.value().stats().supportsType(part)) {
            resolved = priority;
        } else {
            resolved = materialLookup.listElements().toList().stream().filter(m -> m.value().stats().supportsType(part)).findFirst().orElse(materialLookup.getOrThrow(ModulaMaterials.UNKNOWN));
        }
        return new ModularPart(part, resolved);
    }

    public static void resolveTool(Item item, Consumer<ItemStack> items, HolderLookup.Provider lookupProvider, ToolPartType<?>... mainParts) {
        var materialLookup = lookupProvider.lookupOrThrow(ModularityRegistries.TOOL_MATERIAL);
        for (Holder<ToolMaterial> headMaterial : materialLookup.listElements().toList()) {
            List<ModularPart> parts = new ArrayList<>();
            for (ToolPartType<?> partType : mainParts) {
                parts.add(ToolHelpers.resolveForPart(headMaterial, materialLookup, partType));
            }
            Holder<ToolMaterial> handleMaterial = materialLookup.getOrThrow(ToolHelpers.tabHandleMaterial(headMaterial));
            ModularPart handle = ToolHelpers.resolveForPart(handleMaterial, materialLookup, ModulaToolParts.HANDLE.get());
            parts.add(handle);
            ItemStack stack = new ItemStack(item);
            stack.set(ModulaDataComponents.MULTIPART, parts);
            stack.set(ModulaDataComponents.REINIT_COMPONENTS, true);
            items.accept(stack.copy());
        }
    }
}
