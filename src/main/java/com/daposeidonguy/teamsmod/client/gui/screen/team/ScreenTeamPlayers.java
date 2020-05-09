package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.ClientHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class ScreenTeamPlayers extends ScreenBase {
    private String name;

    public ScreenTeamPlayers(ScreenBase parent, String name) {
        super(new StringTextComponent("teamplayers"), parent);
        this.name = name;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString("Team Players: " + name, guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Team Players: " + name) / 2, guiTop + 10, Color.BLACK.getRGB());
        int yoffset = 30;
        Iterator<UUID> teamIterator = StorageHandler.teamToUuidsMap.get(name).iterator();
        while (teamIterator.hasNext()) {
            UUID uid = teamIterator.next();
            String playerName = ClientHandler.getUsernameFromUUID(uid);
            minecraft.fontRenderer.drawString(playerName, guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth(playerName) / 2, guiTop + yoffset, Color.GRAY.getRGB());
            yoffset += 15;
        }
    }
}
