package com.daposeidonguy.teamsmod.common.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class InterfaceTransfer implements INamedContainerProvider {

    private String name;

    public InterfaceTransfer(String name) {
        this.name = name;
    }

    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new ContainerTransfer(i, playerInventory, name);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Transfer");
    }
}
