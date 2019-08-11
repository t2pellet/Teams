package com.daposeidonguy.teamsmod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessageRequest implements IMessage {

    private NBTTagCompound tagId = new NBTTagCompound();

    public MessageRequest() {

    }

    public MessageRequest(UUID id1, UUID id2) {
        tagId.setString("uid1",id1.toString());
        tagId.setString("uid2",id2.toString());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tagId= ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf,tagId);
    }
    public static class MessageHandler implements IMessageHandler<MessageRequest,IMessage> {
        @Override
        public IMessage onMessage(MessageRequest message, MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                EntityPlayer p1 = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(UUID.fromString(message.tagId.getString("uid1")));
                EntityPlayer p2 = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(UUID.fromString(message.tagId.getString("uid2")));
                if (p1==null || p2==null) {
                    return;
                }
                if(p1==null) {
                } else if (p2==null) {
                }
                PacketHandler.INSTANCE.sendTo(new MessageHunger(p2.getFoodStats().getFoodLevel()), (EntityPlayerMP) p1);
            });
            return null;
        }
    }
}
