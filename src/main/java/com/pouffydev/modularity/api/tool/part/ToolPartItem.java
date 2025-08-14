package com.pouffydev.modularity.api.tool.part;

import com.pouffydev.modularity.api.material.item.ITabFiller;
import com.pouffydev.modularity.api.material.item.MaterialItem;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

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

    public ToolPartType<?> getType() {
        return toolPartType.value();
    }

    private void accept(Consumer<ItemStack> output, HolderLookup.Provider lookupProvider) {
        addMaterials(output, lookupProvider);
    }
}
