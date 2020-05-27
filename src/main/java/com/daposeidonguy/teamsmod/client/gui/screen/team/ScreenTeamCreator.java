package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenText;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.Iterator;

public class ScreenTeamCreator extends AbstractScreenText {

    ScreenTeamCreator(final AbstractScreenBase parent) {
        super(new TranslationTextComponent("teamsmod.create.title"), parent);
    }

    @Override
    public void init() {
        super.init();

        Button button = new Button(BUTTON_CENTERED_X, guiTop + 75, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.create.create"), btn -> {
            minecraft.player.sendChatMessage("/teamsmod create " + this.text.getText());
            minecraft.displayGuiScreen(null);
        });
        this.addButton(button);
    }

    @Override
    public void render(int mouseX, int mouseY, final float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        minecraft.fontRenderer.drawString(I18n.format("teamsmod.create.taken"), guiLeft + WIDTH + 40 - minecraft.fontRenderer.getStringWidth(I18n.format("teamsmod.create.taken")) / 2, guiTop + 35, Color.WHITE.getRGB());
        Iterator<String> nameIterator = StorageHelper.getTeamSet().iterator();
        int yoffset = 15;
        while (nameIterator.hasNext()) {
            String name = nameIterator.next();
            minecraft.fontRenderer.drawString(name, guiLeft + WIDTH + 40 - (minecraft.fontRenderer.getStringWidth(name) >> 1), guiTop + yoffset + 35, Color.GRAY.getRGB());
            yoffset += 15;
        }

    }
}
