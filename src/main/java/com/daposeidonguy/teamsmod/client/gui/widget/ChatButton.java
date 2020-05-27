package com.daposeidonguy.teamsmod.client.gui.widget;

import com.daposeidonguy.teamsmod.client.ClientHelper;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.common.network.NetworkHelper;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.network.messages.MessageTeamChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;

public class ChatButton extends ClearButton {

    private static final int buttonWidth1 = ClientHelper.mc.fontRenderer.getStringWidth("Display: Server Chat");
    private static final int buttonWidth2 = ClientHelper.mc.fontRenderer.getStringWidth("Display: Team Chat");

    public ChatButton(int buttonId, int xIn, int yIn, int widthIn, int heightIn) {
        super(buttonId, xIn, yIn, widthIn, heightIn, "Display: Server Chat", (button -> {
            try {
                GuiNewChat oldGui = (GuiNewChat) GuiHandler.persistentChatGUI.get(ClientHelper.mc.ingameGUI);
                GuiHandler.persistentChatGUI.set(ClientHelper.mc.ingameGUI, GuiHandler.backupChatGUI);
                GuiHandler.backupChatGUI = oldGui;
                GuiHandler.displayTeamChat = !GuiHandler.displayTeamChat;
                NetworkHelper.sendToServer(new MessageTeamChat(ClientHelper.mc.player.getUniqueID(), GuiHandler.displayTeamChat));
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
            if (GuiHandler.displayTeamChat) {
                button.displayString = "Display: Team Chat";
                button.setWidth(buttonWidth2);
                button.x = (int) (ClientHelper.getWindow().getScaledWidth() * 0.99 - buttonWidth2);
            } else {
                button.displayString = "Display: Server Chat";
                button.setWidth(buttonWidth1);
                button.x = (int) (ClientHelper.getWindow().getScaledWidth() * 0.99 - buttonWidth1);
            }
        }));
        if (GuiHandler.displayTeamChat) {
            this.displayString = "Display: Team Chat";
            this.setWidth(buttonWidth2);
            this.x = (int) (ClientHelper.getWindow().getScaledWidth() * 0.99 - buttonWidth2);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        activate();
        super.mouseReleased(mouseX, mouseY);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        drawRect(x - 2, y - 2, x + width + 2, y + height, (int) (179.0D) << 24 & -16777216);
        super.drawButton(mc, mouseX, mouseY, partialTicks);
    }
}
