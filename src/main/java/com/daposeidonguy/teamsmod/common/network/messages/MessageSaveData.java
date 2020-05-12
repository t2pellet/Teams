package com.daposeidonguy.teamsmod.common.network.messages;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

import static com.daposeidonguy.teamsmod.common.storage.StorageHandler.readFromNBT;
import static com.daposeidonguy.teamsmod.common.storage.StorageHandler.writeToNBT;

/* Sent/received to update team save data and configuration data */
public class MessageSaveData extends AbstractMessage {

    public MessageSaveData(final PacketBuffer buf) {
        super(buf);
    }

    public MessageSaveData(ServerWorld world) {
        tag = writeToNBT(new CompoundNBT());
    }

    public void onMessage(final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> readFromNBT(tag));
        ctx.get().setPacketHandled(true);
    }

}
