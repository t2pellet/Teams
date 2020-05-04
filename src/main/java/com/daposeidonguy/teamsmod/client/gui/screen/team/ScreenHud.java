package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import com.daposeidonguy.teamsmod.client.gui.screen.ScreenPages;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class ScreenHud extends ScreenPages {


    protected ScreenHud(ScreenBase parent) {
        super(new StringTextComponent("hudmanager"), parent);
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
            if (uid.equals(minecraft.player.getUniqueID())) {
                if (teamIterator.hasNext()) {
                    uid = teamIterator.next();
                } else {
                    return;
                }
            }
            GameProfile otherP = minecraft.player.connection.getPlayerInfo(uid).getGameProfile();
            Button button = new Button(guiLeft + WIDTH / 2 - 62, guiTop + yOffset, 124, 20, otherP.getName() + ": " + GuiHandler.priorityPlayers.contains(otherP.getId()), btn -> {
                boolean isPriority = GuiHandler.priorityPlayers.contains(otherP.getId());
                if (isPriority) {
                    GuiHandler.priorityPlayers.remove(otherP.getId());
                } else {
                    GuiHandler.priorityPlayers.add(otherP.getId());
                }
                btn.setMessage(otherP.getName() + ": " + !isPriority);
            });
            this.addButton(button);
            yOffset += 25;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString("Set Priority Players:", guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Set Priority Players") / 2, guiTop + 10, Color.BLACK.getRGB());

    }
}
