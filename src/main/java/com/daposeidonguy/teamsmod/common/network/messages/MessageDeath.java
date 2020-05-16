package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/* Sent/received when a player dies */
public class MessageDeath extends AbstractMessage {

    public MessageDeath() {
    }

    public static class MessageHandler implements IMessageHandler<MessageDeath, IMessage> {
        @Override
        public IMessage onMessage(MessageDeath message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (!TeamConfig.common.disableDeathSound && !TeamConfig.serverDisableDeathSound) {
                    Minecraft.getMinecraft().player.playSound(SoundEvents.ENTITY_LIGHTNING_THUNDER, 1.0F, 5.0F);
                }
            });
            return null;
        }
    }

}
