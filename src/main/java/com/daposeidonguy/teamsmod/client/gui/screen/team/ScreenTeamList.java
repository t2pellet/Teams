package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenPages;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenTeamList extends AbstractScreenPages {

    ScreenTeamList(final AbstractScreenBase parent) {
        super(new TranslationTextComponent("teamsmod.list.title"), parent);
    }

    @Override
    public void init() {
        super.init();
        for (String team : StorageHandler.teamToUuidsMap.keySet()) {
            addButton(new Button(BUTTON_CENTERED_X, guiTop + yOffset, BUTTON_WIDTH, BUTTON_HEIGHT, team, (pressable) -> {
                minecraft.displayGuiScreen(new ScreenTeamPlayers(this, team));
            }));
            yOffset += 25;
        }
    }

}
