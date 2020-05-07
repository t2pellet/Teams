package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.client.ClientEventHandler;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/* Sent/received when a player sends a chat message */
public class MessageNewChat extends AbstractMessage {

    protected MessageNewChat(PacketBuffer buf) {
        super(buf);
    }

    public MessageNewChat(String username, String message, boolean teamChat) {
        tag.putString("username", username);
        tag.putString("message", message);
        tag.putBoolean("teamChat", teamChat);
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            Pair<String, Long> chatPair = new Pair(tag.getString("message"), ClientEventHandler.ticks);
            ClientEventHandler.lastMessageReceived = new Pair(ClientEventHandler.nametoIdMap.get(tag.getString("username")), tag.getString("message"));
            GuiHandler.chatMap.put(tag.getString("username"), chatPair);
            ClientEventHandler.lastMessageTeam = tag.getBoolean("teamChat");
        }));
        ctx.get().setPacketHandled(true);
    }

}
