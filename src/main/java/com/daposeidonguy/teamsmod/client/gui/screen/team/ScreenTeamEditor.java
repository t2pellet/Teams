package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;

public class ScreenTeamEditor extends ScreenBase {

    private String teamName;


    public ScreenTeamEditor(ScreenBase parent, String name) {
        super(new StringTextComponent("teameditor"), parent);
        this.teamName = name;
    }

    @Override
    public void init() {
        super.init();

        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 25, BUTTON_WIDTH, BUTTON_HEIGHT, "Invite Players", btn -> {
            minecraft.displayGuiScreen(new ScreenTeamInvite(this));
        }));
        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 50, BUTTON_WIDTH, BUTTON_HEIGHT, "Kick Players", btn -> {
            minecraft.displayGuiScreen(new ScreenTeamKick(this));
        }));
        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 75, BUTTON_WIDTH, BUTTON_HEIGHT, "Team Options", btn -> {
            minecraft.displayGuiScreen(new ScreenTeamConfig(this, teamName));
        }));
        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 100, BUTTON_WIDTH, BUTTON_HEIGHT, "Leave Team", btn -> {
            minecraft.player.sendChatMessage("/teamsmod leave");
            minecraft.displayGuiScreen(null);
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString("Team Manager: " + teamName, guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Team Manager: " + teamName) / 2, guiTop + 10, Color.BLACK.getRGB());
    }

}
