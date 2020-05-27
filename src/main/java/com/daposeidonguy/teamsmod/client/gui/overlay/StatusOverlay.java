package com.daposeidonguy.teamsmod.client.gui.overlay;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class StatusOverlay extends AbstractGui {

    private final Minecraft mc;
    private final int scaledWidth;
    private final int scaledHeight;
    private int offsetY;
    private int count;

    public StatusOverlay(final Minecraft mc, final String teamName) {
        this.mc = mc;
        this.scaledWidth = mc.getMainWindow().getScaledWidth();
        this.scaledHeight = mc.getMainWindow().getScaledHeight();
        this.offsetY = 0;
        this.count = 0;
        Iterator<UUID> priorityIterator = GuiHandler.priorityPlayers.iterator();
        Iterator<UUID> teamIterator = StorageHelper.getTeamPlayers(teamName).iterator();
        renderStatuses(priorityIterator, true);
        renderStatuses(teamIterator, false);
    }

    /* Iterates through UUID iterator and renders HUD element for appropriate UUIDS within */
    private void renderStatuses(final Iterator<UUID> uuidIterator, boolean isPriority) {
        while (uuidIterator.hasNext() && count < 4) {
            UUID playerId = uuidIterator.next();
            if (shouldRenderStatus(playerId, isPriority)) {
                renderStatus(playerId);
                offsetY += 46;
                ++count;
            }
        }
    }

    /* Returns true if HUD element should be rendered for that player */
    private boolean shouldRenderStatus(UUID playerId, boolean isPriority) {
        boolean isDifferentOnlinePlayer = !playerId.equals(mc.player.getUniqueID()) && mc.getConnection().getPlayerInfo(playerId) != null;
        if (isPriority) {
            return isDifferentOnlinePlayer;
        } else { // Make sure they aren't a priority player
            return isDifferentOnlinePlayer && !GuiHandler.priorityPlayers.contains(playerId);
        }
    }

    /* Renders HUD element for player with UUID playerUUID */
    private void renderStatus(final UUID playerUUID) {
        NetworkPlayerInfo info = mc.getConnection().getPlayerInfo(playerUUID);
        String playerName = info.getGameProfile().getName();
        ResourceLocation skinLoc = info.getLocationSkin();
        int health = GuiHandler.healthMap.getOrDefault(playerUUID, 20);
        int hunger = GuiHandler.hungerMap.getOrDefault(playerUUID, 20);
        if (health < 0) {
            return;
        }
        mc.getTextureManager().bindTexture(new ResourceLocation(TeamsMod.MODID, "textures/gui/icon.png"));
        blit((int) Math.round(scaledWidth * 0.002) + 20, (scaledHeight / 4 - 5) + offsetY, 0, 0, 9, 9);
        drawString(mc.fontRenderer, String.valueOf(health), (int) Math.round(scaledWidth * 0.002) + 32, (scaledHeight / 4 - 5) + offsetY, Color.WHITE.getRGB());

        mc.getTextureManager().bindTexture(new ResourceLocation(TeamsMod.MODID, "textures/gui/icon.png"));
        blit((int) Math.round(scaledWidth * 0.002) + 46, (scaledHeight / 4 - 5) + offsetY, 9, 0, 9, 9);
        drawString(mc.fontRenderer, String.valueOf(hunger), (int) Math.round(scaledWidth * 0.002) + 58, (scaledHeight / 4 - 5) + offsetY, Color.WHITE.getRGB());

        mc.getTextureManager().bindTexture(skinLoc);
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        blit((int) Math.round(scaledWidth * 0.002) + 4, (scaledHeight / 2 - 34) + 2 * offsetY, 32, 32, 32, 32);
        GL11.glPopMatrix();
        drawString(mc.fontRenderer, playerName, (int) Math.round(scaledWidth * 0.001) + 20, (scaledHeight / 4 - 20) + offsetY, Color.WHITE.getRGB());
    }
}
