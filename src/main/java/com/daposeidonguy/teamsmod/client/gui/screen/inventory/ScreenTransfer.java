package com.daposeidonguy.teamsmod.client.gui.screen.inventory;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.inventory.ContainerTransfer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

public class ScreenTransfer extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TeamsMod.MODID, "textures/gui/transfer.png");
    private final InventoryPlayer playerInv;
    private final ContainerTransfer container;
    private final ITextComponent title;

    public ScreenTransfer(final ContainerTransfer screenContainer, final InventoryPlayer inv, final ITextComponent titleIn) {
        super(screenContainer);
        this.container = screenContainer;
        this.playerInv = inv;
        this.title = titleIn;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        mc.fontRenderer.drawString(title.getFormattedText(), xSize / 2 - mc.fontRenderer.getStringWidth(title.getFormattedText()) / 2, 6, Color.DARK_GRAY.getRGB());
    }

    @Override
    public void onGuiClosed() {
        this.playerInv.addItemStackToInventory(this.container.getSlot(0).getStack());
        super.onGuiClosed();
    }
}