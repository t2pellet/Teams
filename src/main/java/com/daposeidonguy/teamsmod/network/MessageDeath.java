package com.daposeidonguy.teamsmod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDeath implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class MessageHandler implements IMessageHandler<MessageDeath,IMessage> {
        @Override
        public IMessage onMessage(MessageDeath message, MessageContext ctx) {
            FMLClientHandler.instance().getClient().addScheduledTask(() -> {
               FMLClientHandler.instance().getClientPlayerEntity().playSound(SoundEvents.ENTITY_LIGHTNING_THUNDER,1.0F,1.0F);
            });
            return null;
        }
    }
}
