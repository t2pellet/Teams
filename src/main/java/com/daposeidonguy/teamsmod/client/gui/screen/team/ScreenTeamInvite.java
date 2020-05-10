package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.ClientHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.ScreenText;
import com.daposeidonguy.teamsmod.client.gui.widget.ClearButton;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class ScreenTeamInvite extends ScreenText {

    private String teamName;

    protected ScreenTeamInvite(ScreenBase parent, String teamName) {
        super(new TranslationTextComponent("teamsmod.invite.title", teamName), parent);
        this.teamName = teamName;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 70, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.invite.invite"), (pressable) -> {
            minecraft.player.sendChatMessage("/teamsmod invite " + this.text.getText());
            minecraft.displayGuiScreen(null);
        }));
        Iterator<NetworkPlayerInfo> infoIterator = minecraft.getConnection().getPlayerInfoMap().iterator();
        String clientName = minecraft.player.getGameProfile().getName();
        int yoffset = 15;
        while (infoIterator.hasNext()) {
            UUID uid = infoIterator.next().getGameProfile().getId();
            String playerName = ClientHandler.getOnlineUsernameFromUUID(uid);
            if (playerName != clientName && !StorageHandler.uuidToTeamMap.containsKey(uid)) {
                int width = minecraft.fontRenderer.getStringWidth(playerName);
                this.addButton(new ClearButton(guiLeft + WIDTH + 42 - width / 2, guiTop + yoffset + 35, width, 10, playerName, btn -> this.text.setText(btn.getMessage())));
                yoffset += 15;
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString(I18n.format("teamsmod.players.suggestions"), guiLeft + WIDTH + 42 - minecraft.fontRenderer.getStringWidth(I18n.format("teamsmod.players.suggestions")) / 2, guiTop + 35, Color.WHITE.getRGB());
    }
}
