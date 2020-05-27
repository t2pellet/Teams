package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.client.gui.widget.AbstractButton;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;

public class ScreenTeamConfig extends AbstractScreenBase {

    private final String teamName;
    private boolean disableAdvancementSync;
    private boolean enableFriendlyFire;

    ScreenTeamConfig(final AbstractScreenBase parent, final String name) {
        super(new TextComponentTranslation("teamsmod.config.title", name), parent);
        teamName = name;
        disableAdvancementSync = StorageHelper.getTeamSetting(teamName, "disableAdvancementSync");
        enableFriendlyFire = StorageHelper.getTeamSetting(teamName, "enableFriendlyFire");
    }

    @Override
    public void initGui() {
        super.initGui();
        this.addButton(new AbstractButton.Basic(GuiHandler.BUTTON_SYNC, BUTTON_CENTERED_X - 5, guiTop + 25, BUTTON_WIDTH + 10, BUTTON_HEIGHT, I18n.format("teamsmod.config.sync") + !disableAdvancementSync, btn -> {
            disableAdvancementSync = !disableAdvancementSync;
            mc.player.sendChatMessage("/teamsmod config disableAdvancementSync " + disableAdvancementSync);
            btn.displayString = ("Advancement Syncing: " + !disableAdvancementSync);
        }));
        this.addButton(new AbstractButton.Basic(GuiHandler.BUTTON_FF, BUTTON_CENTERED_X - 5, guiTop + 50, BUTTON_WIDTH + 10, BUTTON_HEIGHT, I18n.format("teamsmod.config.ff", teamName) + enableFriendlyFire, btn -> {
            enableFriendlyFire = !enableFriendlyFire;
            mc.player.sendChatMessage("/teamsmod config enableFriendlyFire " + String.valueOf(enableFriendlyFire));
            btn.displayString = ("Friendly Fire: " + enableFriendlyFire);
        }));
    }
}
