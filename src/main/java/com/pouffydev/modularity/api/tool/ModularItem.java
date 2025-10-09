package com.pouffydev.modularity.api.tool;

import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.IToolPart;
import com.pouffydev.modularity.common.registry.ModulaDataComponents;
import com.pouffydev.modularity.common.registry.ModulaItemAbilities;
import com.pouffydev.modularity.common.util.TooltipUtils;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModularItem extends TieredItem {

    private final int maxStackSize;

    public ModularItem(Properties properties) {
        this(properties, 1);
    }

    public ModularItem(Properties properties, int maxStackSize) {
        // By default, we want to reinitialize the attributes when loading the items
        super(EmptyTier.INSTANCE, properties.component(ModulaDataComponents.REINIT_ATTRIBUTES, true));
        this.maxStackSize = maxStackSize;
    }

    public String getItemName(ItemStack stack) {
        String itemName = Component.translatable(stack.getDescriptionId()).getString();
        Holder<ToolMaterial> material = getMainMaterial(stack);
        String materialName = "";
        if (material != null) {
            String materialLangKey = Util.makeDescriptionId("tool_material", material.getKey().location());
            materialName = Component.translatable(materialLangKey).getString() + " ";
        }

        return materialName + itemName;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(getItemName(stack));
    }

    public static Holder<ToolMaterial> getMainMaterial(ItemStack stack) {
        var comp = getPartsFromStack(stack);
        if (comp != null) {
            for (ModularPart part : comp) {
                ToolMaterial material = part.material().value();
                IToolPart toolPart = material.stats().getPartOfType(part.type());
                if (toolPart != null && toolPart.isMainPart()) {
                    return part.material();
                }
            }
        }
        return null;
    }

    public static boolean partsMatch(ItemStack stack1, ItemStack stack2) {
        List<ModularPart> parts1 = getPartsFromStack(stack1);
        List<ModularPart> parts2 = getPartsFromStack(stack2);
        if (parts1 == null || parts2 == null) return false;
        if (parts1.size() != parts2.size()) return false;
        for (int i = 0; i < parts1.size(); i++) {
            ModularPart part1 = parts1.get(i);
            ModularPart part2 = parts2.get(i);
            if (!part1.equals(part2)) return false;
        }
        return true;
    }

    public static List<ModularPart> getPartsFromStack(ItemStack stack) {
        if (stack.getItem() instanceof ModularItem modularItem) {
            return modularItem.getParts(stack);
        }
        return null;
    }

    public List<ModularPart> getParts(ItemStack stack) {
        if (!stack.has(ModulaDataComponents.MULTIPART)) return null;
        return stack.get(ModulaDataComponents.MULTIPART);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return stack.isDamaged() ? 1 : maxStackSize;
    }

    @Override
    public boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot) {
        return true;
    }

    @Nullable
    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        if (stack.is(Tags.Items.TOOLS_SHIELD)) {
            return EquipmentSlot.OFFHAND;
        }
        return null;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        if (!stack.has(DataComponents.ATTRIBUTE_MODIFIERS) || stack.has(ModulaDataComponents.REINIT_ATTRIBUTES)) {
            initializeComponents(stack);
        }
    }

    /**
     * Initialize the data components of the item.
     * Used for setting the attributes of the item based on its parts.
     * @param stack The item stack to initialize.
     */
    public void initializeComponents(ItemStack stack) {
        stack.remove(ModulaDataComponents.REINIT_ATTRIBUTES);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {

    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return canPerformAction(stack, ModulaItemAbilities.SHIELD_DISABLE);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (oldStack == newStack) {
            return false;
        }
        if (slotChanged || oldStack.getItem() != newStack.getItem()) {
            return true;
        }
        return false;
    }


    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return shouldCauseReequipAnimation(oldStack, newStack, false);
    }

    public static BlockHitResult blockRayTrace(Level worldIn, Player player, ClipContext.Fluid fluidMode) {
        return Item.getPlayerPOVHitResult(worldIn, player, fluidMode);
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Holder<ToolMaterial> mainMaterial = getMainMaterial(stack);
        TooltipUtils.material(mainMaterial, tooltipComponents::add, tooltipFlag.isAdvanced());
        TooltipUtils.modularStats(getParts(stack), tooltipComponents::add, tooltipFlag);
    }
}
