package com.daposeidonguy.teamsmod.common.inventory;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

class SlotTransfer extends Slot {

    private final String name;

    public SlotTransfer(final IInventory inventoryIn, int index, int xPosition, int yPosition, final String name) {
        super(inventoryIn, index, xPosition, yPosition);
        this.name = name;
    }

    @Override
    public void onSlotChanged() {
        if (EffectiveSide.get().isServer() && getHasStack()) {
            ItemStack stack = getStack();
            ItemStack stack1 = stack.copy();
            stack.setCount(0);
            CompoundNBT tagStack = new CompoundNBT();
            stack1.write(tagStack);
            ServerPlayerEntity p = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUsername(name);
            if (p == null) {
                return;
            }
            if (p.inventory.getFirstEmptyStack() != -1) {
                p.addItemStackToInventory(stack1);
            } else {
                p.getEntityWorld().addEntity(new ItemEntity(p.getEntityWorld(), p.getPosX(), p.getPosY(), p.getPosZ(), stack1));
            }
        } else {
            getStack().setCount(0);
        }
        super.onSlotChanged();
    }
}
