package com.daposeidonguy.teamsmod.network;

import com.daposeidonguy.teamsmod.team.SaveData;
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

public class MessageRequestData implements IMessage {

    private NBTTagCompound tagId = new NBTTagCompound();

    public MessageRequestData() {

    }

    public MessageRequestData(UUID id1) {
        tagId.setString("uid1",id1.toString());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tagId= ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf,tagId);
    }
    public static class MessageHandler implements IMessageHandler<MessageRequestData,IMessage> {
        @Override
        public IMessage onMessage(MessageRequestData message, MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                EntityPlayer p1 = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(UUID.fromString(message.tagId.getString("uid1")));
                if (p1==null) {
                    return;
                }
                PacketHandler.INSTANCE.sendTo(new MessageSaveData(SaveData.listTeams),(EntityPlayerMP)p1);
            });
            return null;
        }
    }
}
