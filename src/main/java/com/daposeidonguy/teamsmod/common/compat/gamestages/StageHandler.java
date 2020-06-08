package com.daposeidonguy.teamsmod.common.compat.gamestages;

import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;

public class StageHandler {

    public static void syncStages(final String team, final ServerPlayerEntity player) {
        if (EffectiveSide.get().isServer() && player != null && ModList.get().isLoaded("gamestages")) {
            for (String stageName : GameStageHelper.getKnownStages()) {
                for (UUID id : StorageHelper.getTeamPlayers(team)) {
                    ServerPlayerEntity teammate = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(id);
                    if (teammate != null) {
                        if (GameStageHelper.hasStage(teammate, stageName)) {
                            GameStageHelper.addStage(player, stageName);
                        } else if (GameStageHelper.hasStage(player, stageName)) {
                            GameStageHelper.addStage(teammate, stageName);
                        }
                    }
                }
            }
        }
    }

}
