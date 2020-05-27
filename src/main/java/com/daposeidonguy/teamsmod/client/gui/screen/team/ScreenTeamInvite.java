package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.ClientHelper;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenText;
import com.daposeidonguy.teamsmod.client.gui.widget.AbstractButton;
import com.daposeidonguy.teamsmod.client.gui.widget.ClearButton;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class ScreenTeamInvite extends AbstractScreenText {

    ScreenTeamInvite(final AbstractScreenBase parent, final String teamName) {
        super(new TextComponentTranslation("teamsmod.invite.title", teamName), parent);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.addButton(new AbstractButton.Basic(GuiHandler.BUTTON_INVITE, BUTTON_CENTERED_X, guiTop + 70, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.invite.invite"), (pressable) -> {
            mc.player.sendChatMessage("/teamsmod invite " + this.text.getText());
            mc.displayGuiScreen(null);
        }));
        Iterator<NetworkPlayerInfo> infoIterator = mc.getConnection().getPlayerInfoMap().iterator();
        String clientName = mc.player.getGameProfile().getName();
        int yoffset = 15;
        while (infoIterator.hasNext()) {
            UUID uid = infoIterator.next().getGameProfile().getId();
            String playerName = ClientHelper.getOnlineUsernameFromUUID(uid);
            if (!playerName.equals(clientName) && StorageHelper.isPlayerInTeam(uid)) {
                int width = mc.fontRenderer.getStringWidth(playerName);
                this.addButton(new ClearButton(GuiHandler.BUTTON_PLAYERNAME, guiLeft + WIDTH + 42 - width / 2, guiTop + yoffset + 35, width, 10, playerName, btn -> this.text.setText(btn.displayString)));
                yoffset += 15;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        mc.fontRenderer.drawString(I18n.format("teamsmod.players.suggestions"), guiLeft + WIDTH + 42 - mc.fontRenderer.getStringWidth(I18n.format("teamsmod.players.suggestions")) / 2, guiTop + 35, Color.WHITE.getRGB());
    }
}
