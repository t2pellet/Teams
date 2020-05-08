package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;


public class ScreenTeam extends ScreenBase {

    public ScreenTeam() {
        super(new StringTextComponent("command"), null);
    }

    @Override
    protected void init() {
        super.init();
        this.goBack.setMessage("Close menu");
        boolean inTeam = SaveData.teamMap.containsKey(minecraft.player.getUniqueID());
        String editorText = inTeam ? "Manage Team" : "Create Team";
        this.addButton(new Button(guiLeft + WIDTH / 2 - 60, guiTop + 25, 120, 20, editorText, btn -> {
            if (inTeam) {
                minecraft.displayGuiScreen(new ScreenTeamManager.GuiTeamEditor(this, SaveData.teamMap.get(minecraft.player.getUniqueID())));
            } else {
                minecraft.displayGuiScreen(new ScreenTeamManager.GuiTeamCreator(this));
            }
        }));
        this.addButton(new Button(guiLeft + WIDTH / 2 - 60, guiTop + 50, 120, 20, "List Teams", btn -> {
            minecraft.displayGuiScreen(new ScreenTeamList(this));
        }));
        this.addButton(new Button(guiLeft + WIDTH / 2 - 60, guiTop + 75, 120, 20, "Transfer Items", btn -> {
            minecraft.displayGuiScreen(new ScreenTransferPlayers(this));
        }));
        this.addButton(new Button(guiLeft + WIDTH / 2 - 60, guiTop + 100, 120, 20, "Configure HUD", btn -> {
            minecraft.displayGuiScreen(new ScreenHudConfig(this));
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString("Teams GUI", guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Teams GUI") / 2, guiTop + 10, Color.BLACK.getRGB());
    }
}
