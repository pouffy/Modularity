package com.pouffydev.modularity.api.material.item;

import net.minecraft.world.item.CreativeModeTab;

public interface ITabFiller {
    void fillItemCategory(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output);

}
