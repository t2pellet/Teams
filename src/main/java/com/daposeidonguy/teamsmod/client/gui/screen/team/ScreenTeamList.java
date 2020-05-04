package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.ScreenPages;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.Iterator;

public class ScreenTeamList extends ScreenPages {

    protected ScreenTeamList(ITextComponent title) {
        super(title);
    }

    @Override
    public void init() {
        super.init();

        Iterator<String> teamIterator = SaveData.teamsMap.keySet().iterator();
        while (teamIterator.hasNext()) {
            String team = teamIterator.next();
            Button button = new Button(guiLeft + WIDTH / 2 - 60, guiTop + yoffset, 120, 20, team, (pressable) -> {
                minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                minecraft.displayGuiScreen(new ScreenTeamPlayers(new StringTextComponent(team)));
            });
            addButton(button);
            yoffset += 25;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString("Teams List", guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Teams List") / 2, guiTop + 10, Color.BLACK.getRGB());
    }

}
