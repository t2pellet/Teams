package com.daposeidonguy.teamsmod.common;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.UUID;

/* Handles events relating to miscellaneous team features */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID)
class TeamEvents {

    /* Syncs advancements with player and their team upon login (depending on config) */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.player.getEntityWorld().isRemote && !TeamConfig.server.disableAdvancementSync && StorageHelper.isPlayerInTeam(event.player.getUniqueID())) {
            String team = StorageHelper.getTeam(event.player.getUniqueID());
            if (!StorageHelper.getTeamSetting(team, "disableAdvancementSync")) {
                StorageHandler.syncPlayers(team, (EntityPlayerMP) event.player);
            }
        }
    }

    /* Cancels damage and knockback when friendly fire occurs (depending on config) */
    @SubscribeEvent
    public static void playerHitPlayer(final LivingAttackEvent event) {
        if (!event.getEntityLiving().getEntityWorld().isRemote && !TeamConfig.server.enableFriendlyFire && event.getSource().getTrueSource() instanceof EntityPlayer && event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) event.getSource().getTrueSource();
            EntityPlayer target = (EntityPlayer) event.getEntityLiving();
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
        if (!TeamConfig.server.disableAdvancementSync && !event.getEntity().getEntityWorld().isRemote) {
            String team = StorageHelper.getTeam(event.getEntityPlayer().getUniqueID());
            if (StorageHelper.doesTeamExist(team) && !StorageHelper.getTeamSetting(team, "disableAdvancementSync")) {
                Advancement adv = event.getAdvancement();
                EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
                for (UUID playerID : StorageHelper.getTeamPlayers(team)) {
                    EntityPlayerMP teammate = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(playerID);
                    if (teammate != null) {
                        for (String s : player.getAdvancements().getProgress(adv).getCompletedCriteria()) {
							if (!teammate.getAdvancements().getProgress(adv).getCriterionProgress(s).isObtained()) {
								teammate.getAdvancements().grantCriterion(adv, s);
							}
                        }
                    }
                }
            }
        }
    }

    /* Adds prefix to chat message */
    @SubscribeEvent
    public static void onServerChat(final ServerChatEvent event) {
        if (!TeamConfig.common.disablePrefix) {
            String teamName = StorageHelper.getTeam(event.getPlayer().getUniqueID());
            if (teamName != null) {
                TextComponentString prefix = new TextComponentString("[" + teamName + "] ");
                event.setComponent(prefix.appendSibling(event.getComponent()));
            }
        }
    }

}
