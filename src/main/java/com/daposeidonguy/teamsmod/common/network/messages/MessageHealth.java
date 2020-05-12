package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/* Sent/received to update client health maps */
public class MessageHealth extends AbstractMessage {

    public MessageHealth(final PacketBuffer buf) {
        super(buf);
    }

    public MessageHealth(final UUID id, int health) {
        tag.putString("id", id.toString());
        tag.putInt("health", health);
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            UUID uid;
            try {
                uid = UUID.fromString(tag.getString("id"));
            } catch (IllegalArgumentException ex) {
                return;
            }
            GuiHandler.healthMap.put(uid, tag.getInt("health"));
        });
        ctx.get().setPacketHandled(true);
    }

}
