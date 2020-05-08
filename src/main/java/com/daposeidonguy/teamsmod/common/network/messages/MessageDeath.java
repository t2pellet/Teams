package com.daposeidonguy.teamsmod.common.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/* Sent/received when a player dies */
public class MessageDeath extends AbstractMessage {

    public MessageDeath(PacketBuffer buf) {
        super(buf);
    }

    public MessageDeath() {
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            Minecraft.getInstance().player.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0F, 5.0F);
        }));
        ctx.get().setPacketHandled(true);
    }

}
