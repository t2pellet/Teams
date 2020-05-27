package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.ClientHelper;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.UUID;

public class ScreenTeamPlayers extends AbstractScreenBase {
    private final String teamName;

    ScreenTeamPlayers(final AbstractScreenBase parent, final String name) {
        super(new TranslationTextComponent("teamsmod.players.title", name), parent);
        this.teamName = name;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void render(int mouseX, int mouseY, final float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        int yoffset = 30;
        for (UUID uid : StorageHelper.getTeamPlayers(teamName)) {
            String playerName = ClientHelper.getNameFromId(uid);
            ClientHelper.mc.fontRenderer.drawString(playerName, CENTERED_X - (font.getStringWidth(playerName) >> 1), guiTop + yoffset, Color.GRAY.getRGB());
            yoffset += 15;
        }
    }
}
