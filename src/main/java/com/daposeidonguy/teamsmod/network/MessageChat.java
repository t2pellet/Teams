package com.daposeidonguy.teamsmod.network;

import com.daposeidonguy.teamsmod.handlers.ClientEventHandler;
import com.mojang.realmsclient.util.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageChat implements IMessage {

    private NBTTagCompound tag = new NBTTagCompound();


    public MessageChat() {

    }

    public MessageChat(String username, String message) {
        tag.setString("username", username);
        tag.setString("message", message);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, tag);
    }

    public static class MessageHandler implements IMessageHandler<MessageChat, IMessage> {
        @Override
        public IMessage onMessage(MessageChat message, MessageContext ctx) {
            NBTTagCompound tagCompound = message.tag;
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Pair<String, Long> chatPair = Pair.of(tagCompound.getString("message"), ClientEventHandler.ticks);
                ClientEventHandler.chatMap.put(tagCompound.getString("username"), chatPair);
                ClientEventHandler.lastMessage = tagCompound.getString("message");
            });
            return null;
        }
    }
}
