package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.client.ClientHandler;
import com.daposeidonguy.teamsmod.client.chat.ChatHandler;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/* Sent/received when a player sends a chat message */
public class MessageNewChat extends AbstractMessage {

    public MessageNewChat(final PacketBuffer buf) {
        super(buf);
    }

    public MessageNewChat(final String username, final String message, boolean teamChat) {
        tag.putString("username", username);
        tag.putString("message", message);
        tag.putBoolean("teamChat", teamChat);
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            Pair<String, Long> chatPair = new Pair<>(tag.getString("message"), ClientHandler.ticks);
            ChatHandler.lastMessageReceived = new Pair<>(ClientHandler.nametoIdMap.get(tag.getString("username")), tag.getString("message"));
            GuiHandler.chatMap.put(tag.getString("username"), chatPair);
            ChatHandler.lastMessageTeam = tag.getBoolean("teamChat");
        }));
        ctx.get().setPacketHandled(true);
    }

}
