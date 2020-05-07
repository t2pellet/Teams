package com.daposeidonguy.teamsmod.client.gui.overlay;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class StatusOverlay extends AbstractGui {

    private int offsetY;
    private Minecraft mc;

    public StatusOverlay(Minecraft mc, String teamName) {
        offsetY = 0;
        this.mc = mc;
        int count = 0;
        Iterator<UUID> priorityIterator = GuiHandler.priorityPlayers.iterator();
        Iterator<UUID> teamIterator = SaveData.teamsMap.get(teamName).iterator();
        while (priorityIterator.hasNext() && count < 4) {
            UUID playerUUID = priorityIterator.next();
            if (!playerUUID.equals(mc.player.getUniqueID())) {
                renderHUDElement(playerUUID);
                count++;
                offsetY += 46;
            }
        }
        while (teamIterator.hasNext() && count < 4) {
            UUID playerUUID = teamIterator.next();
            if (!playerUUID.equals(mc.player.getUniqueID()) && !GuiHandler.priorityPlayers.contains(playerUUID)) {
                renderHUDElement(playerUUID);
                count++;
                offsetY += 46;
            }
        }
    }

    private int getWidth(Minecraft mc) {
        return mc.getMainWindow().getScaledWidth();
    }

    private int getHeight(Minecraft mc) {
        return mc.getMainWindow().getScaledHeight();
    }

    private void renderHUDElement(UUID playerUUID) {
        NetworkPlayerInfo info = mc.getConnection().getPlayerInfo(playerUUID);
        if (info != null) {
            String playerName = info.getGameProfile().getName();
            ResourceLocation skinLoc = info.getLocationSkin();
            int health = GuiHandler.healthMap.containsKey(playerUUID) ? GuiHandler.healthMap.get(playerUUID) : 20;
            int hunger = GuiHandler.hungerMap.containsKey(playerUUID) ? GuiHandler.hungerMap.get(playerUUID) : 20;

            mc.getTextureManager().bindTexture(new ResourceLocation(TeamsMod.MODID, "textures/gui/icon.png"));
            blit((int) Math.round(getWidth(mc) * 0.002) + 20, (getHeight(mc) / 4 - 5) + offsetY, 0, 0, 9, 9);
            drawString(mc.fontRenderer, String.valueOf(health), (int) Math.round(getWidth(mc) * 0.002) + 32, (getHeight(mc) / 4 - 5) + offsetY, Color.WHITE.getRGB());

            mc.getTextureManager().bindTexture(new ResourceLocation(TeamsMod.MODID, "textures/gui/icon.png"));
            blit((int) Math.round(getWidth(mc) * 0.002) + 46, (getHeight(mc) / 4 - 5) + offsetY, 9, 0, 9, 9);
            drawString(mc.fontRenderer, String.valueOf(hunger), (int) Math.round(getWidth(mc) * 0.002) + 58, (getHeight(mc) / 4 - 5) + offsetY, Color.WHITE.getRGB());

            mc.getTextureManager().bindTexture(skinLoc);
            GL11.glPushMatrix();
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            blit((int) Math.round(getWidth(mc) * 0.002) + 4, (getHeight(mc) / 2 - 34) + 2 * offsetY, 32, 32, 32, 32);
            GL11.glPopMatrix();
            drawString(mc.fontRenderer, playerName, (int) Math.round(getWidth(mc) * 0.001) + 20, (getHeight(mc) / 4 - 20) + offsetY, Color.WHITE.getRGB());
        }
    }
}
