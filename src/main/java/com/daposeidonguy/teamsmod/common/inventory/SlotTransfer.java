package com.daposeidonguy.teamsmod.common.inventory;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class SlotTransfer extends Slot {

    private String name;

    public SlotTransfer(IInventory inventoryIn, int index, int xPosition, int yPosition, String name) {
        super(inventoryIn, index, xPosition, yPosition);
        this.name = name;
    }

    @Override
    public void onSlotChanged() {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && getHasStack()) {
            ItemStack stack = getStack();
            ItemStack stack1 = stack.copy();
            stack.setCount(0);
            NBTTagCompound tagStack = new NBTTagCompound();
            stack1.writeToNBT(tagStack);
            for (EntityPlayer p : FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().playerEntities) {
                if (p.getDisplayNameString().equals(name)) {
                    if (p.inventory.getFirstEmptyStack() != -1) {
                        p.addItemStackToInventory(stack1);
                    } else {
                        FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().spawnEntity(new EntityItem(FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld(), p.getPosition().getX(), p.getPosition().getY(), p.getPosition().getZ(), stack1));
                    }
                }
            }
        } else {
            getStack().setCount(0);
        }
        super.onSlotChanged();
    }
}
