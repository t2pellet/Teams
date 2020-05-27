package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.ClientHelper;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenPages;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

public class ScreenHudConfig extends AbstractScreenPages {


    ScreenHudConfig(final AbstractScreenBase parent) {
        super(new TranslationTextComponent("teamsmod.hud.title"), parent);
    }

    @Override
    public void init() {
        super.init();

        String name = StorageHelper.getTeam(minecraft.player.getUniqueID());
        if (name == null) {
            minecraft.displayGuiScreen(null);
            minecraft.player.sendMessage(new TranslationTextComponent("teamsmod.hud.notinteam"));
            return;
        }
        for (UUID uid : StorageHelper.getTeamPlayers(name)) {
            if (!uid.equals(minecraft.player.getUniqueID()) && ClientHelper.mc.getConnection().getPlayerInfo(uid) != null) {
                String playerName = ClientHelper.getOnlineNameFromId(uid);
                if (playerName != null) {
                    Button button = new Button(BUTTON_CENTERED_X - 5, guiTop + yOffset, BUTTON_WIDTH + 10, BUTTON_HEIGHT, playerName + ": " + GuiHandler.priorityPlayers.contains(uid), btn -> {
                        boolean isPriority = GuiHandler.priorityPlayers.contains(uid);
                        if (isPriority) {
                            GuiHandler.priorityPlayers.remove(uid);
                        } else {
                            GuiHandler.priorityPlayers.add(uid);
                        }
                        btn.setMessage(playerName + ": " + !isPriority);
                    });
                    this.addButton(button);
                    yOffset += 25;
                }
            }
        }
    }
}
