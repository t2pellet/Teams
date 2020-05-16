package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.client.gui.widget.AbstractButton;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.UUID;

public class ScreenTeamEditor extends AbstractScreenBase {

    private final String teamName;


    ScreenTeamEditor(final AbstractScreenBase parent, final String teamName) {
        super(new TextComponentTranslation("teamsmod.edit.title", teamName), parent);
        this.teamName = teamName;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.addButton(new AbstractButton.Basic(GuiHandler.BUTTON_INVITEPLAYERS, BUTTON_CENTERED_X, guiTop + 25, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.edit.invite"), btn -> {
            mc.displayGuiScreen(new ScreenTeamInvite(this, teamName));
        }));
        boolean isTeamOwner = isTeamOwner();
        GuiButton kickButton = this.addButton(new AbstractButton.Basic(GuiHandler.BUTTON_KICKPLAYERS, BUTTON_CENTERED_X, guiTop + 50, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.edit.kick"), btn -> {
            if (mc.player.getUniqueID().equals(StorageHandler.teamToOwnerMap.get(teamName))) {
                mc.displayGuiScreen(new ScreenTeamKick(this, teamName));
            }
        }));
        kickButton.enabled = isTeamOwner;
        GuiButton configButton = this.addButton(new AbstractButton.Basic(GuiHandler.BUTTON_CONFIG, BUTTON_CENTERED_X, guiTop + 75, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.edit.config"), btn -> {
            if (mc.player.getUniqueID().equals(StorageHandler.teamToOwnerMap.get(teamName))) {
                mc.displayGuiScreen(new ScreenTeamConfig(this, teamName));
            }
        }));
        configButton.enabled = isTeamOwner;
        GuiButton leaveButton = this.addButton(new AbstractButton.Basic(GuiHandler.BUTTON_LEAVE, BUTTON_CENTERED_X, guiTop + 100, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.edit.leave"), btn -> {
            mc.player.sendChatMessage("/teamsmod leave");
            mc.displayGuiScreen(null);
        }));
        leaveButton.enabled = !isTeamOwner || StorageHandler.teamToUuidsMap.get(StorageHandler.uuidToTeamMap.get(mc.player.getUniqueID())).size() == 1;
    }

    private boolean isTeamOwner() {
        UUID clientId = mc.player.getUniqueID();
        String team = StorageHandler.uuidToTeamMap.get(clientId);
        return StorageHandler.teamToOwnerMap.get(team).equals(clientId);
    }

}
