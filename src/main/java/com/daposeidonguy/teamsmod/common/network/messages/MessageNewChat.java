package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.client.ClientHandler;
import com.daposeidonguy.teamsmod.client.chat.ChatHandler;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/* Sent/received when a player sends a chat message */
public class MessageNewChat extends AbstractMessage {

    public MessageNewChat(final String username, final String message, boolean teamChat) {
        tag.setString("username", username);
        tag.setString("message", message);
        tag.setBoolean("teamChat", teamChat);
    }

    public static class MessageHandler implements IMessageHandler<MessageNewChat, IMessage> {
        @Override
        public IMessage onMessage(MessageNewChat message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Pair<String, Long> chatPair = Pair.of(message.tag.getString("message"), ClientHandler.ticks);
                ChatHandler.lastMessageReceived = Pair.of(ClientHandler.nametoIdMap.get(message.tag.getString("username")), message.tag.getString("message"));
                GuiHandler.chatMap.put(message.tag.getString("username"), chatPair);
                ChatHandler.lastMessageTeam = message.tag.getBoolean("teamChat");
            });
            return null;
        }
    }

}
