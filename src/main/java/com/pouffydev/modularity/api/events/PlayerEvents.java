package com.pouffydev.modularity.api.events;

import com.pouffydev.modularity.api.tool.ModularItem;
import com.pouffydev.modularity.common.util.ToolHelpers;
import com.pouffydev.modularity.mixin.PlayerAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class PlayerEvents {

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        ItemStack itemstack = player.getMainHandItem();
        ItemStack lastItem = ((PlayerAccessor)player).getLastItemInMainHand();
        if (!ItemStack.matches(lastItem, itemstack)) {
            if (lastItem.getItem() instanceof ModularItem && itemstack.getItem() instanceof ModularItem) {
                if (!ModularItem.partsMatch(lastItem, itemstack)) {
                    player.resetAttackStrengthTicker();
                }

            }
            if (!ItemStack.isSameItem(lastItem, itemstack)) {
                player.resetAttackStrengthTicker();
            }

        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof ModularItem) {
            float speed = event.getNewSpeed();
            event.setNewSpeed(ToolHelpers.miningSpeed(stack, speed));
        }

    }
}
