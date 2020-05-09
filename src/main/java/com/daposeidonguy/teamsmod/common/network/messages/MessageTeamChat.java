package com.daposeidonguy.teamsmod.common.network.messages;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;
import java.util.function.Supplier;

/* Sent/received when a player cycles between team and server chat */
public class MessageTeamChat extends AbstractMessage {

    public MessageTeamChat(PacketBuffer buf) {
        super(buf);
    }

    public MessageTeamChat(UUID uuid, boolean teamChat) {
        tag.putUniqueId("uuid", uuid);
        tag.putBoolean("teamChat", teamChat);
    }

    @Override
    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (EffectiveSide.get().isServer()) {
                PlayerEntity playerEntity = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(tag.getUniqueId("uuid"));
                    if (playerEntity != null) {
                        CompoundNBT playerData = playerEntity.getPersistentData();
                        playerData.putBoolean("teamChat", tag.getBoolean("teamChat"));
                    }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
