package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MessageHealth {

    private CompoundNBT tag = new CompoundNBT();

    public MessageHealth() {
    }

    public MessageHealth(UUID id, int health) {
        tag.putString("id", id.toString());
        tag.putInt("health", health);
    }

    public MessageHealth(PacketBuffer buf) {
        tag = buf.readCompoundTag();
    }

    public void encode(PacketBuffer buf) {
        buf.writeCompoundTag(tag);
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
