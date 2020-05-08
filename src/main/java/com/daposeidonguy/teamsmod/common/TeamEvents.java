package com.daposeidonguy.teamsmod.common;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.UUID;

/* Handles events relating to miscellaneous team features */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TeamEvents {

    /* Syncs advancements with player and their command upon login (depending on config) */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!TeamConfig.disableAchievementSync) {
            if (SaveData.teamMap.containsKey(event.getPlayer().getUniqueID())) {
                String team = SaveData.teamMap.get(event.getPlayer().getUniqueID());
                SaveData.syncPlayers(team, (ServerPlayerEntity) event.getEntity());
            }
        }
    }

    /* Cancels damage and knockback when friendly fire occurs (depending on config) */
    @SubscribeEvent
    public static void playerHitPlayer(LivingAttackEvent event) {
        if (!TeamConfig.enableFriendlyFire && event.getSource().getTrueSource() instanceof PlayerEntity && event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity attacker = (PlayerEntity) event.getSource().getTrueSource();
            PlayerEntity target = (PlayerEntity) event.getEntityLiving();
            String targetTeam = null;
            String attackerTeam = null;
            if (SaveData.teamMap.containsKey(target.getUniqueID())) {
                targetTeam = SaveData.teamMap.get(target.getUniqueID());
            }
            if (SaveData.teamMap.containsKey(attacker.getUniqueID())) {
                attackerTeam = SaveData.teamMap.get(attacker.getUniqueID());
            }
            if (targetTeam != null && attackerTeam != null && targetTeam.equals(attackerTeam)) {
                event.setCanceled(true);
            }
        }
    }

    /* Syncs advancement among teammates when AdvancementEvent fires */
    @SubscribeEvent
    public static void achievementGet(AdvancementEvent event) {
        if (!TeamConfig.disableAchievementSync) {
            PlayerEntity player = event.getPlayer();
            if (SaveData.teamMap.containsKey(player.getUniqueID()) && !event.getEntity().getEntityWorld().isRemote) {
                String team = SaveData.teamMap.get(player.getUniqueID());
                Iterator<UUID> uuidIterator = SaveData.teamsMap.get(team).iterator();
                while (uuidIterator.hasNext()) {
                    UUID id = uuidIterator.next();
                    ServerPlayerEntity playerMP = (ServerPlayerEntity) player.getEntityWorld().getPlayerByUuid(id);
                    SaveData.syncPlayers(team, playerMP);
                }
            }
        }
    }

}
