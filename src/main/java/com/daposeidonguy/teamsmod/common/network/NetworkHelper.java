package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.common.network.messages.AbstractMessage;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;

public class NetworkHelper {

    public static void sendToTeam(final ServerPlayerEntity player, final AbstractMessage message) {
        String teamName = StorageHelper.getTeam(player.getUniqueID());
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (teamName != null) {
            for (UUID playerId : StorageHelper.getTeamPlayers(teamName)) {
                if (!playerId.equals(player.getUniqueID())) {
                    ServerPlayerEntity teamPlayer = server.getPlayerList().getPlayerByUUID(playerId);
                    if (teamPlayer != null) {
                        sendToPlayer(teamPlayer, message);
                    }
                }
            }
        }
    }

    public static void sendToPlayer(final ServerPlayerEntity player, final AbstractMessage message) {
        NetworkHelper.sendToPlayer(player, message);
    }

    public static void sendToAll(final AbstractMessage message) {
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    public static void sendToServer(final AbstractMessage message) {
        PacketHandler.INSTANCE.sendToServer(message);
    }
}
