package com.daposeidonguy.teamsmod.client.gui;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiStatus extends Gui {

    public GuiStatus(Minecraft mc, int offsety, int health, int hunger, String name, ResourceLocation loc) {
        ScaledResolution resolution = new ScaledResolution(mc);
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();

        mc.renderEngine.bindTexture(new ResourceLocation(TeamsMod.MODID, "textures/gui/icon.png"));
        drawTexturedModalRect((int) Math.round(width * 0.001) + 20, (height / 4 - 5) + offsety, 0, 0, 9, 9);
        drawString(mc.fontRenderer, String.valueOf(health), (int) Math.round(width * 0.001) + 32, (height / 4 - 5) + offsety, Color.WHITE.getRGB());

        mc.renderEngine.bindTexture(new ResourceLocation(TeamsMod.MODID, "textures/gui/icon.png"));
        drawTexturedModalRect((int) Math.round(width * 0.001) + 46, (height / 4 - 5) + offsety, 9, 0, 9, 9);
        drawString(mc.fontRenderer, String.valueOf(hunger), (int) Math.round(width * 0.001) + 58, (height / 4 - 5) + offsety, Color.WHITE.getRGB());

        mc.renderEngine.bindTexture(loc);
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        drawTexturedModalRect((int) Math.round(width * 0.001) + 4, (height / 2 - 34) + 2 * offsety, 32, 32, 32, 32);
        GL11.glPopMatrix();
        drawString(mc.fontRenderer, name, (int) Math.round(width * 0.001) + 20, (height / 4 - 20) + offsety, Color.WHITE.getRGB());
    }
}