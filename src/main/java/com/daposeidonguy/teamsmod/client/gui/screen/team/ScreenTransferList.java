package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.ScreenPages;
import com.daposeidonguy.teamsmod.client.gui.screen.inventory.ScreenTransfer;
import com.daposeidonguy.teamsmod.common.config.ConfigHandler;
import com.daposeidonguy.teamsmod.common.inventory.ContainerTransfer;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.network.messages.MessageGuiTransfer;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class ScreenTransferList extends ScreenPages {

    protected ScreenTransferList(ScreenBase parent) {
        super(new StringTextComponent("transferlist"), parent);
    }

    @Override
    public void init() {
        super.init();
        TeamsMod.logger.info("Server Transfer Disabled: " + ConfigHandler.serverDisableTransfer);
        String name = StorageHandler.uuidToTeamMap.get(minecraft.player.getUniqueID());
        if (name == null) {
            minecraft.displayGuiScreen(null);
            minecraft.player.sendMessage(new StringTextComponent("You are not in a team!"));
            return;
        } else if (ConfigHandler.serverDisableTransfer) {
            minecraft.displayGuiScreen(null);
            minecraft.player.sendMessage(new StringTextComponent("That feature is disabled"));
        }
        Iterator<UUID> teamIterator = StorageHandler.teamToUuidsMap.get(name).iterator();
        while (teamIterator.hasNext()) {
            UUID uid = teamIterator.next();
            if (!uid.equals(minecraft.player.getUniqueID())) {
                if (minecraft.getConnection().getPlayerInfo(uid) != null) {
                    String otherP = minecraft.getConnection().getPlayerInfo(uid).getGameProfile().getName();
                    addButton(new Button(BUTTON_CENTERED_X, guiTop + yOffset, BUTTON_WIDTH, BUTTON_HEIGHT, otherP, btn -> {
                        minecraft.displayGuiScreen(new ScreenTransfer(new ContainerTransfer(0, minecraft.player.inventory, otherP), minecraft.player.inventory, new StringTextComponent("transfer")));
                        if (EffectiveSide.get().isClient()) {
                            PacketHandler.INSTANCE.sendToServer(new MessageGuiTransfer(minecraft.player.getUniqueID(), otherP));
                        }
                    }));
                    yOffset += 25;
                }
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString("Player List", guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Player List") / 2, guiTop + 10, Color.BLACK.getRGB());
    }

}
