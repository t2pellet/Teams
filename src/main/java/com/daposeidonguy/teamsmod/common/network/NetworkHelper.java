package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.common.network.messages.AbstractMessage;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

public class NetworkHelper {
    public static void sendToTeam(final EntityPlayerMP player, final AbstractMessage message) {
        String teamName = StorageHelper.getTeam(player.getUniqueID());
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (teamName != null) {
            for (UUID playerId : StorageHelper.getTeamPlayers(teamName)) {
                if (!playerId.equals(player.getUniqueID())) {
                    EntityPlayerMP teamPlayer = server.getPlayerList().getPlayerByUUID(playerId);
                    if (teamPlayer != null) {
                        NetworkHelper.sendToPlayer(teamPlayer, message);
                    }
                }
            }
        }
    }

    public static void sendToPlayer(final EntityPlayerMP player, final AbstractMessage message) {
        PacketHandler.INSTANCE.sendTo(message, player);
    }

    public static void sendToAll(final AbstractMessage message) {
        PacketHandler.INSTANCE.sendToAll(message);
    }

    public static void sendToServer(final AbstractMessage message) {
        PacketHandler.INSTANCE.sendToServer(message);
    }
}
