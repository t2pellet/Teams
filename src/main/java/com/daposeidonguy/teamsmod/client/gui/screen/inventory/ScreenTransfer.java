package com.daposeidonguy.teamsmod.client.gui.screen.inventory;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.inventory.ContainerTransfer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

public class ScreenTransfer extends ContainerScreen<ContainerTransfer> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TeamsMod.MODID, "textures/gui/transfer.png");
    private final PlayerInventory playerInv;
    private final ContainerTransfer container;

    public ScreenTransfer(ContainerTransfer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.container = screenContainer;
        this.playerInv = inv;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        renderBackground();
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.blit(x, y, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(title.getFormattedText(), xSize / 2 - this.font.getStringWidth(title.getFormattedText()) / 2, 6, Color.DARK_GRAY.getRGB());
    }

    @Override
    public void onClose() {
        this.playerInv.addItemStackToInventory(this.container.getSlot(0).getStack());
        super.onClose();
    }
}