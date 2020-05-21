package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.client.ClientHandler;
import com.mojang.realmsclient.util.Pair;
import com.sun.javafx.geom.Vec2d;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessagePos extends AbstractMessage {

    public MessagePos() {
        super();
    }

    public MessagePos(EntityPlayerMP playerMP) {
        tag.setDouble("x", playerMP.posX);
        tag.setDouble("z", playerMP.posZ);
        tag.setInteger("dim", playerMP.getEntityWorld().provider.getDimension());
        tag.setUniqueId("id", playerMP.getUniqueID());
    }

    public static class MessageHandler implements IMessageHandler<MessagePos, IMessage> {
        @Override
        public IMessage onMessage(MessagePos message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                UUID playerId = message.tag.getUniqueId("id");
                Vec2d pos = new Vec2d(message.tag.getDouble("x"), message.tag.getDouble("z"));
                ClientHandler.idtoPosMap.put(playerId, Pair.of(message.tag.getInteger("dim"), pos));
            });
            return null;
        }
    }
}
