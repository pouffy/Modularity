package com.pouffydev.modularity.common.tools.data;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ToolData {

    private final Item item;
    private CompoundTag nbt;
    private int damage = -1;

    @Nullable
    private StatsData stats;

    private ToolData(Item item, CompoundTag nbt) {
        this.item = item;
        this.nbt = nbt;
    }

    public static ToolData from(Item item, CompoundTag nbt) {
        return new ToolData(item, nbt);
    }

    public StatsData getStats() {
        if (stats == null) {
            stats = StatsData.readFromNBT(nbt.getCompound("stats"));
        }
        return stats;
    }
}
