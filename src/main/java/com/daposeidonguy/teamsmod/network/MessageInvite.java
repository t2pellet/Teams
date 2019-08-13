package com.daposeidonguy.teamsmod.network;

import com.daposeidonguy.teamsmod.team.SaveData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessageInvite implements IMessage {

    private NBTTagCompound tagId = new NBTTagCompound();

    public MessageInvite() {

    }

    public MessageInvite(String name, UUID id) {
        tagId.setString("name",name);
        tagId.setString("uid",id.toString());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tagId= ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf,tagId);
    }
    public static class MessageHandler implements IMessageHandler<MessageInvite,IMessage> {
        @Override
        public IMessage onMessage(MessageInvite message, MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                SaveData data = SaveData.get(FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld());
                EntityPlayer invitee = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getPlayerEntityByName(message.tagId.getString("name"));
                EntityPlayer inviter = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(UUID.fromString(message.tagId.getString("uid")));
                if(invitee==null) {
                    inviter.sendMessage(new TextComponentString("Player is not online / invalid username"));
                    return;
                }
                data.addPlayer(inviter,invitee.getUniqueID());
                PacketHandler.INSTANCE.sendToAll(new MessageSaveData(SaveData.listTeams));
            });
            return null;
        }
    }
}
