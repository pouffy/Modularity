package com.pouffydev.modularity.datagen.client;

import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.common.registry.ModulaCreativeTab;
import com.pouffydev.modularity.common.registry.ModulaItems;
import com.pouffydev.modularity.common.registry.bootstrap.ModulaMaterials;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.NoSuchElementException;
import java.util.Set;

@SuppressWarnings("ConstantValue")
public class ModulaLangProvider extends LanguageProvider {
    public ModulaLangProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
    }

    @Override
    protected void addTranslations() {
        Set<Holder<Item>> overrides = Set.of();
        for (DeferredHolder<Item, ? extends Item> registry : ModulaItems.getItems()) {
            if (registry.get() instanceof BlockItem) continue;
            if (overrides.contains(registry)) {
                continue;
            }
            this.item(registry);
        }

        this.tab(ModulaCreativeTab.MODULARITY);

        for (ResourceKey<ToolMaterial> key : ModulaMaterials.getMaterials()) {
            String name = transform(key.location().getPath());
            super.add(key.location().toLanguageKey("tool_material"), name);
        }
    }

    private void tab(Holder<CreativeModeTab> tabHolder) {
        this.add(tabHolder, "itemGroup");
    }

    private void block(Holder<Block> blockHolder) {
        this.add(blockHolder, "block");
    }

    private void item(Holder<Item> itemHolder) {
        this.add(itemHolder, "item");
    }

    private void add(Holder<?> holder, String type) {
        ResourceKey<?> resourceKey = holder.unwrapKey().orElseThrow(() -> new NoSuchElementException("No respective key. Check log"));
        ResourceLocation path = resourceKey.location();
        super.add(path.toLanguageKey(type), this.transform(path));
    }

    private String transform(ResourceLocation id) {
        return this.transform(id.getPath());
    }

    private String transform(String path) {
        int pathLength = path.length();
        StringBuilder stringBuilder = new StringBuilder(pathLength).append(Character.toUpperCase(path.charAt(0)));
        for (int i = 1; i < pathLength; i++) {
            char posChar = path.charAt(i);
            if (posChar == '_') {
                stringBuilder.append(' ');
            } else if (path.charAt(i - 1) == '_') {
                stringBuilder.append(Character.toUpperCase(posChar));
            } else stringBuilder.append(posChar);
        }
        return stringBuilder.toString();
    }
}
