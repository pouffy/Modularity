package com.pouffydev.modularity.common.registry;

import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.tool.part.ToolPartItem;
import com.pouffydev.modularity.common.RegistryHelper;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModulaItems {
    private static final Supplier<Item> SIMPLE_SUPPLIER = () -> new Item(new Item.Properties());
    public static final DeferredRegister.Items ITEMS = RegistryHelper.createRegister(DeferredRegister::createItems);

    public static final DeferredItem<ToolPartItem> SWORD_BLADE = registerToolPart("sword_blade");

    private static DeferredItem<Item> registerSimple(String name) {
        return register(name, SIMPLE_SUPPLIER);
    }

    public static DeferredItem<ToolPartItem> registerToolPart(String name) {
        return register(name, () -> new ToolPartItem(new Item.Properties()));
    }

    private static <T extends Item> DeferredItem<T> register(String id, Supplier<T> pIProp) {
        return ITEMS.register(id.toLowerCase(), pIProp);
    }

    public static void staticInit() {
        Modularity.LOGGER.info("Registering Items");
    }
}
