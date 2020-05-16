package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.client.gui.widget.AbstractButton;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;


public class ScreenMain extends AbstractScreenBase {

    public ScreenMain() {
        super(new TextComponentTranslation("teamsmod.main.title"), null);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.goBack.displayString = I18n.format("teamsmod.button.close");
        boolean inTeam = StorageHandler.uuidToTeamMap.containsKey(mc.player.getUniqueID());
        String editorText = inTeam ? I18n.format("teamsmod.main.manage") : I18n.format("teamsmod.main.create");
        this.addButton(new AbstractButton.Basic(GuiHandler.BUTTON_MANAGE, BUTTON_CENTERED_X, guiTop + 25, BUTTON_WIDTH, BUTTON_HEIGHT, editorText, btn -> {
            if (inTeam) {
                mc.displayGuiScreen(new ScreenTeamEditor(this, StorageHandler.uuidToTeamMap.get(mc.player.getUniqueID())));
            } else {
                mc.displayGuiScreen(new ScreenTeamCreator(this));
            }
        }));
        this.addButton(new AbstractButton.Basic(GuiHandler.BUTTON_TEAMLIST, BUTTON_CENTERED_X, guiTop + 50, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.main.list"), btn -> {
            mc.displayGuiScreen(new ScreenTeamList(this));
        }));
        AbstractButton.Basic buttonTransfer = this.addButton(new AbstractButton.Basic(GuiHandler.BUTTON_TRANSFERLIST, BUTTON_CENTERED_X, guiTop + 75, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.main.transfer"), btn -> {
            mc.displayGuiScreen(new ScreenTransferList(this));
        }));
        buttonTransfer.enabled = inTeam && !TeamConfig.serverDisableTransfer;
        AbstractButton.Basic buttonHud = this.addButton(new AbstractButton.Basic(GuiHandler.BUTTON_HUD, BUTTON_CENTERED_X, guiTop + 100, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.main.hud"), btn -> {
            mc.displayGuiScreen(new ScreenHudConfig(this));
        }));
        buttonHud.enabled = inTeam;
    }
}
