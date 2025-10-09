package com.pouffydev.modularity.common.util;

import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tool.ModularItem;
import com.pouffydev.modularity.api.tool.ModularPart;
import com.pouffydev.modularity.api.tool.part.ToolPartItem;
import com.pouffydev.modularity.common.registry.ModulaDataComponents;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import com.pouffydev.modularity.common.registry.bootstrap.ModulaMaterials;
import com.pouffydev.modularity.common.tools.parts.ToolHandle;
import com.pouffydev.modularity.common.tools.parts.ToolHead;
import com.pouffydev.modularity.common.tools.parts.ToolGuard;
import com.pouffydev.modularity.datagen.server.ModulaMaterialTagProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.function.Consumer;

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

    public static Tier getTier(ItemStack stack) {
        Tier tier = Tiers.WOOD;
        if (stack.getItem() instanceof ToolPartItem partItem) {
            var partType = partItem.getType();
            var material = partItem.getMaterial(stack);
            if (material != null) {
                var part = material.value().stats().getPartOfType(partType);
                if (part instanceof ToolHead head) {
                    tier = head.tier().value();
                }
            }
        } else {
            ToolHead head = getToolHead(stack);
            if (head != null) {
                tier = head.tier().value();
            }
        }
        return tier;
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

    public static void durability(ItemStack stack, boolean init) {
        int durability = getTier(stack).getUses();
        var part = getPart(stack, ModulaToolParts.HANDLE.get());
        if (part.isPresent()) {
            ToolHandle handle = (ToolHandle) part.get().material().value().stats().getPartOfType(part.get().type());
            if (handle != null) {
                int d = durability;
                durability = Math.round(d + (d * handle.durability()));
            }
        }
        if (init && !stack.has(DataComponents.DAMAGE))
            stack.set(DataComponents.DAMAGE, 0);
        stack.set(DataComponents.MAX_DAMAGE, durability);
        stack.set(DataComponents.MAX_STACK_SIZE, 1);
    }

    public static float attackSpeed(ItemStack stack, float original) {
        var guardPart = getPart(stack, ModulaToolParts.GUARD.get());
        var handlePart = getPart(stack, ModulaToolParts.HANDLE.get());
        if (guardPart.isPresent()) {
            ToolGuard guard = (ToolGuard) guardPart.get().material().value().stats().getPartOfType(guardPart.get().type());
            original = guard != null ? original + guard.attackSpeed() : original;
        }
        if (handlePart.isPresent()) {
            ToolHandle handle = (ToolHandle) handlePart.get().material().value().stats().getPartOfType(handlePart.get().type());
            if (handle != null) {
                float a = original;
                original = (a + (a * handle.meleeSpeed()));
            }
        }
        return original;
    }

    public static float miningSpeed(ItemStack stack, float original) {
        var handlePart = getPart(stack, ModulaToolParts.HANDLE.get());
        if (handlePart.isPresent()) {
            ToolHandle handle = (ToolHandle) handlePart.get().material().value().stats().getPartOfType(handlePart.get().type());
            if (handle != null) {
                float a = original;
                original = (a + (a * handle.miningSpeed()));
            }
        }
        return original;
    }

    public static float attackDamage(ItemStack stack, float original) {
        var handlePart = getPart(stack, ModulaToolParts.HANDLE.get());
        var headPart = getPart(stack, ModulaToolParts.HEAD.get());
        if (headPart.isPresent()) {
            ToolHead head = (ToolHead) headPart.get().material().value().stats().getPartOfType(headPart.get().type());
            original = head != null ? original + head.attack() : original;
        }
        if (handlePart.isPresent()) {
            ToolHandle handle = (ToolHandle) handlePart.get().material().value().stats().getPartOfType(handlePart.get().type());
            if (handle != null) {
                float a = original;
                original = (a + (a * handle.attackDamage()));
            }
        }
        return original;
    }

    public static void addAttributes(ItemStack stack, ItemAttributeModifiers attributeModifiers) {
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, attributeModifiers);
    }

    public static void simpleTool(ItemStack stack, TagKey<Block> mineableTag) {
        stack.set(DataComponents.TOOL, getTier(stack).createToolProperties(mineableTag));
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
            stack.set(ModulaDataComponents.REINIT_ATTRIBUTES, true);
            items.accept(stack.copy());
        }
    }
}
