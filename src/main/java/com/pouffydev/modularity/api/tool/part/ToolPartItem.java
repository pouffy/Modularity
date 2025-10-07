package com.pouffydev.modularity.api.tool.part;

import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.item.ITabFiller;
import com.pouffydev.modularity.api.material.item.MaterialItem;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.common.util.TooltipUtils;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.function.Consumer;

public class ToolPartItem extends MaterialItem implements ITabFiller {
    public final Holder<ToolPartType<?>> toolPartType;

    @Override
    public void fillItemCategory(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        accept(output::accept, params.holders());
    }

    public ToolPartItem(Properties properties, Holder<ToolPartType<?>> toolPartType) {
        super(properties);
        this.toolPartType = toolPartType;
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Holder<ToolMaterial> material = getMaterial(stack);
        TooltipUtils.material(material, tooltipComponents::add, tooltipFlag.isAdvanced());
        TooltipUtils.singleStats(material, getType(), tooltipComponents::add, tooltipFlag);
    }

    public ToolPartType<?> getType() {
        return toolPartType.value();
    }

    public boolean canUseMaterial(Holder<ToolMaterial> materialHolder) {
        if (materialHolder == null) return false;
        if (!materialHolder.value().stats().supportsType(getType())) return false;
        return super.canUseMaterial(materialHolder);
    }

    private void accept(Consumer<ItemStack> output, HolderLookup.Provider lookupProvider) {
        addMaterials(output, lookupProvider);
    }
}
