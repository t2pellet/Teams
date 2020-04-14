package com.daposeidonguy.teamsmod.network;

import com.daposeidonguy.teamsmod.client.GuiHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessageHealth implements IMessage {

    private NBTTagCompound tag = new NBTTagCompound();


    public MessageHealth() {

    }

    public MessageHealth(UUID id, int health) {
        tag.setString("id", id.toString());
        tag.setInteger("health", health);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, tag);
    }

    public static class MessageHandler implements IMessageHandler<MessageHealth, IMessage> {
        @Override
        public IMessage onMessage(MessageHealth message, MessageContext ctx) {
            NBTTagCompound tagCompound = message.tag;
            Minecraft.getMinecraft().addScheduledTask(() -> {
                UUID uid;
                System.out.println("New packet: MessageHealth");
                try {
                    uid = UUID.fromString(tagCompound.getString("id"));
                } catch (IllegalArgumentException ex) {
                    return;
                }
                GuiHandler.healthMap.put(uid, tagCompound.getInteger("health"));
            });
            return null;
        }
    }
}
