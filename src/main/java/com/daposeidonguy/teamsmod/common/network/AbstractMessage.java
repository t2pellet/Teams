package com.daposeidonguy.teamsmod.common.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class AbstractMessage {

    CompoundNBT tag = new CompoundNBT();

    /* Decodes PacketBuffer buf into CompoundNBT tag */
    public AbstractMessage(PacketBuffer buf) {
        tag = buf.readCompoundTag();
    }

    /* Second constructor used when first creating the message in SimpleChannel::send */
    AbstractMessage() {
    }

    /* Encodes CompoundNBT tag into PacketBuffer buf */
    void encode(PacketBuffer buf) {
        buf.writeCompoundTag(tag);
    }

    /* Handles logic for when the message is received, given context ctx */
    abstract void onMessage(Supplier<NetworkEvent.Context> ctx);
}
