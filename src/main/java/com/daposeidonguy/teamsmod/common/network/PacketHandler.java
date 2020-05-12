package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.network.messages.*;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;

/* Registers network messages */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PacketHandler {

    private static final String PROTOCOL_VERSION = "2";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TeamsMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void register() {
        TeamsMod.logger.info("Registering packets...");
        int id = 0;
        INSTANCE.registerMessage(++id, MessageDeath.class, MessageDeath::encode, MessageDeath::new, MessageDeath::onMessage);
        INSTANCE.registerMessage(++id, MessageGuiTransfer.class, MessageGuiTransfer::encode, MessageGuiTransfer::new, MessageGuiTransfer::onMessage);
        INSTANCE.registerMessage(++id, MessageHealth.class, MessageHealth::encode, MessageHealth::new, MessageHealth::onMessage);
        INSTANCE.registerMessage(++id, MessageHunger.class, MessageHunger::encode, MessageHunger::new, MessageHunger::onMessage);
        INSTANCE.registerMessage(++id, MessageSaveData.class, MessageSaveData::encode, MessageSaveData::new, MessageSaveData::onMessage);
        INSTANCE.registerMessage(++id, MessageInvite.class, MessageInvite::encode, MessageInvite::new, MessageInvite::onMessage);
        INSTANCE.registerMessage(++id, MessageNewChat.class, MessageNewChat::encode, MessageNewChat::new, MessageNewChat::onMessage);
        INSTANCE.registerMessage(++id, MessageTeamChat.class, MessageTeamChat::encode, MessageTeamChat::new, MessageTeamChat::onMessage);
        INSTANCE.registerMessage(++id, MessageConfig.class, MessageConfig::encode, MessageConfig::new, MessageConfig::onMessage);
    }

    public static void sendToTeam(ServerPlayerEntity player, AbstractMessage message) {
        String teamName = StorageHandler.uuidToTeamMap.get(player.getUniqueID());
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (teamName != null) {
            for (UUID playerId : StorageHandler.teamToUuidsMap.get(teamName)) {
                if (!playerId.equals(player.getUniqueID())) {
                    ServerPlayerEntity teamPlayer = server.getPlayerList().getPlayerByUUID(playerId);
                    if (teamPlayer != null) {
                        INSTANCE.send(PacketDistributor.PLAYER.with(() -> teamPlayer), message);
                    }
                }
            }
        }
    }

}
