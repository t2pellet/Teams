package com.daposeidonguy.teamsmod.client;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiTransfer extends GuiContainer {

    private InventoryPlayer playerInv;
    private static final ResourceLocation TEXTURE = new ResourceLocation(TeamsMod.MODID,"textures/gui/transfer.png");

    public GuiTransfer(Container inventorySlotsIn, InventoryPlayer playerInv) {
        super(inventorySlotsIn);
        this.playerInv = playerInv;
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        if (slotIn != null && slotIn.xPos==80 && slotIn.yPos==35) {
            slotIn.getStack().setCount(0);
        }
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
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
        fontRenderer.drawString("Transfer Item",xSize / 2 - fontRenderer.getStringWidth("Transfer Item")/2,6, Color.DARK_GRAY.getRGB());
    }
}
