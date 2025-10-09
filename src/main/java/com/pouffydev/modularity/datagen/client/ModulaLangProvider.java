package com.pouffydev.modularity.datagen.client;

import com.pouffydev.modularity.api.ModularityRegistries;
import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.api.tool.SerializableTier;
import com.pouffydev.modularity.common.registry.ModulaCreativeTab;
import com.pouffydev.modularity.common.registry.ModulaItems;
import com.pouffydev.modularity.common.registry.ModulaToolParts;
import com.pouffydev.modularity.common.registry.bootstrap.ModulaMaterials;
import com.pouffydev.modularity.common.registry.bootstrap.ModulaTiers;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
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

        for (DeferredHolder< ToolPartType<?>, ? extends ToolPartType<?>> registry : ModulaToolParts.TOOL_PARTS.getEntries()) {
            this.add(registry, "tool_part_type");
        }

        this.tab(ModulaCreativeTab.MODULARITY);

        material(ModulaMaterials.WOOD);
        material(ModulaMaterials.STONE);
        material(ModulaMaterials.IRON);
        material(ModulaMaterials.DIAMOND);
        material(ModulaMaterials.GOLD);
        material(ModulaMaterials.NETHERITE);
        material(ModulaMaterials.NETHER_WOOD);
        material(ModulaMaterials.UNKNOWN);

        tier(ModulaTiers.WOOD);
        tier(ModulaTiers.STONE);
        tier(ModulaTiers.IRON);
        tier(ModulaTiers.DIAMOND);
        tier(ModulaTiers.GOLD);
        tier(ModulaTiers.NETHERITE);

        string("modularity.tooltip.keyShift", "Shift");
        string("modularity.tooltip.holdForStats", "Hold [%1$s] for Stats");
        string("modularity.tooltip.holdForParts", "Hold [%1$s] for Parts");

        statsTooltip("durability");
        statsTooltip("mining_speed");
        statsTooltip("attack_damage");
        statsTooltip("attack_speed");
        statsTooltip("enchantability");
        statsTooltip("tier");
    }

    private void statsTooltip(String type) {
        String name = transform(type);
        string("modularity.tooltip.stat."+type, name+" ");
    }

    private void material(ResourceKey<ToolMaterial> key) {
        String name = transform(key.location().getPath());
        material(key, name);
    }

    private void material(ResourceKey<ToolMaterial> key, String name) {
        super.add(key.location().toLanguageKey("tool_material"), name);
    }

    private void tier(ResourceKey<SerializableTier> key) {
        String name = transform(key.location().getPath());
        super.add(key.location().toLanguageKey("tool_tier"), name);
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

    private void string(String key, String value) {
        super.add(key, value);
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
