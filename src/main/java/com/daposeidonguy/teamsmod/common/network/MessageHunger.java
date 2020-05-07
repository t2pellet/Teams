package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/* Sent/received to update client hunger maps */
public class MessageHunger extends AbstractMessage {

    protected MessageHunger(PacketBuffer buf) {
        super(buf);
    }

    public MessageHunger(UUID id, int health) {
        tag.putString("id", id.toString());
        tag.putInt("hunger", health);
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            UUID uid;
            try {
                uid = UUID.fromString(tag.getString("id"));
            } catch (IllegalArgumentException ex) {
                return;
            }
            GuiHandler.hungerMap.put(uid, tag.getInt("hunger"));
        });
        ctx.get().setPacketHandled(true);
    }
}
