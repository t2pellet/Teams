package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.common.inventory.InterfaceTransfer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

/* Sent/received when a player opens Item Transfer GUI */
public class MessageGuiTransfer extends AbstractMessage {


    public MessageGuiTransfer(final UUID id, final String name) {
        tag.setUniqueId("id", id);
        tag.setString("name", name);
    }

    public static class MessageHandler implements IMessageHandler<MessageGuiTransfer, IMessage> {
        @Override
        public IMessage onMessage(MessageGuiTransfer message, MessageContext ctx) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                if (message.tag.hasKey("id") && message.tag.hasKey("name")) {
                    EntityPlayerMP p = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(message.tag.getUniqueId("id"));
                    if (p != null) {
                        p.displayGui(new InterfaceTransfer(message.tag.getString("name")));
                    }
                }
            });
            return null;
        }
    }

}
