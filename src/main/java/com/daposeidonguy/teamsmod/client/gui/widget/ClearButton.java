package com.daposeidonguy.teamsmod.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;

import java.awt.*;

public class ClearButton extends Button {

    public ClearButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, String message, IPressable onPressIn) {
        super(xIn, yIn, widthIn, heightIn, message, onPressIn);
    }

    @Override
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft.getInstance().fontRenderer.drawString(this.getMessage(), x, y, Color.GRAY.getRGB());
    }
}
