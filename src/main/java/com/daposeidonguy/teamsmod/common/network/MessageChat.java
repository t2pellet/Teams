package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.client.ClientEventHandler;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageChat {

    private CompoundNBT tag = new CompoundNBT();

    public MessageChat() {
    }

    public MessageChat(String username, String message) {
        tag.putString("username", username);
        tag.putString("message", message);
    }

    public MessageChat(PacketBuffer buf) {
        tag = buf.readCompoundTag();
    }

    public void encode(PacketBuffer buf) {
        buf.writeCompoundTag(tag);
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
