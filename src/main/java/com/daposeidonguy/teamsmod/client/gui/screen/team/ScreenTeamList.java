package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.ScreenPages;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Iterator;

public class ScreenTeamList extends ScreenPages {

    protected ScreenTeamList(ScreenBase parent) {
        super(new TranslationTextComponent("teamsmod.list.title"), parent);
    }

    @Override
    public void init() {
        super.init();
        Iterator<String> teamIterator = StorageHandler.teamToUuidsMap.keySet().iterator();
        while (teamIterator.hasNext()) {
            String team = teamIterator.next();
            addButton(new Button(BUTTON_CENTERED_X, guiTop + yOffset, BUTTON_WIDTH, BUTTON_HEIGHT, team, (pressable) -> {
                minecraft.displayGuiScreen(new ScreenTeamPlayers(this, team));
            }));
            yOffset += 25;
        }
    }

}
