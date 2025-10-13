package com.pouffydev.modularity.common.tools;

import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.item.ITabFiller;
import com.pouffydev.modularity.api.tool.ModularItem;
import com.pouffydev.modularity.api.tool.ModularPart;
import com.pouffydev.modularity.common.registry.ModulaDataComponents;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import com.pouffydev.modularity.common.util.ToolHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;
import java.util.function.Consumer;

public class ModularSwordItem extends ModularItem implements ITabFiller {
    public ModularSwordItem(Properties properties) {
        super(properties.component(DataComponents.TOOL, SwordItem.createToolProperties()), 1);
    }

    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(itemAbility);
    }

    public static ItemAttributeModifiers createAttributes(Tier tier, float damage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID,
                                damage + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID,
                                attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public void initializeComponents(ItemStack stack) {
        ToolHelpers.initCommonComponents(stack,
                ModularSwordItem::createAttributes, 3, -2.4F, null);
    }

    @Override
    public void fillItemCategory(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        accept(output::accept, params.holders());
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    private void accept(Consumer<ItemStack> output, HolderLookup.Provider lookupProvider) {
        ToolHelpers.resolveTool(this, output, lookupProvider, ModulaToolParts.HEAD.get(), ModulaToolParts.GUARD.get());
    }

    void addMaterials(Consumer<ItemStack> items, HolderLookup.Provider lookupProvider) {
        var materialLookup = lookupProvider.lookupOrThrow(ModularityRegistries.TOOL_MATERIAL);
        for (Holder<ToolMaterial> bladeMaterial : materialLookup.listElements().toList()) {
            ModularPart blade = ToolHelpers.resolveForPart(bladeMaterial, materialLookup, ModulaToolParts.HEAD.get());
            ModularPart hilt = ToolHelpers.resolveForPart(bladeMaterial, materialLookup, ModulaToolParts.GUARD.get());
            Holder<ToolMaterial> handleMaterial = materialLookup.getOrThrow(ToolHelpers.tabHandleMaterial(bladeMaterial));
            ModularPart handle = ToolHelpers.resolveForPart(handleMaterial, materialLookup, ModulaToolParts.HANDLE.get());
            ItemStack stack = new ItemStack(this);
            stack.set(ModulaDataComponents.MULTIPART, List.of(blade, handle, hilt));
            stack.set(ModulaDataComponents.REINIT_COMPONENTS, true);
            items.accept(stack.copy());
        }
    }
}
