package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

public class ScreenTeamEditor extends AbstractScreenBase {

    private final String teamName;


    ScreenTeamEditor(final AbstractScreenBase parent, final String teamName) {
        super(new TranslationTextComponent("teamsmod.edit.title", teamName), parent);
        this.teamName = teamName;
    }

    @Override
    public void init() {
        super.init();

        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 25, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.edit.invite"), btn -> {
            minecraft.displayGuiScreen(new ScreenTeamInvite(this, teamName));
        }));
        boolean isTeamOwner = isTeamOwner();
        Button kickButton = this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 50, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.edit.kick"), btn -> {
            if (minecraft.player.getUniqueID().equals(StorageHandler.teamToOwnerMap.get(teamName))) {
                minecraft.displayGuiScreen(new ScreenTeamKick(this, teamName));
            }
        }));
        kickButton.active = isTeamOwner;
        Button configButton = this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 75, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.edit.config"), btn -> {
            if (minecraft.player.getUniqueID().equals(StorageHandler.teamToOwnerMap.get(teamName))) {
                minecraft.displayGuiScreen(new ScreenTeamConfig(this, teamName));
            }
        }));
        configButton.active = isTeamOwner;
        this.addButton(new Button(BUTTON_CENTERED_X, guiTop + 100, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.edit.leave"), btn -> {
            minecraft.player.sendChatMessage("/teamsmod leave");
            minecraft.displayGuiScreen(null);
        }));
    }

    private boolean isTeamOwner() {
        UUID clientId = minecraft.player.getUniqueID();
        String team = StorageHandler.uuidToTeamMap.get(clientId);
        return StorageHandler.teamToOwnerMap.get(team).equals(clientId);
    }

}
