package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.client.gui.toasts.ToastInvite;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/* Sent to player invited to a team */
public class MessageInvite extends AbstractMessage {

    public MessageInvite(final String teamName) {
        tag.setString("teamName", teamName);
    }

    @SideOnly(Side.CLIENT)
    private static void displayToast(MessageInvite message) {
        Minecraft.getMinecraft().getToastGui().add(new ToastInvite(message.tag.getString("teamName")));
    }

    public static class MessageHandler implements IMessageHandler<MessageInvite, IMessage> {
        @Override
        public IMessage onMessage(MessageInvite message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> displayToast(message));
            return null;
        }
    }
}
