package com.daposeidonguy.teamsmod.client.gui.widget;

import net.minecraft.client.Minecraft;

import java.awt.*;

/* Button with no background */
public class ClearButton extends AbstractButton {

    public ClearButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, IPress press) {
        super(buttonId, x, y, widthIn, heightIn, buttonText, press);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        Minecraft.getMinecraft().fontRenderer.drawString(this.displayString, x, y, Color.GRAY.getRGB());
    }
}
