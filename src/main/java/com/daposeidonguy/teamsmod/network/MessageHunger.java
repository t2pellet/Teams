package com.daposeidonguy.teamsmod.network;

import com.daposeidonguy.teamsmod.client.GuiTeam;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageHunger implements IMessage {

    private int hunger = 20;

    public MessageHunger() {

    }

    public MessageHunger(int hunger) {
        this.hunger = hunger;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hunger = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(hunger);
    }

    public static class MessageHandler implements IMessageHandler<MessageHunger, IMessage> {
        @Override
        public IMessage onMessage(MessageHunger message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                GuiTeam.hunger = message.hunger;
            });
            return null;
        }
    }
}
