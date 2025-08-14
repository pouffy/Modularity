package com.pouffydev.modularity.common.registry;

import com.pouffydev.modularity.api.material.item.ITabFiller;
import com.pouffydev.modularity.common.RegistryHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ModulaCreativeTab {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = RegistryHelper.createRegister(Registries.CREATIVE_MODE_TAB);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MODULARITY;

    static {
        MODULARITY = registerTabSearchBar("modularity", ModulaItems.PICKAXE_HEAD, (params, output) -> {
            for (DeferredHolder<Item, ? extends Item> registry : ModulaItems.getItems()) {
                if (registry.get() instanceof ITabFiller tabFiller) {
                    tabFiller.fillItemCategory(params, output);
                    continue;
                }
                if (registry.get() instanceof BlockItem)
                    continue;
                output.accept(registry.get());
            }
        }, builder -> builder.withTabsBefore(CreativeModeTabs.COMBAT));
    }

    private static DeferredHolder<CreativeModeTab, CreativeModeTab> registerTabSearchBar(String name, Holder<Item> icon, BiConsumer<CreativeModeTab.ItemDisplayParameters, CreativeModeTab.Output> displayItems, Consumer<CreativeModeTab.Builder> additionalProperties) {
        return CREATIVE_MODE_TABS.register(name, id -> {
            final CreativeModeTab.Builder builder = CreativeModeTab.builder();
            builder.title(Component.translatable(id.toLanguageKey("itemGroup")))
                    .icon(() -> new ItemStack(icon))
                    .withSearchBar()
                    .displayItems(displayItems::accept);
            additionalProperties.accept(builder);
            return builder.build();
        });
    }

    public static void staticInit() {

    }
}
