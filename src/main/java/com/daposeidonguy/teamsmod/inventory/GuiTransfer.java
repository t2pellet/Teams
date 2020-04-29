package com.daposeidonguy.teamsmod.inventory;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiTransfer extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TeamsMod.MODID, "textures/gui/transfer.png");
    private InventoryPlayer playerInv;
    private ContainerTransfer container;

    public GuiTransfer(ContainerTransfer inventorySlotsIn, InventoryPlayer playerInv) {
        super(inventorySlotsIn);
        this.container = inventorySlotsIn;
        this.playerInv = playerInv;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString("Transfer Item: " + container.getName(), xSize / 2 - fontRenderer.getStringWidth("Transfer Item: " + container.getName()) / 2, 6, Color.DARK_GRAY.getRGB());
    }

    @Override
    public void onGuiClosed() {
        this.playerInv.addItemStackToInventory(this.container.getSlot(0).getStack());
        super.onGuiClosed();
    }
}