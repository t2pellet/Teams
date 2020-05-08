package com.daposeidonguy.teamsmod.common.network.messages;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;
import java.util.function.Supplier;

/* Sent/received when a player cycles between command and server chat */
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
                Iterable<ServerWorld> worlds = ServerLifecycleHooks.getCurrentServer().getWorlds();
                for (ServerWorld world : worlds) {
                    PlayerEntity playerEntity = world.getPlayerByUuid(tag.getUniqueId("uuid"));
                    if (playerEntity != null) {
                        CompoundNBT playerData = world.getPlayerByUuid(tag.getUniqueId("uuid")).getPersistentData();
                        playerData.putBoolean("teamChat", tag.getBoolean("teamChat"));
                        break;
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
