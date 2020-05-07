package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.ScreenPages;
import com.daposeidonguy.teamsmod.client.gui.screen.inventory.ScreenTransfer;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.inventory.ContainerTransfer;
import com.daposeidonguy.teamsmod.common.network.MessageGui;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class ScreenTransferPlayers extends ScreenPages {

    protected ScreenTransferPlayers(ScreenBase parent) {
        super(new StringTextComponent("transferplayers"), parent);
    }

    @Override
    public void init() {
        super.init();

        String name = SaveData.teamMap.get(minecraft.player.getUniqueID());
        if (name == null) {
            minecraft.displayGuiScreen(null);
            minecraft.player.sendMessage(new StringTextComponent("You are not in a team!"));
            return;
        }
        Iterator<UUID> teamIterator = SaveData.teamsMap.get(name).iterator();
        while (teamIterator.hasNext()) {
            UUID uid = teamIterator.next();
            if (!uid.equals(minecraft.player.getUniqueID())) {
                if (minecraft.getConnection().getPlayerInfo(uid) != null) {
                    String otherP = minecraft.getConnection().getPlayerInfo(uid).getGameProfile().getName();
                    addButton(new Button(guiLeft + WIDTH / 2 - 60, guiTop + yOffset, 120, 20, otherP, (pressable) -> {
                        if (!TeamConfig.disableInventoryTransfer) {
                            minecraft.displayGuiScreen(new ScreenTransfer(new ContainerTransfer(0, minecraft.player.inventory, otherP), minecraft.player.inventory, new StringTextComponent("transfer")));
                            if (EffectiveSide.get().isClient()) {
                                PacketHandler.INSTANCE.sendToServer(new MessageGui(minecraft.player.getUniqueID(), otherP));
                            }
                        } else {
                            minecraft.player.sendMessage(new StringTextComponent("That feature is disabled"));
                            minecraft.displayGuiScreen(null);
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
