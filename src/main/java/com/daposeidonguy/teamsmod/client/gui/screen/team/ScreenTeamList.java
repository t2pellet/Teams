package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.ScreenPages;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.Iterator;

public class ScreenTeamList extends ScreenPages {

    protected ScreenTeamList(ScreenBase parent) {
        super(new StringTextComponent("teamlist"), parent);
    }

    @Override
    public void init() {
        super.init();
        Iterator<String> teamIterator = SaveData.teamsMap.keySet().iterator();
        while (teamIterator.hasNext()) {
            String team = teamIterator.next();
            addButton(new Button(guiLeft + WIDTH / 2 - 60, guiTop + yOffset, 120, 20, team, (pressable) -> {
                minecraft.displayGuiScreen(new ScreenTeamPlayers(this, team));
            }));
            yOffset += 25;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString("Teams List", guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Teams List") / 2, guiTop + 10, Color.BLACK.getRGB());
    }

}
