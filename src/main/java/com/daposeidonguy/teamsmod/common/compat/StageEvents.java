package com.daposeidonguy.teamsmod.common.compat;

import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Iterator;
import java.util.UUID;

public class StageEvents {

    /* Syncs gamestages among teammates when GameStageEvent fires */
    @SubscribeEvent
    public void gameStage(final GameStageEvent event) {
        if (!event.getEntityLiving().getEntityWorld().isRemote && event.getEntityLiving() instanceof PlayerEntity && ModList.get().isLoaded("gamestages")) {
            ServerPlayerEntity playerEntity = (ServerPlayerEntity) event.getEntityLiving();
            String teamName = StorageHelper.getTeam(playerEntity.getUniqueID());
            if (teamName != null) {
                Iterator<UUID> teamIterator = StorageHelper.getTeamPlayers(teamName).iterator();
                while (teamIterator.hasNext()) {
                    UUID playerId = teamIterator.next();
                    ServerPlayerEntity teamPlayer = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(playerId);
                    if (teamPlayer != null) {
                        GameStageHelper.addStage(teamPlayer, event.getStageName());
                    }
                }
            }
        }
    }

}
