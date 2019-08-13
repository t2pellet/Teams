package com.daposeidonguy.teamsmod.network;

import com.daposeidonguy.teamsmod.team.SaveData;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessageTeam implements IMessage {

    private NBTTagCompound tagId = new NBTTagCompound();

    public MessageTeam() {

    }

    public MessageTeam(String name, UUID id) {
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
    public static class MessageHandler implements IMessageHandler<MessageTeam,IMessage> {
        @Override
        public IMessage onMessage(MessageTeam message, MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                SaveData data = SaveData.get(FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld());
                data.addTeam(message.tagId.getString("name"),FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(UUID.fromString(message.tagId.getString("uid"))));
                PacketHandler.INSTANCE.sendToAll(new MessageSaveData(SaveData.listTeams));
            });
            return null;
        }
    }
}
