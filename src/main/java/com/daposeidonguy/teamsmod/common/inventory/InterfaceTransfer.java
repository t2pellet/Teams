package com.daposeidonguy.teamsmod.common.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;

public class InterfaceTransfer implements INamedContainerProvider {

    private final String name;

    public InterfaceTransfer(String name) {
        this.name = name;
    }

    @Override
    public Container createMenu(int i, @Nonnull final PlayerInventory playerInventory, @Nonnull final PlayerEntity playerEntity) {
        return new ContainerTransfer(i, playerInventory, name);
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return new StringTextComponent("transfer");
    }
}
