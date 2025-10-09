package com.pouffydev.modularity.common.tools;

import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.item.ITabFiller;
import com.pouffydev.modularity.api.tool.ModularItem;
import com.pouffydev.modularity.api.tool.ModularPart;
import com.pouffydev.modularity.common.registry.ModulaDataComponents;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import com.pouffydev.modularity.common.registry.bootstrap.ModulaMaterials;
import com.pouffydev.modularity.common.tools.parts.ToolHead;
import com.pouffydev.modularity.common.util.ToolHelpers;
import com.pouffydev.modularity.datagen.server.ModulaMaterialTagProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;
import java.util.function.Consumer;

public class ModularPickaxeItem extends ModularItem implements ITabFiller {

    public ModularPickaxeItem(Properties properties) {
        super(properties, 1);
    }

    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(itemAbility);
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
    }

    public static ItemAttributeModifiers createAttributes(Tier tier, float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
    }

    @Override
    public void initializeComponents(ItemStack stack) {
        ToolHead head = ToolHelpers.getToolHead(stack);
        Tier tier = ToolHelpers.getTier(stack);
        float attackDamage = ToolHelpers.attackDamage(stack, 0);
        float attackSpeed = ToolHelpers.attackSpeed(stack, -2.8F);
        ToolHelpers.addAttributes(stack, createAttributes(tier, attackDamage, attackSpeed));
        ToolHelpers.simpleTool(stack, BlockTags.MINEABLE_WITH_PICKAXE);
        ToolHelpers.durability(stack, true);
    }

    @Override
    public void fillItemCategory(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        accept(output::accept, params.holders());
    }

    private void accept(Consumer<ItemStack> output, HolderLookup.Provider lookupProvider) {
        ToolHelpers.resolveTool(this, output, lookupProvider, ModulaToolParts.HEAD.get());
    }
}
