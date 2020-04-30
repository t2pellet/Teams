package com.daposeidonguy.teamsmod.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageDeath {

    public MessageDeath() {
    }

    public MessageDeath(PacketBuffer buf) {
    }

    public void encode(PacketBuffer buf) {
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            ctx.get().enqueueWork(() -> {
                Minecraft.getInstance().player.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0F, 5.0F);
            });
        });
        ctx.get().setPacketHandled(true);
    }

}
