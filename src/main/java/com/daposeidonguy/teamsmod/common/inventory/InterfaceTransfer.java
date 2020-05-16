package com.daposeidonguy.teamsmod.common.inventory;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;

public class InterfaceTransfer implements IInteractionObject {

    private String name;

    public InterfaceTransfer(String name) {
        this.name = name;
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerTransfer(playerInventory, name);
    }

    @Override
    public String getGuiID() {
        return "teamsmod:transfer";
    }

    @Override
    public String getName() {
        return I18n.format("teamsmod.transfer.title", name);
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString("Transfer");
    }
}
