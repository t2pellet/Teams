package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.ClientHelper;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenText;
import com.daposeidonguy.teamsmod.client.gui.widget.ClearButton;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class ScreenTeamKick extends AbstractScreenText {

    ScreenTeamKick(final AbstractScreenBase parent, final String teamName) {
        super(new TranslationTextComponent("teamsmod.kick.title", teamName), parent);
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 70, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.kick.kick"), (pressable) -> {
            minecraft.player.sendChatMessage("/teamsmod kick " + this.text.getText());
            minecraft.displayGuiScreen(null);
        }));

        Iterator<UUID> uuidIterator = StorageHelper.getTeamPlayers(StorageHelper.getTeam(minecraft.player.getUniqueID())).iterator();
        int yoffset = 15;
        while (uuidIterator.hasNext()) {
            UUID uid = uuidIterator.next();
            String playerName = ClientHelper.getOnlineNameFromId(uid);
            if (!uid.equals(minecraft.player.getUniqueID()) && playerName != null) {
                int width = minecraft.fontRenderer.getStringWidth(playerName);
                addButton(new ClearButton(guiLeft + WIDTH + 42 - width / 2, guiTop + yoffset + 35, width, 10, playerName, btn -> this.text.setText(btn.getMessage())));
                yoffset += 15;
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, final float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString(I18n.format("teamsmod.players.suggestions"), guiLeft + WIDTH + 42 - minecraft.fontRenderer.getStringWidth(I18n.format("teamsmod.players.suggestions")) / 2, guiTop + 35, Color.WHITE.getRGB());
    }
}
