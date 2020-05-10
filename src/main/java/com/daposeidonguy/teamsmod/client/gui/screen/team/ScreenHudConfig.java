package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.ClientHandler;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.ScreenPages;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Iterator;
import java.util.UUID;

public class ScreenHudConfig extends ScreenPages {


    protected ScreenHudConfig(ScreenBase parent) {
        super(new TranslationTextComponent("teamsmod.hud.title"), parent);
    }

    @Override
    public void init() {
        super.init();

        String name = StorageHandler.uuidToTeamMap.get(minecraft.player.getUniqueID());
        if (name == null) {
            minecraft.displayGuiScreen(null);
            minecraft.player.sendMessage(new TranslationTextComponent("teamsmod.hud.notinteam"));
            return;
        }
        Iterator<UUID> teamIterator = StorageHandler.teamToUuidsMap.get(name).iterator();
        while (teamIterator.hasNext()) {
            UUID uid = teamIterator.next();
            if (!uid.equals(minecraft.player.getUniqueID())) {
                String playerName = ClientHandler.getOnlineUsernameFromUUID(uid);
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
