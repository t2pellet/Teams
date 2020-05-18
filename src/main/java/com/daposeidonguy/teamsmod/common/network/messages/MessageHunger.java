package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

/* Sent/received to update client hunger maps */
public class MessageHunger extends AbstractMessage {

    public MessageHunger() {
        super();
    }

    public MessageHunger(final UUID id, int hunger) {
        tag.setUniqueId("id", id);
        tag.setInteger("hunger", hunger);
    }

    public static class MessageHandler implements IMessageHandler<MessageHunger, IMessage> {
        @Override
        public IMessage onMessage(MessageHunger message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                GuiHandler.hungerMap.put(message.tag.getUniqueId("id"), message.tag.getInteger("hunger"));
            });
            return null;
        }
    }
}
