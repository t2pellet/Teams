package com.daposeidonguy.teamsmod.common.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class AbstractMessage implements IMessage {

    NBTTagCompound tag = new NBTTagCompound();

    /* Constructor used when first creating the message in SimpleChannel::send */
    public AbstractMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, tag);
    }

}
