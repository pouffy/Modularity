package com.pouffydev.modularity.common.registry;

import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tool.part.ToolPartItem;
import com.pouffydev.modularity.common.RegistryHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.function.Supplier;

public class ModulaItems {
    private static final Supplier<Item> SIMPLE_SUPPLIER = () -> new Item(new Item.Properties());
    public static final DeferredRegister.Items ITEMS = RegistryHelper.createRegister(DeferredRegister::createItems);

    // Parts
    public static final DeferredItem<ToolPartItem> TOOL_HANDLE = registerToolPart("tool_handle", ModulaToolParts.HANDLE);
    public static final DeferredItem<ToolPartItem> PICKAXE_HEAD = registerToolPart("pickaxe_head", ModulaToolParts.HEAD);
    public static final DeferredItem<ToolPartItem> AXE_HEAD = registerToolPart("axe_head", ModulaToolParts.HEAD);
    public static final DeferredItem<ToolPartItem> SHOVEL_HEAD = registerToolPart("shovel_head", ModulaToolParts.HEAD);
    public static final DeferredItem<ToolPartItem> HOE_HEAD = registerToolPart("hoe_head", ModulaToolParts.HEAD);
    public static final DeferredItem<ToolPartItem> SWORD_BLADE = registerToolPart("sword_blade", ModulaToolParts.HEAD);
    public static final DeferredItem<ToolPartItem> HILT = registerToolPart("hilt", ModulaToolParts.HILT);


    private static DeferredItem<Item> registerSimple(String name) {
        return register(name, SIMPLE_SUPPLIER);
    }

    public static DeferredItem<ToolPartItem> registerToolPart(String name, Holder<ToolPartType<?>> partType) {
        return register(name, () -> new ToolPartItem(new Item.Properties(), partType));
    }

    private static <T extends Item> DeferredItem<T> register(String id, Supplier<T> pIProp) {
        return ITEMS.register(id.toLowerCase(), pIProp);
    }

    public static void staticInit() {
        Modularity.LOGGER.info("Registering Items");
    }

    public static Collection<DeferredHolder<Item, ? extends Item>> getItems() {
        return ITEMS.getEntries();
    }
}
