package com.pouffydev.modularity.common.registry;

import com.pouffydev.modularity.Modularity;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tool.part.DamageablePartItem;
import com.pouffydev.modularity.api.tool.part.ToolPartItem;
import com.pouffydev.modularity.common.RegistryHelper;
import com.pouffydev.modularity.common.tools.*;
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

    // Tool Heads (can be damaged)
    public static final DeferredItem<DamageablePartItem> PICKAXE_HEAD = registerDamageableToolPart("pickaxe_head", ModulaToolParts.HEAD);
    public static final DeferredItem<DamageablePartItem> AXE_HEAD = registerDamageableToolPart("axe_head", ModulaToolParts.HEAD);
    public static final DeferredItem<DamageablePartItem> SHOVEL_HEAD = registerDamageableToolPart("shovel_head", ModulaToolParts.HEAD);
    public static final DeferredItem<DamageablePartItem> HOE_HEAD = registerDamageableToolPart("hoe_head", ModulaToolParts.HEAD);
    public static final DeferredItem<DamageablePartItem> SWORD_BLADE = registerDamageableToolPart("sword_blade", ModulaToolParts.HEAD);
    // Extra Parts
    public static final DeferredItem<ToolPartItem> GUARD = registerToolPart("guard", ModulaToolParts.GUARD);
    public static final DeferredItem<ToolPartItem> TOOL_HANDLE = registerToolPart("tool_handle", ModulaToolParts.HANDLE);

    // Tools
    public static final DeferredItem<ModularSwordItem> SWORD = register("sword", () -> new ModularSwordItem(new Item.Properties()));
    public static final DeferredItem<ModularPickaxeItem> PICKAXE = register("pickaxe", () -> new ModularPickaxeItem(new Item.Properties()));
    public static final DeferredItem<ModularHoeItem> HOE = register("hoe", () -> new ModularHoeItem(new Item.Properties()));
    public static final DeferredItem<ModularShovelItem> SHOVEL = register("shovel", () -> new ModularShovelItem(new Item.Properties()));
    public static final DeferredItem<ModularAxeItem> AXE = register("axe", () -> new ModularAxeItem(new Item.Properties()));

    private static DeferredItem<Item> registerSimple(String name) {
        return register(name, SIMPLE_SUPPLIER);
    }

    public static DeferredItem<ToolPartItem> registerToolPart(String name, Holder<ToolPartType<?>> partType) {
        return register(name, () -> new ToolPartItem(new Item.Properties(), partType));
    }
    public static DeferredItem<DamageablePartItem> registerDamageableToolPart(String name, Holder<ToolPartType<?>> partType) {
        return register(name, () -> new DamageablePartItem(new Item.Properties(), partType));
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
