package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

/* Sent/received to update client health maps */
public class MessageHealth extends AbstractMessage {

    public MessageHealth() {
        super();
    }

    public MessageHealth(final UUID id, int health) {
        tag.setUniqueId("id", id);
        tag.setInteger("health", health);
    }

    public static class MessageHandler implements IMessageHandler<MessageHealth, IMessage> {
        @Override
        public IMessage onMessage(MessageHealth message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                GuiHandler.healthMap.put(message.tag.getUniqueId("id"), message.tag.getInteger("health"));
            });
            return null;
        }
    }

}
