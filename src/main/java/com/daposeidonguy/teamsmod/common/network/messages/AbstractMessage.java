package com.daposeidonguy.teamsmod.common.network.messages;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class AbstractMessage {

    CompoundNBT tag = new CompoundNBT();

    /* Decodes PacketBuffer buf into CompoundNBT tag */
    AbstractMessage(final PacketBuffer buf) {
        tag = buf.readCompoundTag();
    }

    /* Second constructor used when first creating the message in SimpleChannel::send */
    AbstractMessage() {
    }

    /* Encodes CompoundNBT tag into PacketBuffer buf */
    public void encode(final PacketBuffer buf) {
        buf.writeCompoundTag(tag);
    }

    /* Handles logic for when the message is received, given context ctx */
    public abstract void onMessage(final Supplier<NetworkEvent.Context> ctx);
}
