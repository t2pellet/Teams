package com.daposeidonguy.teamsmod.handlers;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.network.*;
import com.daposeidonguy.teamsmod.team.SaveData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
                PacketHandler.INSTANCE.sendToAll(new MessageHealth(playerMP.getUniqueID(), MathHelper.ceil(playerMP.getHealth())));
                PacketHandler.INSTANCE.sendToAll(new MessageHunger(playerMP.getUniqueID(), playerMP.getFoodStats().getFoodLevel()));
            }
            ticks = 0;
        }
    }

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        if (!event.getPlayer().getEntityWorld().isRemote) {
            PacketHandler.INSTANCE.sendToAll(new MessageChat(event.getPlayer().getDisplayNameString(), event.getMessage()));
            if (!ConfigHandler.server.disablePrefix) {
                String teamName = SaveData.teamMap.get(event.getPlayer().getUniqueID());
                if (teamName != null) {
                    TextComponentString prefix = new TextComponentString("[" + teamName + "] ");
                    event.setComponent(prefix.appendSibling(event.getComponent()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDamage(LivingHurtEvent event) {
        if (event.getEntity() instanceof EntityPlayer && !event.getEntity().getEntityWorld().isRemote) {
            if (SaveData.teamMap.containsKey(event.getEntity().getUniqueID())) {
                int health = MathHelper.ceil(event.getEntityLiving().getHealth() - event.getAmount());
                PacketHandler.INSTANCE.sendToAll(new MessageHealth(event.getEntity().getUniqueID(), health));
                PacketHandler.INSTANCE.sendToAll(new MessageHunger(event.getEntity().getUniqueID(), ((EntityPlayer) event.getEntity()).getFoodStats().getFoodLevel()));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayer && !event.getEntity().getEntityWorld().isRemote) {
            String team = SaveData.teamMap.get(event.getEntity().getUniqueID());
            if (team != null) {
                Iterator<UUID> uuidIterator = SaveData.teamsMap.get(team).iterator();
                while (uuidIterator.hasNext()) {
                    UUID uuid = uuidIterator.next();
                    if (!event.getEntity().getUniqueID().equals(uuid)) {
                        EntityPlayerMP playerMP = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuid);
                        PacketHandler.INSTANCE.sendTo(new MessageDeath(), playerMP);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerHeal(LivingHealEvent event) {
        if (event.getEntity() instanceof EntityPlayer && !event.getEntity().getEntityWorld().isRemote) {
            if (SaveData.teamMap.containsKey(event.getEntity().getUniqueID())) {
                int health = MathHelper.ceil(event.getEntityLiving().getHealth() + event.getAmount());
                PacketHandler.INSTANCE.sendToAll(new MessageHealth(event.getEntity().getUniqueID(), health));
                PacketHandler.INSTANCE.sendToAll(new MessageHunger(event.getEntity().getUniqueID(), ((EntityPlayer) event.getEntity()).getFoodStats().getFoodLevel()));
            }
        }
    }

    @SubscribeEvent
    public static void playerHitPlayer(LivingAttackEvent event) {
        if (!ConfigHandler.server.enableFriendlyFire && event.getSource().getTrueSource() instanceof EntityPlayer && event.getEntityLiving() instanceof EntityPlayer && !event.getEntityLiving().getEntityWorld().isRemote) {
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
    public static void configUpdate(ConfigChangedEvent.PostConfigChangedEvent event) {
        if (event.getModID().equals(TeamsMod.MODID)) {
            ConfigManager.sync(TeamsMod.MODID, Config.Type.INSTANCE);
            System.out.println("Config updated");
        }
    }

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.player.getEntityWorld().isRemote) {
            PacketHandler.INSTANCE.sendToAll(new MessageSaveData(SaveData.teamsMap));
            PacketHandler.INSTANCE.sendTo(new MessageSaveData(SaveData.teamsMap), (EntityPlayerMP) event.player);
        }
        if (!event.player.getEntityWorld().isRemote && !ConfigHandler.server.disableAchievementSync) {
            if (SaveData.teamMap.containsKey(event.player.getUniqueID())) {
                String team = SaveData.teamMap.get(event.player.getUniqueID());
                SaveData.syncPlayers(team, (EntityPlayerMP) event.player);
            }
        }
    }

    @SubscribeEvent
    public static void playerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.player.getEntityWorld().isRemote && !FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer()) {
            PacketHandler.INSTANCE.sendToAll(new MessageSaveData(SaveData.teamsMap));
        }
    }

    @SubscribeEvent
    public static void achievementGet(AdvancementEvent event) {
        if (!ConfigHandler.server.disableAchievementSync && !event.getEntity().getEntityWorld().isRemote) {
            EntityPlayer player = event.getEntityPlayer();
            if (SaveData.teamMap.containsKey(player.getUniqueID())) {
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
