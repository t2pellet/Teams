package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.ClientHandler;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenText;
import com.daposeidonguy.teamsmod.client.gui.widget.AbstractButton;
import com.daposeidonguy.teamsmod.client.gui.widget.ClearButton;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class ScreenTeamKick extends AbstractScreenText {

    ScreenTeamKick(final AbstractScreenBase parent, final String teamName) {
        super(new TextComponentTranslation("teamsmod.kick.title", teamName), parent);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.addButton(new AbstractButton.Basic(GuiHandler.BUTTON_KICK, BUTTON_CENTERED_X, guiTop + 70, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.kick.kick"), (pressable) -> {
            mc.player.sendChatMessage("/teamsmod kick " + this.text.getText());
            mc.displayGuiScreen(null);
        }));

        Iterator<UUID> uuidIterator = StorageHandler.teamToUuidsMap.get(StorageHandler.uuidToTeamMap.get(mc.player.getUniqueID())).iterator();
        int yoffset = 15;
        while (uuidIterator.hasNext()) {
            UUID uid = uuidIterator.next();
            String playerName = ClientHandler.getOnlineUsernameFromUUID(uid);
            if (!uid.equals(mc.player.getUniqueID()) && playerName != null) {
                int width = mc.fontRenderer.getStringWidth(playerName);
                addButton(new ClearButton(GuiHandler.BUTTON_PLAYERNAME, guiLeft + WIDTH + 42 - width / 2, guiTop + yoffset + 35, width, 10, playerName, btn -> this.text.setText(btn.displayString)));
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
