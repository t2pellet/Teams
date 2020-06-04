package com.daposeidonguy.teamsmod.client.gui.overlay;

import com.daposeidonguy.teamsmod.client.ClientHelper;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class CompassOverlay extends Gui {

    private static final int HUD_WIDTH = 182;
    private static final int HUD_HEIGHT = 5;

    private final Minecraft mc;
    private final int scaledWidth;
    private final int scaledHeight;

    public CompassOverlay(final Minecraft mc, final String teamName) {
        this.mc = mc;
        ScaledResolution res = new ScaledResolution(mc);
        this.scaledWidth = res.getScaledWidth();
        this.scaledHeight = res.getScaledHeight();

        double rotationHead = caculateRotationHead();
        Iterator<UUID> uuidIterator = StorageHelper.getTeamPlayers(teamName).iterator();
        int onlineCount = 0;
        while (uuidIterator.hasNext()) {
            UUID playerId = uuidIterator.next();
            if (!playerId.equals(mc.player.getUniqueID())) {
                NetworkPlayerInfo player = mc.getConnection().getPlayerInfo(playerId);
                if (player != null) {
                    Pair<Integer, Vec2f> posPair = ClientHelper.idtoPosMap.get(playerId);
                    if (posPair == null || posPair.first() != mc.player.getEntityWorld().provider.getDimension()) {
                        continue;
                    }
                    ++onlineCount;
                    double magnitude = calculateMagnitude(posPair.second());
                    double renderFactor = calculateRenderFactor(posPair.second(), rotationHead, magnitude);
                    ResourceLocation skin = player.getLocationSkin();
                    renderHUDHead(skin, renderFactor, magnitude);
                } else {
                }
            }
        }
        if (onlineCount != 0) {
            mc.getTextureManager().bindTexture(ICONS);
            drawTexturedModalRect(scaledWidth / 2 - HUD_WIDTH / 2, (int) (scaledHeight * 0.01) + 10, 0, 74, HUD_WIDTH, HUD_HEIGHT);
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

    private double calculateMagnitude(final Vec2f pos) {
        double diffPosX = pos.x - mc.player.posX;
        double diffPosZ = pos.y - mc.player.posZ;
        return Math.sqrt(diffPosX * diffPosX + diffPosZ * diffPosZ);
    }

    private double calculateRenderFactor(final Vec2f pos, final double rotationHead, final double magnitude) {
        double diffPosX = pos.x - mc.player.posX;
        double diffPosZ = pos.y - mc.player.posZ;
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

    private void renderHUDHead(final ResourceLocation skin, final double renderFactor, final double magnitude) {
        mc.getTextureManager().bindTexture(skin);
        int x = (int) (scaledWidth / 2 - HUD_WIDTH / 4 + renderFactor * HUD_WIDTH / 2 + 41);
        int y = (int) ((scaledHeight * 0.01) + 16);
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.25F, 0.25F, 0.25F);
        String strMagnitude = String.valueOf(Math.round(magnitude * 1000) / 1000) + "m";
        if (1 - Math.abs(renderFactor) < 0.6) {
            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, (float) (1.2 - Math.abs(renderFactor)));
            drawTexturedModalRect(4 * x, 4 * y, 32, 32, 32, 32);
            Color colorText = new Color(1.0F, 1.0F, 1.0F, (float) (1.2 - Math.abs(renderFactor)));
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            drawString(mc.fontRenderer, strMagnitude, 2 * x - mc.fontRenderer.getStringWidth(strMagnitude) / 2 + 8, 2 * y + 17, colorText.getRGB());
            GlStateManager.disableBlend();
        } else {
            drawTexturedModalRect(4 * x, 4 * y, 32, 32, 32, 32);
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            drawString(mc.fontRenderer, strMagnitude, 2 * x - mc.fontRenderer.getStringWidth(strMagnitude) / 2 + 8, 2 * y + 17, Color.WHITE.getRGB());
        }
        GlStateManager.popMatrix();
    }
}
