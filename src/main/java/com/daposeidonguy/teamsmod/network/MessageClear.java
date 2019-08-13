package com.daposeidonguy.teamsmod.network;

import com.daposeidonguy.teamsmod.team.SaveData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClear implements IMessage {

    public MessageClear() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class MessageHandler implements IMessageHandler<MessageClear, IMessage> {
        @Override
        public IMessage onMessage(MessageClear message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                SaveData.listTeams.clear();
            });
            return null;
        }
    }
}
