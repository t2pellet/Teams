package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenPages;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenTeamList extends AbstractScreenPages {

    ScreenTeamList(final AbstractScreenBase parent) {
        super(new TranslationTextComponent("teamsmod.list.title"), parent);
    }

    @Override
    public void init() {
        super.init();
        for (String team : StorageHelper.getTeamSet()) {
            addButton(new Button(BUTTON_CENTERED_X, guiTop + yOffset, BUTTON_WIDTH, BUTTON_HEIGHT, team, (pressable) -> {
                minecraft.displayGuiScreen(new ScreenTeamPlayers(this, team));
            }));
            yOffset += 25;
        }
    }

}
