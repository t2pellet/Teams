package com.daposeidonguy.teamsmod.client.gui.overlay;

import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.UUID;

public class CompassOverlay extends AbstractGui {

    private static final int WIDTH = 182;
    private static final int HEIGHT = 5;

    private Minecraft mc;

    public CompassOverlay(Minecraft mc, String teamName) {
        this.mc = mc;
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
                AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) mc.world.getPlayerByUuid(playerId);
                if (player != null && player.getEntityWorld().getDimension() == mc.player.world.getDimension()) {
                    onlineCount++;
                    double diffPosX = player.getPosX() - mc.player.getPosX();
                    double diffPosZ = player.getPosZ() - mc.player.getPosZ();
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
                    GL11.glScalef(0.25F, 0.25F, 0.25F);
                    int x = (int) (4 * (getWidth() / 2 - WIDTH / 4 + renderFactor * WIDTH / 2 + 41));
                    int y = (int) (4 * ((getHeight() * 0.01) + 12));
                    if (1 - Math.abs(renderFactor) < 0.6) {
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, (float) (1.2 - Math.abs(renderFactor)));
                        blit(x, y, 32, 32, 32, 32);
                        GL11.glDisable(GL11.GL_BLEND);
                    } else {
                        blit(x, y, 32, 32, 32, 32);
                    }
                    GL11.glPopMatrix();
                }
            }
        }
        if (onlineCount != 0) {
            mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
            blit(getWidth() / 2 - WIDTH / 2, (int) (getHeight() * 0.01) + 5, 0, 74, WIDTH, HEIGHT);
        }
    }

    /* Returns scaled width of minecraft window */
    private int getWidth() {
        return mc.getMainWindow().getScaledWidth();
    }

    /* Returns scaled height of minecraft window */
    private int getHeight() {
        return mc.getMainWindow().getScaledHeight();
    }
}
