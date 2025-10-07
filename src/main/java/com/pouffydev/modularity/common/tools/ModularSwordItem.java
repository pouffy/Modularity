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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
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

    @Override
    public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    private void accept(Consumer<ItemStack> output, HolderLookup.Provider lookupProvider) {
        addMaterials(output, lookupProvider);
    }

    void addMaterials(Consumer<ItemStack> items, HolderLookup.Provider lookupProvider) {
        var materialLookup = lookupProvider.lookupOrThrow(ModularityRegistries.TOOL_MATERIAL);
        for (Holder<ToolMaterial> bladeMaterial : materialLookup.listElements().toList()) {
            ModularPart blade = resolveBlade(bladeMaterial, materialLookup);
            for (Holder<ToolMaterial> hiltMaterial : materialLookup.listElements().toList()) {
                ModularPart hilt = resolveHilt(hiltMaterial, materialLookup);
                for (Holder<ToolMaterial> handleMaterial : materialLookup.listElements().toList()) {
                    ModularPart handle = resolveHandle(handleMaterial, materialLookup);
                    ItemStack stack = new ItemStack(this);
                    stack.set(ModulaDataComponents.MULTIPART, List.of(blade, handle, hilt));
                    stack.set(ModulaDataComponents.REINIT_ATTRIBUTES, true);
                    items.accept(stack.copy());
                }
            }
        }
    }

    ModularPart resolveBlade(Holder<ToolMaterial> material,  HolderLookup<ToolMaterial> materialLookup) {
        var partMaterial = material;
        if (!material.value().stats().supportsType(ModulaToolParts.HEAD.get())) {
            partMaterial = materialLookup.getOrThrow(ModulaMaterials.WOOD);
        }
        return new ModularPart(ModulaToolParts.HEAD.get(), partMaterial);
    }
    ModularPart resolveHilt(Holder<ToolMaterial> material,  HolderLookup<ToolMaterial> materialLookup) {
        var partMaterial = material;
        if (!material.value().stats().supportsType(ModulaToolParts.HILT.get())) {
            partMaterial = materialLookup.getOrThrow(ModulaMaterials.WOOD);
        }
        return new ModularPart(ModulaToolParts.HILT.get(), partMaterial);
    }
    ModularPart resolveHandle(Holder<ToolMaterial> material,  HolderLookup<ToolMaterial> materialLookup) {
        var partMaterial = material;
        if (!material.value().stats().supportsType(ModulaToolParts.HANDLE.get())) {
            partMaterial = materialLookup.getOrThrow(ModulaMaterials.WOOD);
        }
        return new ModularPart(ModulaToolParts.HANDLE.get(), partMaterial);
    }
}
