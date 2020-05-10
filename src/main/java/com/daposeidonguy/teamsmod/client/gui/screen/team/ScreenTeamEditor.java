package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenTeamEditor extends ScreenBase {

    private String teamName;


    public ScreenTeamEditor(ScreenBase parent, String teamName) {
        super(new TranslationTextComponent("teamsmod.edit.title", teamName), parent);
        this.teamName = teamName;
    }

    @Override
    public void init() {
        super.init();

        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 25, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.edit.invite"), btn -> {
            minecraft.displayGuiScreen(new ScreenTeamInvite(this, teamName));
        }));
        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 50, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.edit.kick"), btn -> {
            minecraft.displayGuiScreen(new ScreenTeamKick(this, teamName));
        }));
        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 75, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.edit.config"), btn -> {
            minecraft.displayGuiScreen(new ScreenTeamConfig(this, teamName));
        }));
        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 100, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.edit.leave"), btn -> {
            minecraft.player.sendChatMessage("/teamsmod leave");
            minecraft.displayGuiScreen(null);
        }));
    }

}
