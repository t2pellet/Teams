package com.daposeidonguy.teamsmod.common.inventory;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class SlotTransfer extends Slot {

    private String name;

    public SlotTransfer(IInventory inventoryIn, int index, int xPosition, int yPosition, String name) {
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
            for (World w : ServerLifecycleHooks.getCurrentServer().getWorlds()) {
                for (PlayerEntity p : w.getPlayers()) {
                    if (p.getDisplayName().toString().equals(name)) {
                        if (p.inventory.getFirstEmptyStack() != -1) {
                            p.addItemStackToInventory(stack1);
                        } else {
                            w.addEntity(new ItemEntity(w, p.getPosition().getX(), p.getPosition().getY(), p.getPosition().getZ(), stack1));
                        }
                    }
                }
            }
        } else {
            getStack().setCount(0);
        }
        super.onSlotChanged();
    }
}
