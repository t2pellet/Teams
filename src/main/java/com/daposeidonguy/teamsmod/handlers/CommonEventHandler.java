package com.daposeidonguy.teamsmod.handlers;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.network.MessageSaveData;
import com.daposeidonguy.teamsmod.network.PacketHandler;
import com.daposeidonguy.teamsmod.team.SaveData;
import com.daposeidonguy.teamsmod.team.Team;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatList;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.UUID;

@Mod.EventBusSubscriber
public class CommonEventHandler {

    @SubscribeEvent
    public static void arrowShootPlayer(LivingAttackEvent event) {
        if (!ConfigHandler.enableFriendlyFire && event.getSource().getTrueSource() instanceof EntityPlayer && event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) event.getSource().getTrueSource();
            EntityPlayer target = (EntityPlayer)event.getEntityLiving();
            Team targetTeam = Team.getTeam(target.getUniqueID());
            Team attackerTeam = Team.getTeam(attacker.getUniqueID());
            if(targetTeam!=null && attackerTeam!=null && targetTeam.getName().equals(attackerTeam.getName())) {
                event.setCanceled(true);
                attacker.sendMessage(new TextComponentString("Don't attack your own teammate!"));
            }
        }
    }

    @SubscribeEvent
    public static void playerHitPlayer(AttackEntityEvent event) {
        if(!ConfigHandler.enableFriendlyFire && (event.getEntityLiving() instanceof EntityPlayer) && event.getTarget() instanceof EntityPlayer && !event.getEntity().getEntityWorld().isRemote) {
            EntityPlayer target = (EntityPlayer)event.getTarget();
            EntityPlayer attacker = event.getEntityPlayer();
            Team targetTeam = Team.getTeam(target.getUniqueID());
            Team attackerTeam = Team.getTeam(attacker.getUniqueID());
            if(targetTeam!=null && attackerTeam!=null && targetTeam.getName().equals(attackerTeam.getName())) {
                event.setCanceled(true);
                attacker.sendMessage(new TextComponentString("Don't attack your own teammate!"));
            }
        }
    }

    @SubscribeEvent
    public static void configUpdate(ConfigChangedEvent.OnConfigChangedEvent event) {
        if(event.getModID().equals(TeamsMod.MODID)) {
            ConfigManager.sync(TeamsMod.MODID, Config.Type.INSTANCE);
        }
    }

    @SubscribeEvent
    public static void playerChat(ServerChatEvent event) {
        if(!event.getPlayer().getServerWorld().isRemote) {
            EntityPlayerMP p = event.getPlayer();
            Team team = Team.getTeam(p.getUniqueID());

            if (team!=null && !ConfigHandler.disablePrefix) {
                String message = "[" + team.getName() + "]" + " <" +  p.getDisplayNameString() + "> "  + event.getMessage();
                event.setComponent(new TextComponentTranslation(message));
            }
        }
    }

    @SubscribeEvent
    public static void logIn(PlayerEvent.PlayerLoggedInEvent event) {
        if(!event.player.getEntityWorld().isRemote) {
            if(((EntityPlayerMP)event.player).getStatFile().readStat(StatList.LEAVE_GAME)==0) {
                event.player.sendMessage(new TextComponentString("Welcome to the server! This server has a teams system allowing you to disable PvP, sync advancements and see health/hunger of your teammates!\nType /team to get started"));
            }
        }
    }

    @SubscribeEvent
    public static void playerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        PacketHandler.INSTANCE.sendToAll(new MessageSaveData(SaveData.listTeams));
    }

    @SubscribeEvent
    public static void playerJoin(EntityJoinWorldEvent event) {
        if(!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
            SaveData data = SaveData.get(event.getWorld());
            PacketHandler.INSTANCE.sendToAll(new MessageSaveData(SaveData.listTeams));
        }
        if(!event.getWorld().isRemote && !ConfigHandler.disableAchievementSync) {
            if (event.getEntity() instanceof EntityPlayer && !event.getWorld().isRemote) {
                EntityPlayerMP player = (EntityPlayerMP)event.getEntity();
                Team team = Team.getTeam(player.getUniqueID());
                if(team!=null) {
                    Team.syncPlayers(team,player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void achievementGet(AdvancementEvent event) {
        if(!ConfigHandler.disableAchievementSync) {
            EntityPlayer player = event.getEntityPlayer();
            Team team = Team.getTeam(player.getUniqueID());
            if(team!=null && !event.getEntity().getEntityWorld().isRemote) {
                for (UUID id : team.getPlayers()) {
                    EntityPlayerMP playerMP = (EntityPlayerMP)player.getEntityWorld().getPlayerEntityByUUID(id);
                    if(playerMP!=null) {
                        for (String s : playerMP.getAdvancements().getProgress(event.getAdvancement()).getRemaningCriteria()) {
                            playerMP.getAdvancements().grantCriterion(event.getAdvancement(),s);
                        }
                    }
                }
            }
        }
    }

}
