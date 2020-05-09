package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.ScreenText;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.Iterator;

public class ScreenTeamCreator extends ScreenText {

    private Button button;

    protected ScreenTeamCreator(ScreenBase parent) {
        super(new StringTextComponent("teamcreator"), parent);
    }

    @Override
    public void init() {
        super.init();

        this.button = new Button(BUTTON_CENTERED_X, guiTop + 75, BUTTON_WIDTH, BUTTON_HEIGHT, "Create Team", btn -> {
            minecraft.player.sendChatMessage("/teamsmod create " + this.text.getText());
            minecraft.displayGuiScreen(null);
        });
        this.addButton(this.button);
        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 130, BUTTON_WIDTH, BUTTON_HEIGHT, "Go back", btn -> {
            minecraft.displayGuiScreen(new ScreenMain());
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString("Team Manager", guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Team Manager") / 2, guiTop + 10, Color.BLACK.getRGB());

        minecraft.fontRenderer.drawString("Taken Names:", guiLeft + WIDTH + 40 - minecraft.fontRenderer.getStringWidth("Taken Names:") / 2, guiTop + 35, Color.WHITE.getRGB());
        Iterator<String> nameIterator = StorageHandler.teamToUuidsMap.keySet().iterator();
        int yoffset = 15;
        while (nameIterator.hasNext()) {
            String name = nameIterator.next();
            minecraft.fontRenderer.drawString(name, guiLeft + WIDTH + 40 - minecraft.fontRenderer.getStringWidth(name) / 2, guiTop + yoffset + 35, Color.GRAY.getRGB());
            yoffset += 15;
        }

    }
}
