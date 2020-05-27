package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenText;
import com.daposeidonguy.teamsmod.client.gui.widget.AbstractButton;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;

import java.awt.*;
import java.util.Iterator;

public class ScreenTeamCreator extends AbstractScreenText {

    ScreenTeamCreator(final AbstractScreenBase parent) {
        super(new TextComponentTranslation("teamsmod.create.title"), parent);
    }

    @Override
    public void initGui() {
        super.initGui();

        GuiButton button = new AbstractButton.Basic(GuiHandler.BUTTON_CREATE, BUTTON_CENTERED_X, guiTop + 75, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.create.create"), btn -> {
            mc.player.sendChatMessage("/teamsmod create " + this.text.getText());
            mc.displayGuiScreen(null);
        });
        this.addButton(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        mc.fontRenderer.drawString(I18n.format("teamsmod.create.taken"), guiLeft + WIDTH + 40 - mc.fontRenderer.getStringWidth(I18n.format("teamsmod.create.taken")) / 2, guiTop + 35, Color.WHITE.getRGB());
        Iterator<String> nameIterator = StorageHelper.getTeamSet().iterator();
        int yoffset = 15;
        while (nameIterator.hasNext()) {
            String name = nameIterator.next();
            mc.fontRenderer.drawString(name, guiLeft + WIDTH + 40 - (mc.fontRenderer.getStringWidth(name) >> 1), guiTop + yoffset + 35, Color.GRAY.getRGB());
            yoffset += 15;
        }

    }
}
