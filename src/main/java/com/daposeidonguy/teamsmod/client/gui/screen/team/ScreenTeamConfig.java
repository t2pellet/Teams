package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenTeamConfig extends AbstractScreenBase {

    private final String teamName;
    private boolean disableAdvancementSync;
    private boolean enableFriendlyFire;

    ScreenTeamConfig(AbstractScreenBase parent, String name) {
        super(new TranslationTextComponent("teamsmod.config.title", name), parent);
        teamName = name;
        disableAdvancementSync = StorageHandler.teamSettingsMap.get(teamName).get("disableAdvancementSync");
        enableFriendlyFire = StorageHandler.teamSettingsMap.get(teamName).get("enableFriendlyFire");
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new Button(BUTTON_CENTERED_X - 5, guiTop + 25, BUTTON_WIDTH + 10, BUTTON_HEIGHT, I18n.format("teamsmod.config.sync") + !disableAdvancementSync, btn -> {
            disableAdvancementSync = !disableAdvancementSync;
            minecraft.player.sendChatMessage("/teamsmod config disableAdvancementSync " + disableAdvancementSync);
            btn.setMessage("Advancement Syncing: " + !disableAdvancementSync);
        }));
        this.addButton(new Button(BUTTON_CENTERED_X - 5, guiTop + 50, BUTTON_WIDTH + 10, BUTTON_HEIGHT, I18n.format("teamsmod.config.ff", teamName) + enableFriendlyFire, btn -> {
            enableFriendlyFire = !enableFriendlyFire;
            minecraft.player.sendChatMessage("/teamsmod config enableFriendlyFire " + String.valueOf(enableFriendlyFire));
            btn.setMessage("Friendly Fire: " + enableFriendlyFire);
        }));
    }
}
