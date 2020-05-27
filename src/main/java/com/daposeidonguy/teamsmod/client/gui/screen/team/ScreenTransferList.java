package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.AbstractScreenPages;
import com.daposeidonguy.teamsmod.client.gui.screen.inventory.ScreenTransfer;
import com.daposeidonguy.teamsmod.client.gui.widget.AbstractButton;
import com.daposeidonguy.teamsmod.common.inventory.ContainerTransfer;
import com.daposeidonguy.teamsmod.common.network.NetworkHelper;
import com.daposeidonguy.teamsmod.common.network.messages.MessageGuiTransfer;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.UUID;

public class ScreenTransferList extends AbstractScreenPages {

    ScreenTransferList(final AbstractScreenBase parent) {
        super(new TextComponentTranslation("teamsmod.transferlist.title"), parent);
    }

    @Override
    public void initGui() {
        super.initGui();
        String name = StorageHelper.getTeam(mc.player.getUniqueID());
        for (UUID uid : StorageHelper.getTeamPlayers(name)) {
            if (!uid.equals(mc.player.getUniqueID())) {
                if (mc.getConnection().getPlayerInfo(uid) != null) {
                    String otherP = mc.getConnection().getPlayerInfo(uid).getGameProfile().getName();
                    addButton(new AbstractButton.Basic(GuiHandler.BUTTON_TRANSFER, BUTTON_CENTERED_X, guiTop + yOffset, BUTTON_WIDTH, BUTTON_HEIGHT, otherP, btn -> {
                        mc.displayGuiScreen(new ScreenTransfer(new ContainerTransfer(mc.player.inventory, otherP), mc.player.inventory, new TextComponentTranslation("teamsmod.transfer.title", otherP)));
                        NetworkHelper.sendToServer(new MessageGuiTransfer(mc.player.getUniqueID(), otherP));
                    }));
                    yOffset += 25;
                }
            }
        }
    }

}
