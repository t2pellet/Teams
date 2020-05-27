package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.client.ClientHelper;
import com.daposeidonguy.teamsmod.client.chat.ChatHelper;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.PacketBuffer;
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
        ctx.get().enqueueWork(() -> {
            Pair<String, Long> chatPair = new Pair<>(tag.getString("message"), ClientHelper.ticks);
            ChatHelper.setLastMessage(ClientHelper.getIdFromName(tag.getString("username")), tag.getString("message"));
            GuiHandler.chatMap.put(tag.getString("username"), chatPair);
            ChatHelper.setLastMessageTeam(tag.getBoolean("teamChat"));
        });
        ctx.get().setPacketHandled(true);
    }

}
