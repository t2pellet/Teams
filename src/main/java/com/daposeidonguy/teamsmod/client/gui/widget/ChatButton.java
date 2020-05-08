package com.daposeidonguy.teamsmod.client.gui.widget;

import com.daposeidonguy.teamsmod.client.ClientUtils;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.network.messages.MessageTeamChat;
import net.minecraft.client.gui.NewChatGui;

import static com.daposeidonguy.teamsmod.client.gui.GuiHandler.*;

public class ChatButton extends ClearButton {

    private static int buttonWidth1 = ClientUtils.mc.fontRenderer.getStringWidth("Display: Server Chat");
    private static int buttonWidth2 = ClientUtils.mc.fontRenderer.getStringWidth("Display: Team Chat");

    public ChatButton(int xIn, int yIn, int widthIn, int heightIn) {
        super(xIn, yIn, widthIn, heightIn, "Display: Server Chat", btn -> {
            try {
                NewChatGui oldGui = (NewChatGui) persistentChatGUI.get(ClientUtils.mc.ingameGUI);
                persistentChatGUI.set(ClientUtils.mc.ingameGUI, backupChatGUI);
                backupChatGUI = oldGui;
                displayTeamChat = !displayTeamChat;
                PacketHandler.INSTANCE.sendToServer(new MessageTeamChat(ClientUtils.mc.player.getUniqueID(), displayTeamChat));
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
            if (displayTeamChat) {
                btn.setMessage("Display: Team Chat");
                btn.setWidth(buttonWidth2);
                btn.x = (int) (ClientUtils.window.getScaledWidth() * 0.99 - buttonWidth2);
            } else {
                btn.setMessage("Display: Server Chat");
                btn.setWidth(buttonWidth1);
                btn.x = (int) (ClientUtils.window.getScaledWidth() * 0.99 - buttonWidth1);
            }
        });
        if (displayTeamChat) {
            this.setMessage("Display: Team Chat");
            this.setWidth(buttonWidth2);
            this.x = (int) (ClientUtils.window.getScaledWidth() * 0.99 - buttonWidth2);
        }
    }

    @Override
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        fill(x - 2, y - 2, x + width + 2, y + height, (int) (179.0D) << 24 & -16777216);
        super.renderButton(p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);
    }
}
