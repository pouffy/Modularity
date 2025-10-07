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
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
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
        return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, (double)(attackDamage + tier.getAttackDamageBonus()), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, (double)attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
    }

    @Override
    public void initializeAttributes(ItemStack stack) {
        Holder<ToolMaterial> mainMaterial = this.getMainMaterial(stack);
        if (mainMaterial == null) {
            return;
        }
        ToolHead head = (ToolHead) mainMaterial.value().stats().getPartOfType(ModulaToolParts.HEAD.get());
        if (head == null) {
            return;
        }
        Tier tier = head.tier().value();
        float attackDamage = head.attack();
        float attackSpeed = head.miningSpeed();
        ItemAttributeModifiers attributes = createAttributes(tier, attackDamage, attackSpeed);
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, attributes);
    }

    @Override
    public void fillItemCategory(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        accept(output::accept, params.holders());
    }

    private void accept(Consumer<ItemStack> output, HolderLookup.Provider lookupProvider) {
        addMaterials(output, lookupProvider);
    }

    void addMaterials(Consumer<ItemStack> items, HolderLookup.Provider lookupProvider) {
        var materialLookup = lookupProvider.lookupOrThrow(ModularityRegistries.TOOL_MATERIAL);
        for (Holder<ToolMaterial> headMaterial : materialLookup.listElements().toList()) {
            ModularPart head = resolveHead(headMaterial, materialLookup);
            for (Holder<ToolMaterial> handleMaterial : materialLookup.listElements().toList()) {
                ModularPart handle = resolveHandle(handleMaterial, materialLookup);
                ItemStack stack = new ItemStack(this);
                stack.set(ModulaDataComponents.MULTIPART, List.of(head, handle));
                stack.set(ModulaDataComponents.REINIT_ATTRIBUTES, true);
                items.accept(stack.copy());
            }
        }
    }
    ModularPart resolveHead(Holder<ToolMaterial> material,  HolderLookup<ToolMaterial> materialLookup) {
        var partMaterial = material;
        if (!material.value().stats().supportsType(ModulaToolParts.HEAD.get())) {
            partMaterial = materialLookup.getOrThrow(ModulaMaterials.UNKNOWN);
        }
        return new ModularPart(ModulaToolParts.HEAD.get(), partMaterial);
    }
    ModularPart resolveHandle(Holder<ToolMaterial> material,  HolderLookup<ToolMaterial> materialLookup) {
        var partMaterial = material;
        if (!material.value().stats().supportsType(ModulaToolParts.HANDLE.get())) {
            partMaterial = materialLookup.getOrThrow(ModulaMaterials.UNKNOWN);
        }
        return new ModularPart(ModulaToolParts.HANDLE.get(), partMaterial);
    }
}
