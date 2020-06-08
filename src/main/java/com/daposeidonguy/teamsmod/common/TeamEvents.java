package com.daposeidonguy.teamsmod.common;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.compat.gamestages.StageHandler;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;

/* Handles events relating to miscellaneous team features */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
class TeamEvents {

    /* Syncs advancements with player and their team upon login (depending on config) */
    @SubscribeEvent
    public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().getEntityWorld().isRemote && !TeamConfig.disableAdvancementSync && StorageHelper.isPlayerInTeam(event.getPlayer().getUniqueID())) {
            String team = StorageHelper.getTeam(event.getPlayer().getUniqueID());
            if (!StorageHelper.getTeamSetting(team, "disableAdvancementSync")) {
                StorageHandler.syncAdvancements(team, (ServerPlayerEntity) event.getEntity());
                StageHandler.syncStages(team, (ServerPlayerEntity) event.getEntity());
            }
        }
    }

    /* Cancels damage and knockback when friendly fire occurs (depending on config) */
    @SubscribeEvent
    public static void playerHitPlayer(final LivingAttackEvent event) {
        if (!event.getEntityLiving().getEntityWorld().isRemote && !TeamConfig.enableFriendlyFire && event.getSource().getTrueSource() instanceof PlayerEntity && event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity attacker = (PlayerEntity) event.getSource().getTrueSource();
            PlayerEntity target = (PlayerEntity) event.getEntityLiving();
            String targetTeam = StorageHelper.getTeam(target.getUniqueID());
            String attackerTeam = StorageHelper.getTeam(attacker.getUniqueID());
            if (targetTeam != null && targetTeam.equals(attackerTeam) && !StorageHelper.getTeamSetting(targetTeam, "enableFriendlyFire")) {
                event.setCanceled(true);
            }
        }
    }

    /* Syncs advancement among teammates when AdvancementEvent fires */
    @SubscribeEvent
    public static void achievementGet(final AdvancementEvent event) {
        if (!TeamConfig.disableAdvancementSync && !event.getEntity().getEntityWorld().isRemote) {
            String team = StorageHelper.getTeam(event.getPlayer().getUniqueID());
            if (StorageHelper.doesTeamExist(team) && !StorageHelper.getTeamSetting(team, "disableAdvancementSync")) {
                Advancement adv = event.getAdvancement();
                ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
                for (UUID playerID : StorageHelper.getTeamPlayers(team)) {
                    ServerPlayerEntity teammate = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(playerID);
                    if (teammate != null) {
                        for (String s : player.getAdvancements().getProgress(adv).getCompletedCriteria()) {
                            teammate.getAdvancements().grantCriterion(adv, s);
                        }
                    }
                }
            }
        }
    }

    /* Adds prefix to chat message */
    @SubscribeEvent
    public static void onServerChat(final ServerChatEvent event) {
        if (!TeamConfig.disablePrefix) {
            String teamName = StorageHelper.getTeam(event.getPlayer().getUniqueID());
            if (teamName != null) {
                StringTextComponent prefix = new StringTextComponent("[" + teamName + "] ");
                event.setComponent(prefix.appendSibling(event.getComponent()));
            }
        }
    }

}
