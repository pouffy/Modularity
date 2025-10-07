package com.pouffydev.modularity.datagen.client;

import com.pouffydev.modularity.api.material.item.IMaterialItem;
import com.pouffydev.modularity.api.material.item.MaterialItem;
import com.pouffydev.modularity.api.tool.ModularItem;
import com.pouffydev.modularity.api.tool.part.ToolPartItem;
import com.pouffydev.modularity.client.render.MaterialLoaderBuilder;
import com.pouffydev.modularity.client.render.ModularLoaderBuilder;
import com.pouffydev.modularity.common.registry.ModulaItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import team.lodestar.lodestone.systems.datagen.providers.LodestoneItemModelProvider;

import java.util.Set;
import java.util.function.Supplier;

@SuppressWarnings("ConstantValue")
public class ModulaItemModelProvider extends LodestoneItemModelProvider {

    public ModulaItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        Set<Holder<Item>> overrides = Set.of();
        for (DeferredHolder<Item, ? extends Item> item : ModulaItems.getItems()) {
            if (overrides.contains(item) || ((item.get() instanceof BlockItem))) {
                continue;
            }
            if (item.get() instanceof IMaterialItem) {
                continue;
            }
            if (item.get() instanceof ModularItem) {
                modular(item);
                continue;
            }
            basicItem(item);
        }
        material(ModulaItems.TOOL_HANDLE, "parts/tool_handle", new Vec2(0, 0));
        material(ModulaItems.PICKAXE_HEAD, "parts/pickaxe_head", new Vec2(0, 0));
        material(ModulaItems.AXE_HEAD, "parts/axe_head", new Vec2(0, 0));
        material(ModulaItems.SHOVEL_HEAD, "parts/shovel_head", new Vec2(0, 0));
        material(ModulaItems.HOE_HEAD, "parts/hoe_head", new Vec2(0, 0));
        material(ModulaItems.SWORD_BLADE, "parts/sword_blade", new Vec2(0, 0));
        material(ModulaItems.HILT, "parts/hilt", new Vec2(0, 0));

        modular(ModulaItems.SWORD);
        modular(ModulaItems.PICKAXE);
    }

    private void basicItem(Supplier<? extends Item> item) {
        super.basicItem(item.get());
    }

    private ItemModelBuilder modular(Supplier<? extends Item> item) {
        String name = BuiltInRegistries.ITEM.getKey(item.get()).getPath();
        return getBuilder(name)
                .customLoader(ModularLoaderBuilder::new).end()
                .parent(new ModelFile.UncheckedModelFile("neoforge:item/default"));
    }

    private ItemModelBuilder material(Supplier<? extends Item> item, String path, Vec2 offset) {
        String name = BuiltInRegistries.ITEM.getKey(item.get()).getPath();
        return getBuilder(name)
                .parent(new ModelFile.UncheckedModelFile("neoforge:item/default"))
                .texture("texture", modLoc("item/"+path))
                .customLoader((builder, helper) -> new MaterialLoaderBuilder(builder, helper, offset)).end();
    }

}
