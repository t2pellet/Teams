package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.ClientHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.util.text.TextComponentTranslation;

import java.awt.*;
import java.util.UUID;

public class ScreenTeamPlayers extends AbstractScreenBase {
    private final String teamName;

    ScreenTeamPlayers(final AbstractScreenBase parent, final String name) {
        super(new TextComponentTranslation("teamsmod.players.title", name), parent);
        this.teamName = name;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        int yoffset = 30;
        for (UUID uid : StorageHandler.teamToUuidsMap.get(teamName)) {
            String playerName = ClientHandler.getUsernameFromUUID(uid);
            ClientHandler.mc.fontRenderer.drawString(playerName, CENTERED_X - mc.fontRenderer.getStringWidth(playerName) / 2, guiTop + yoffset, Color.GRAY.getRGB());
            yoffset += 15;
        }
    }
}
