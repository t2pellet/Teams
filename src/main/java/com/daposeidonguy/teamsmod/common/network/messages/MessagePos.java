package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.client.ClientHelper;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec2f;
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
                Vec2f pos = new Vec2f((float) message.tag.getDouble("x"), (float) message.tag.getDouble("z"));
                ClientHelper.idtoPosMap.put(playerId, Pair.of(message.tag.getInteger("dim"), pos));
            });
            return null;
        }
    }
}
