package com.pouffydev.modularity.api.tool;

import com.pouffydev.modularity.api.material.ToolMaterial;
import com.pouffydev.modularity.api.material.parts.ToolPartType;
import com.pouffydev.modularity.common.registry.ModulaDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class ModularItemWrapper implements IModularItem {
    protected ItemStack modularItem;

    public ModularItemWrapper(ItemStack modularItem) {
        this.modularItem = modularItem;
    }

    public ItemStack getModularItem() {
        return this.modularItem;
    }

    public static class Single extends ModularItemWrapper {
        public Single(ItemStack modularItem) {
            super(modularItem);
        }

        @Override
        public List<ModularPart> getParts() {
            ModularPart part = modularItem.get(ModulaDataComponents.PART);
            return part != null ? List.of(part) : List.of();
        }

        @Override
        public List<ModularPart> getPartsForMaterial(ResourceKey<ToolMaterial> material) {
            ModularPart part = modularItem.get(ModulaDataComponents.PART);
            if (part != null && part.material().is(material)) {
                return List.of(part);
            }
            return List.of();
        }

        @Override
        public Holder<ToolMaterial> getMaterialForPart(ToolPartType<?> partType) {
            ModularPart part = modularItem.get(ModulaDataComponents.PART);
            Holder<ToolMaterial> material = modularItem.get(ModulaDataComponents.MATERIAL);
            if (part != null) {
                if (part.type().equals(partType)) {
                    return material != null ? material : part.material();
                }
            }
            return null;
        }

        @Override
        public void setMaterial(ToolPartType<?> partType, Holder<ToolMaterial> material) {
            ModularPart part = modularItem.get(ModulaDataComponents.PART);
            if (part != null) {
                if (part.type().equals(partType)) {
                    ModularPart newPart = new ModularPart(part.type(), material);
                    modularItem.set(ModulaDataComponents.PART, newPart);
                }
            }
        }
    }

    public static class Multipart extends ModularItemWrapper {
        public Multipart(ItemStack modularItem) {
            super(modularItem);
        }

        @Override
        public List<ModularPart> getParts() {
            if (!modularItem.has(ModulaDataComponents.MULTIPART)) return List.of();
            return modularItem.get(ModulaDataComponents.MULTIPART);
        }

        @Override
        public List<ModularPart> getPartsForMaterial(ResourceKey<ToolMaterial> material) {
            if (!modularItem.has(ModulaDataComponents.MULTIPART)) return List.of();
            List<ModularPart> parts = modularItem.get(ModulaDataComponents.MULTIPART);
            if (parts != null) {
                parts.removeIf(part -> !part.material().is(material));
            } else return List.of();
            return parts;
        }

        @Override
        public Holder<ToolMaterial> getMaterialForPart(ToolPartType<?> partType) {
            if (!modularItem.has(ModulaDataComponents.MULTIPART)) return null;
            List<ModularPart> parts = modularItem.get(ModulaDataComponents.MULTIPART);
            if (parts != null) {
                for (ModularPart part : parts) {
                    if (part.type().equals(partType)) {
                        return part.material();
                    }
                }
            }
            return null;
        }

        @Override
        public void setMaterial(ToolPartType<?> partType, Holder<ToolMaterial> material) {
            if (!modularItem.has(ModulaDataComponents.MULTIPART)) return;
            List<ModularPart> parts = modularItem.get(ModulaDataComponents.MULTIPART);
            if (parts != null) {
                for (ModularPart part : parts) {
                    if (part.type().equals(partType)) {
                        ModularPart newPart = new ModularPart(part.type(), material);
                        parts.remove(part);parts.add(newPart);
                        modularItem.set(ModulaDataComponents.MULTIPART, parts);
                    }
                }
            }
        }
    }
}
