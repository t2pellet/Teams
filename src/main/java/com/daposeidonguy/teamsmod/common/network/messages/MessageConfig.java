package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageConfig extends AbstractMessage {

    public MessageConfig() {
        tag.setBoolean("disablePing", TeamConfig.common.disablePing);
        tag.setBoolean("disableTransfer", TeamConfig.server.disableInventoryTransfer);
        tag.setBoolean("disableDeathSound", TeamConfig.common.disableDeathSound);
    }

    public static class MessageHandler implements IMessageHandler<MessageConfig, IMessage> {
        @Override
        public IMessage onMessage(MessageConfig message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                TeamConfig.serverDisablePing = message.tag.getBoolean("disablePing");
                TeamConfig.serverDisableTransfer = message.tag.getBoolean("disableTransfer");
                TeamConfig.serverDisableDeathSound = message.tag.getBoolean("disableDeathSound");
            });
            return null;
        }
    }
}
