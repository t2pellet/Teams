package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.ClientHelper;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenPages;
import com.daposeidonguy.teamsmod.client.gui.widget.AbstractButton;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

public class ScreenHudConfig extends AbstractScreenPages {


    ScreenHudConfig(final AbstractScreenBase parent) {
        super(new TextComponentTranslation("teamsmod.hud.title"), parent);
    }

    @Override
    public void initGui() {
        super.initGui();

        String name = StorageHelper.getTeam(mc.player.getUniqueID());
        if (name == null) {
            mc.displayGuiScreen(null);
            mc.player.sendMessage(new TextComponentTranslation("teamsmod.hud.notinteam"));
            return;
        }
        for (UUID uid : StorageHelper.getTeamPlayers(name)) {
            if (!uid.equals(mc.player.getUniqueID()) && ClientHelper.mc.getConnection().getPlayerInfo(uid) != null) {
                String playerName = ClientHelper.getOnlineUsernameFromUUID(uid);
                if (playerName != null && FMLCommonHandler.instance().getSide().isClient()) {
                    AbstractButton.Basic button = new AbstractButton.Basic(GuiHandler.BUTTON_PRIORITY, BUTTON_CENTERED_X - 5, guiTop + yOffset, BUTTON_WIDTH + 10, BUTTON_HEIGHT, playerName + " : " + GuiHandler.priorityPlayers.contains(uid), btn -> {
                        boolean isPriority = GuiHandler.priorityPlayers.contains(uid);
                        if (isPriority) {
                            GuiHandler.priorityPlayers.remove(uid);
                        } else {
                            GuiHandler.priorityPlayers.add(uid);
                        }
                        btn.displayString = playerName + ": " + !isPriority;
                    });
                    this.addButton(button);
                    yOffset += 25;
                }
            }
        }
    }
}
