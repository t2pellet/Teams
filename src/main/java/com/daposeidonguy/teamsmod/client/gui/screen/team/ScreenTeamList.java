package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenPages;
import com.daposeidonguy.teamsmod.client.gui.widget.AbstractButton;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.util.text.TextComponentTranslation;

public class ScreenTeamList extends AbstractScreenPages {

    ScreenTeamList(final AbstractScreenBase parent) {
        super(new TextComponentTranslation("teamsmod.list.title"), parent);
    }

    @Override
    public void initGui() {
        super.initGui();
        for (String team : StorageHelper.getTeamSet()) {
            addButton(new AbstractButton.Basic(GuiHandler.BUTTON_TEAMPLAYERS, BUTTON_CENTERED_X, guiTop + yOffset, BUTTON_WIDTH, BUTTON_HEIGHT, team, (pressable) -> {
                mc.displayGuiScreen(new ScreenTeamPlayers(this, team));
            }));
            yOffset += 25;
        }
    }

}
