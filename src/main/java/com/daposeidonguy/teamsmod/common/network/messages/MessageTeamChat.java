package com.daposeidonguy.teamsmod.common.network.messages;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;
import java.util.function.Supplier;

/* Sent/received when a player cycles between team and server chat */
public class MessageTeamChat extends AbstractMessage {

    public MessageTeamChat(final PacketBuffer buf) {
        super(buf);
    }

    public MessageTeamChat(final UUID uuid, boolean teamChat) {
        tag.putUniqueId("uuid", uuid);
        tag.putBoolean("teamChat", teamChat);
    }

    @Override
    public void onMessage(final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final PlayerEntity playerEntity = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(tag.getUniqueId("uuid"));
            if (playerEntity != null) {
                playerEntity.getPersistentData().putBoolean("teamChat", tag.getBoolean("teamChat"));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
