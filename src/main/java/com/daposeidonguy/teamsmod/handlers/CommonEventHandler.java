package com.daposeidonguy.teamsmod.handlers;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.network.MessageHealth;
import com.daposeidonguy.teamsmod.network.MessageHunger;
import com.daposeidonguy.teamsmod.network.MessageSaveData;
import com.daposeidonguy.teamsmod.network.PacketHandler;
import com.daposeidonguy.teamsmod.team.SaveData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;
import java.util.UUID;

@Mod.EventBusSubscriber
public class CommonEventHandler {

    public static int ticks = 0;

    @SubscribeEvent
    public static void tickEvent(TickEvent.ServerTickEvent event) {
        ticks += 1;
        if (ticks == 200 && !FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().isRemote) {
            Iterator<EntityPlayerMP> playerMPIterator = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().iterator();
            while (playerMPIterator.hasNext()) {
                EntityPlayerMP playerMP = playerMPIterator.next();
                PacketHandler.INSTANCE.sendToAll(new MessageHealth(playerMP.getUniqueID(), (int) playerMP.getHealth()));
                PacketHandler.INSTANCE.sendToAll(new MessageHunger(playerMP.getUniqueID(), playerMP.getFoodStats().getFoodLevel()));
            }
            ticks = 0;
        }
    }

    @SubscribeEvent
    public static void onPlayerDamage(LivingHurtEvent event) {
        if (event.getEntity() instanceof EntityPlayer && !event.getEntity().getEntityWorld().isRemote) {
            if (SaveData.teamMap.containsKey(event.getEntity().getUniqueID())) {
                PacketHandler.INSTANCE.sendToAll(new MessageHealth(event.getEntity().getUniqueID(), (int) ((EntityPlayer) event.getEntity()).getHealth()));
                PacketHandler.INSTANCE.sendToAll(new MessageHunger(event.getEntity().getUniqueID(), ((EntityPlayer) event.getEntity()).getFoodStats().getFoodLevel()));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHeal(LivingHealEvent event) {
        if (event.getEntity() instanceof EntityPlayer && !event.getEntity().getEntityWorld().isRemote) {
            if (SaveData.teamMap.containsKey(event.getEntity().getUniqueID())) {
                PacketHandler.INSTANCE.sendToAll(new MessageHealth(event.getEntity().getUniqueID(), (int) ((EntityPlayer) event.getEntity()).getHealth()));
                PacketHandler.INSTANCE.sendToAll(new MessageHunger(event.getEntity().getUniqueID(), ((EntityPlayer) event.getEntity()).getFoodStats().getFoodLevel()));
            }
        }
    }

    @SubscribeEvent
    public static void playerHitPlayer(LivingAttackEvent event) {
        if (!ConfigHandler.server.enableFriendlyFire && event.getSource().getTrueSource() instanceof EntityPlayer && event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) event.getSource().getTrueSource();
            EntityPlayer target = (EntityPlayer) event.getEntityLiving();
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

    @SubscribeEvent
    public static void configUpdate(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(TeamsMod.MODID)) {
            ConfigManager.sync(TeamsMod.MODID, Config.Type.INSTANCE);
        }
    }

    @SubscribeEvent
    public static void logIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.player.getEntityWorld().isRemote) {
            PacketHandler.INSTANCE.sendToAll(new MessageSaveData(SaveData.teamsMap));
        }
    }

    @SubscribeEvent
    public static void playerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.player.getEntityWorld().isRemote) {
            PacketHandler.INSTANCE.sendToAll(new MessageSaveData(SaveData.teamsMap));
        }
    }

    @SubscribeEvent
    public static void playerJoin(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
            try {
                SaveData.get(event.getWorld());
            } catch (NoClassDefFoundError ex) {
            }
            System.out.println("New player: " + ((EntityPlayer) event.getEntity()).getDisplayNameString());
            PacketHandler.INSTANCE.sendToAll(new MessageSaveData(SaveData.teamsMap));
        }
        if (!event.getWorld().isRemote && !ConfigHandler.server.disableAchievementSync) {
            if (event.getEntity() instanceof EntityPlayer) {
                if (SaveData.teamMap.containsKey(event.getEntity().getUniqueID())) {
                    String team = SaveData.teamMap.get(event.getEntity().getUniqueID());
                    SaveData.syncPlayers(team, (EntityPlayerMP) event.getEntity());

                }
            }
        }
    }

    @SubscribeEvent
    public static void achievementGet(AdvancementEvent event) {
        if (!ConfigHandler.server.disableAchievementSync) {
            EntityPlayer player = event.getEntityPlayer();
            if (SaveData.teamMap.containsKey(player.getUniqueID()) && !event.getEntity().getEntityWorld().isRemote) {
                String team = SaveData.teamMap.get(player.getUniqueID());
                Iterator<UUID> uuidIterator = SaveData.teamsMap.get(team).iterator();
                while (uuidIterator.hasNext()) {
                    UUID id = uuidIterator.next();
                    EntityPlayerMP playerMP = (EntityPlayerMP) player.getEntityWorld().getPlayerEntityByUUID(id);
                    SaveData.syncPlayers(team, playerMP);
                }
            }
        }
    }

}
