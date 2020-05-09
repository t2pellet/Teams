package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;

public class ScreenTeamConfig extends ScreenBase {

    private String teamName;
    private boolean disableAdvancementSync;
    private boolean enableFriendlyFire;

    protected ScreenTeamConfig(ScreenBase parent, String name) {
        super(new StringTextComponent("teamconfig"), parent);
        teamName = name;
        disableAdvancementSync = StorageHandler.teamSettingsMap.get(teamName).get("disableAdvancementSync");
        enableFriendlyFire = StorageHandler.teamSettingsMap.get(teamName).get("enableFriendlyFire");
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new Button(BUTTON_CENTERED_X - 5, guiTop + 25, BUTTON_WIDTH + 10, BUTTON_HEIGHT, "Advancement Sync: " + !disableAdvancementSync, btn -> {
            disableAdvancementSync = !disableAdvancementSync;
            minecraft.player.sendChatMessage("/teamsmod config disableAdvancementSync " + disableAdvancementSync);
            btn.setMessage("Advancement Syncing: " + !disableAdvancementSync);
        }));
        this.addButton(new Button(BUTTON_CENTERED_X - 5, guiTop + 50, BUTTON_WIDTH + 10, BUTTON_HEIGHT, "Friendly Fire: " + enableFriendlyFire, btn -> {
            enableFriendlyFire = !enableFriendlyFire;
            minecraft.player.sendChatMessage("/teamsmod config enableFriendlyFire " + String.valueOf(enableFriendlyFire));
            btn.setMessage("Friendly Fire: " + enableFriendlyFire);
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString("Team Config: " + teamName, guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Team Config: " + teamName) / 2, guiTop + 10, Color.BLACK.getRGB());
    }
}
