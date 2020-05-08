package com.daposeidonguy.teamsmod.client.gui;

import com.daposeidonguy.teamsmod.team.SaveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.UUID;

public class GuiCompass extends Gui {
    private static final int WIDTH = 182;
    private static final int HEIGHT = 5;

    private ScaledResolution resolution;

    public GuiCompass(Minecraft mc, String teamName) {
        resolution = new ScaledResolution(mc);
        Iterator<UUID> uuidIterator = SaveData.teamsMap.get(teamName).iterator();
        int onlineCount = 0;
        while (uuidIterator.hasNext()) {
            UUID playerId = uuidIterator.next();
            double rotationHead = mc.player.getRotationYawHead();
            rotationHead = rotationHead % 360;
            if (rotationHead > 180) {
                rotationHead = rotationHead - 360;
            } else if (rotationHead < -180) {
                rotationHead = 360 + rotationHead;
            }
            if (!playerId.equals(mc.player.getUniqueID())) {
                EntityPlayer player = FMLClientHandler.instance().getWorldClient().getPlayerEntityByUUID(playerId);
                if (player != null) {
                    onlineCount++;
                    double diffPosX = player.posX - mc.player.posX;
                    double diffPosZ = player.posZ - mc.player.posZ;
                    double magnitude = Math.sqrt(diffPosX * diffPosX + diffPosZ * diffPosZ);
                    diffPosX /= magnitude;
                    diffPosZ /= magnitude;
                    double angle = Math.atan(diffPosZ / diffPosX) * 180 / Math.PI + 90;
                    if (diffPosX >= 0) {
                        angle -= 180;
                    }
                    double renderFactor = (angle - rotationHead) / 180;
                    if (renderFactor > 1) {
                        renderFactor = renderFactor - 2;
                    }
                    if (renderFactor < -1) {
                        renderFactor = 2 + renderFactor;
                    }
                    ResourceLocation skin = mc.getConnection().getPlayerInfo(playerId).getLocationSkin();
                    mc.getTextureManager().bindTexture(skin);
                    GL11.glPushMatrix();
                    double x = (getWidth() / 2 - WIDTH / 4 + renderFactor * WIDTH / 2 + 41);
                    double y = ((getHeight() * 0.01) + 12);
                    GL11.glScalef(0.25F, 0.25F, 0.25F);
                    if (1 - Math.abs(renderFactor) < 0.6) {
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, (float) (1.2 - Math.abs(renderFactor)));
                        drawTexturedModalRect((int) (4 * x), (int) (4 * y), 32, 32, 32, 32);
                        GL11.glDisable(GL11.GL_BLEND);
                    } else {
                        drawTexturedModalRect((int) (4 * x), (int) (4 * y), 32, 32, 32, 32);
                    }
                    GL11.glPopMatrix();
                    //drawCenteredString(mc.fontRenderer, String.valueOf(Math.round(1000 * magnitude) / 1000) + "m", (int)(x + 0.5*mc.fontRenderer.getCharWidth('m')), (int)(y + 10), Color.WHITE.getRGB());
                }
            }
        }
        if (onlineCount != 0) {
            mc.getTextureManager().bindTexture(Gui.ICONS);
            drawTexturedModalRect(getWidth() / 2 - WIDTH / 2, (int) (getHeight() * 0.01) + 5, 0, 74, WIDTH, HEIGHT);
        }
    }

    /* Returns scaled width of minecraft window */
    private int getWidth() {
        return resolution.getScaledWidth();
    }

    /* Returns scaled height of minecraft window */
    private int getHeight() {
        return resolution.getScaledHeight();
    }
}
