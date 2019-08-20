package com.daposeidonguy.teamsmod.network;

import com.daposeidonguy.teamsmod.handlers.ConfigHandler;
import com.daposeidonguy.teamsmod.inventory.InterfaceTransfer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class MessageGui implements IMessage {

    private NBTTagCompound tag = new NBTTagCompound();

    public MessageGui() {
        super();
    }

    public MessageGui(UUID id,String name) {
        tag.setString("id",id.toString());
        tag.setString("name",name);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tag = ByteBufUtils.readTag(buf);

    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf,tag);
    }

    public static class MessageHandler implements IMessageHandler<MessageGui,IMessage> {
        @Override
        public IMessage onMessage(MessageGui message, MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                if(!ConfigHandler.server.disableInventoryTransfer) {
                    EntityPlayer p = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(UUID.fromString(message.tag.getString("id")));
                    p.displayGui(new InterfaceTransfer(message.tag.getString("name")));
                }
            });
            return null;
        }
    }
}
