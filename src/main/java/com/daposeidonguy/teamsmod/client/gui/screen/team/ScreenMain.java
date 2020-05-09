package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;


public class ScreenMain extends ScreenBase {

    public ScreenMain() {
        super(new StringTextComponent("main"), null);
    }

    @Override
    protected void init() {
        super.init();
        this.goBack.setMessage("Close menu");
        boolean inTeam = StorageHandler.uuidToTeamMap.containsKey(minecraft.player.getUniqueID());
        String editorText = inTeam ? "Manage Team" : "Create Team";
        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 25, BUTTON_WIDTH, BUTTON_HEIGHT, editorText, btn -> {
            if (inTeam) {
                minecraft.displayGuiScreen(new ScreenTeamEditor(this, StorageHandler.uuidToTeamMap.get(minecraft.player.getUniqueID())));
            } else {
                minecraft.displayGuiScreen(new ScreenTeamCreator(this));
            }
        }));
        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 50, BUTTON_WIDTH, BUTTON_HEIGHT, "List Teams", btn -> {
            minecraft.displayGuiScreen(new ScreenTeamList(this));
        }));
        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 75, BUTTON_WIDTH, BUTTON_HEIGHT, "Transfer Items", btn -> {
            minecraft.displayGuiScreen(new ScreenTransferList(this));
        }));
        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 100, BUTTON_WIDTH, BUTTON_HEIGHT, "Configure HUD", btn -> {
            minecraft.displayGuiScreen(new ScreenHudConfig(this));
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString("Teams GUI", guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Teams GUI") / 2, guiTop + 10, Color.BLACK.getRGB());
    }
}
