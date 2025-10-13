package com.pouffydev.modularity.api.tool.part;

import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.common.registry.ModulaDataComponents;
import com.pouffydev.modularity.common.util.ToolHelpers;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

public class DamageablePartItem extends ToolPartItem {

    public DamageablePartItem(Properties properties, Holder<ToolPartType<?>> toolPartType) {
        super(properties, toolPartType);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        if (stack.has(ModulaDataComponents.REINIT_COMPONENTS)) {
            initializeComponents(stack);
        }
    }

    public void initializeComponents(ItemStack stack) {
        stack.remove(ModulaDataComponents.REINIT_COMPONENTS);
        ToolHelpers.durability(stack, true);
    }
}
