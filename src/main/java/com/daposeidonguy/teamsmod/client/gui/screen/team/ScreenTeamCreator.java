package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.ScreenText;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.Iterator;

public class ScreenTeamCreator extends ScreenText {

    private Button button;

    protected ScreenTeamCreator(ScreenBase parent) {
        super(new TranslationTextComponent("teamsmod.create.title"), parent);
    }

    @Override
    public void init() {
        super.init();

        this.button = new Button(BUTTON_CENTERED_X, guiTop + 75, BUTTON_WIDTH, BUTTON_HEIGHT, "Create Team", btn -> {
            minecraft.player.sendChatMessage("/teamsmod create " + this.text.getText());
            minecraft.displayGuiScreen(null);
        });
        this.addButton(this.button);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        minecraft.fontRenderer.drawString(I18n.format("teamsmod.create.taken"), guiLeft + WIDTH + 40 - minecraft.fontRenderer.getStringWidth(I18n.format("teamsmod.create.taken")) / 2, guiTop + 35, Color.WHITE.getRGB());
        Iterator<String> nameIterator = StorageHandler.teamToUuidsMap.keySet().iterator();
        int yoffset = 15;
        while (nameIterator.hasNext()) {
            String name = nameIterator.next();
            minecraft.fontRenderer.drawString(name, guiLeft + WIDTH + 40 - minecraft.fontRenderer.getStringWidth(name) / 2, guiTop + yoffset + 35, Color.GRAY.getRGB());
            yoffset += 15;
        }

    }
}
