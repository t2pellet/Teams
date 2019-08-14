package com.daposeidonguy.teamsmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiTeam extends Gui {

    public GuiTeam(Minecraft mc, int offsety, int health, int hunger, String name, ResourceLocation loc) {
        ScaledResolution resolution = new ScaledResolution(mc);
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();

        mc.renderEngine.bindTexture(new ResourceLocation("textures/gui/icons.png"));
        drawTexturedModalRect((int) Math.round(width * 0.001) + 20, (height / 4 - 5) + offsety + 16, 53, 0, 9, 9);
        drawString(mc.fontRenderer, String.valueOf(health), (int) Math.round(width * 0.001) + 30, (height / 4 - 4) + offsety+16, Color.WHITE.getRGB());

        mc.renderEngine.bindTexture(new ResourceLocation("textures/gui/icons.png"));
        drawTexturedModalRect((int) Math.round(width * 0.001) + 46, (height / 4 - 5) + offsety+16, 16, 36, 9, 9);
        drawString(mc.fontRenderer, String.valueOf(hunger), (int) Math.round(width * 0.001) + 58, (height / 4 - 5) + offsety+16, Color.WHITE.getRGB());


        mc.renderEngine.bindTexture(loc);
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        drawTexturedModalRect((int) Math.round(width * 0.001) + 4, (height / 2 - 16) + 2 * offsety, 32, 32, 32, 32);
        GL11.glPopMatrix();
        drawString(mc.fontRenderer,name, (int) Math.round(width * 0.001) + 20, (height / 4 - 4) + offsety, Color.WHITE.getRGB());
    }
}
