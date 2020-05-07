package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.client.ClientEventHandler;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageChat extends AbstractMessage {

    protected MessageChat(PacketBuffer buf) {
        super(buf);
    }

    public MessageChat(String username, String message) {
        tag.putString("username", username);
        tag.putString("message", message);
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Pair<String, Long> chatPair = new Pair(tag.getString("message"), ClientEventHandler.ticks);
            ClientEventHandler.chatMap.put(tag.getString("username"), chatPair);
            ClientEventHandler.lastMessageReceived = tag.getString("message");
        });
        ctx.get().setPacketHandled(true);
    }

}
