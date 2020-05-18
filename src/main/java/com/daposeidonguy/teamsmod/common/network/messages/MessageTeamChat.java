package com.daposeidonguy.teamsmod.common.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

/* Sent/received when a player cycles between team and server chat */
public class MessageTeamChat extends AbstractMessage {

    public MessageTeamChat() {
        super();
    }

    public MessageTeamChat(final UUID uuid, boolean teamChat) {
        tag.setUniqueId("uuid", uuid);
        tag.setBoolean("teamChat", teamChat);
    }

    public static class MessageHandler implements IMessageHandler<MessageTeamChat, IMessage> {
        @Override
        public IMessage onMessage(MessageTeamChat message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final EntityPlayer playerEntity = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(message.tag.getUniqueId("uuid"));
                if (playerEntity != null) {
                    playerEntity.getEntityData().setBoolean("teamChat", message.tag.getBoolean("teamChat"));
                }
            });
            return null;
        }
    }
}
