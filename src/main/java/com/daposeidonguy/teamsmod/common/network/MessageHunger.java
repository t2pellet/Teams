package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MessageHunger {

    private CompoundNBT tag = new CompoundNBT();

    public MessageHunger() {
    }

    public MessageHunger(UUID id, int health) {
        tag.putString("id", id.toString());
        tag.putInt("hunger", health);
    }

    public MessageHunger(PacketBuffer buf) {
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
            GuiHandler.hungerMap.put(uid, tag.getInt("hunger"));
        });
        ctx.get().setPacketHandled(true);
    }
}
