package com.daposeidonguy.teamsmod.network;

import com.daposeidonguy.teamsmod.client.gui.ToastInvite;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageInvite implements IMessage {

    private String teamName;

    public MessageInvite() {
        super();
    }

    public MessageInvite(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        teamName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, teamName);
    }

    public static class MessageHandler implements IMessageHandler<MessageInvite, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageInvite message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Minecraft.getMinecraft().getToastGui().add(new ToastInvite(message.teamName));
            });
            return null;
        }
    }
}
