package com.daposeidonguy.teamsmod.client.gui.overlay;

import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import java.util.Iterator;
import java.util.UUID;

public class CompassOverlay extends AbstractGui {

    private static final int HUD_WIDTH = 182;
    private static final int HUD_HEIGHT = 5;

    private final Minecraft mc;
    private final int scaledWidth;
    private final int scaledHeight;

    public CompassOverlay(final Minecraft mc, final String teamName) {
        this.mc = mc;
        this.scaledWidth = mc.getMainWindow().getScaledWidth();
        this.scaledHeight = mc.getMainWindow().getScaledHeight();

        double rotationHead = caculateRotationHead();
        Iterator<UUID> uuidIterator = StorageHelper.getTeamPlayers(teamName).iterator();
        int onlineCount = 0;
        while (uuidIterator.hasNext()) {
            UUID playerId = uuidIterator.next();
            if (!playerId.equals(mc.player.getUniqueID())) {
                PlayerEntity player = mc.world.getPlayerByUuid(playerId);
                if (player != null) {
                    ++onlineCount;
                    double renderFactor = calculateRenderFactor(player, rotationHead);
                    ResourceLocation skin = mc.getConnection().getPlayerInfo(playerId).getLocationSkin();
                    renderHUDHead(skin, renderFactor);
                }
            }
        }
        if (onlineCount != 0) {
            mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
            blit(scaledWidth / 2 - HUD_WIDTH / 2, (int) (scaledHeight * 0.01) + 5, 0, 74, HUD_WIDTH, HUD_HEIGHT);
        }
    }


    private double caculateRotationHead() {
        double rotationHead = mc.player.getRotationYawHead() % 360;
        if (rotationHead > 180) {
            rotationHead = rotationHead - 360;
        } else if (rotationHead < -180) {
            rotationHead = 360 + rotationHead;
        }
        return rotationHead;
    }

    private double calculateRenderFactor(final PlayerEntity player, final double rotationHead) {
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
        return renderFactor;
    }

    private void renderHUDHead(final ResourceLocation skin, final double renderFactor) {
        mc.getTextureManager().bindTexture(skin);
        int x = (int) (scaledWidth / 2 - HUD_WIDTH / 4 + renderFactor * HUD_WIDTH / 2 + 41);
        int y = (int) ((scaledHeight * 0.01) + 12);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(0.25F, 0.25F, 0.25F);
        if (1 - Math.abs(renderFactor) < 0.6) {
            RenderSystem.enableBlend();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, (float) (1.2 - Math.abs(renderFactor)));
            blit(4 * x, 4 * y, 32, 32, 32, 32);
            RenderSystem.disableBlend();
        } else {
            blit(4 * x, 4 * y, 32, 32, 32, 32);
        }
        RenderSystem.popMatrix();
    }
}
