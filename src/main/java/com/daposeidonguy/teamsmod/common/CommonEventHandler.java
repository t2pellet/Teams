package com.daposeidonguy.teamsmod.common;

import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.network.*;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Iterator;
import java.util.UUID;

public class CommonEventHandler {

    public static int ticks = 0;

    @SubscribeEvent
    public static void tickEvent(TickEvent.ServerTickEvent event) {
        ticks += 1;
        if (ticks == 200 && EffectiveSide.get().isServer()) {
            Iterator<ServerPlayerEntity> playerMPIterator = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().iterator();
            while (playerMPIterator.hasNext()) {
                ServerPlayerEntity playerMP = playerMPIterator.next();
                PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageHealth(playerMP.getUniqueID(), (int) playerMP.getHealth()));
                PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageHunger(playerMP.getUniqueID(), playerMP.getFoodStats().getFoodLevel()));
            }
            ticks = 0;
        }
    }

    @SubscribeEvent
    public static void onPlayerChat(ServerChatEvent event) {
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageChat(event.getPlayer().getGameProfile().getName(), event.getMessage()));
        if (TeamConfig.prefixServerSide && !TeamConfig.disablePrefixServer) {
            String teamName = SaveData.teamMap.get(event.getPlayer().getUniqueID());
            if (teamName != null) {
                StringTextComponent prefix = new StringTextComponent("[" + teamName + "] ");
                event.setComponent(prefix.appendSibling(event.getComponent()));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDamage(LivingHurtEvent event) {
        if (event.getEntity() instanceof PlayerEntity && !event.getEntity().getEntityWorld().isRemote) {
            if (SaveData.teamMap.containsKey(event.getEntity().getUniqueID())) {
                PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageHealth(event.getEntity().getUniqueID(), (int) ((PlayerEntity) event.getEntity()).getHealth()));
                PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageHunger(event.getEntity().getUniqueID(), ((PlayerEntity) event.getEntity()).getFoodStats().getFoodLevel()));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof PlayerEntity && !event.getEntity().getEntityWorld().isRemote) {
            String team = SaveData.teamMap.get(event.getEntity().getUniqueID());
            if (team != null) {
                Iterator<UUID> uuidIterator = SaveData.teamsMap.get(team).iterator();
                while (uuidIterator.hasNext()) {
                    UUID uuid = uuidIterator.next();
                    if (!event.getEntity().getUniqueID().equals(uuid)) {
                        ServerPlayerEntity playerMP = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(uuid);
                        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> playerMP), new MessageDeath());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHeal(LivingHealEvent event) {
        if (event.getEntity() instanceof PlayerEntity && !event.getEntity().getEntityWorld().isRemote) {
            if (SaveData.teamMap.containsKey(event.getEntity().getUniqueID())) {
                PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageHealth(event.getEntity().getUniqueID(), (int) ((PlayerEntity) event.getEntity()).getHealth()));
                PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageHunger(event.getEntity().getUniqueID(), ((PlayerEntity) event.getEntity()).getFoodStats().getFoodLevel()));
            }
        }
    }

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

    @SubscribeEvent
    public static void playerJoin(EntityJoinWorldEvent event) {
        if (EffectiveSide.get().isServer() && event.getEntity() instanceof PlayerEntity) {
            SaveData.get(event.getEntity().getServer().getWorld(DimensionType.OVERWORLD));
            PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(SaveData.teamsMap));
        }
        if (EffectiveSide.get().isServer() && !TeamConfig.disableAchievementSync) {
            if (event.getEntity() instanceof PlayerEntity) {
                if (SaveData.teamMap.containsKey(event.getEntity().getUniqueID())) {
                    String team = SaveData.teamMap.get(event.getEntity().getUniqueID());
                    SaveData.syncPlayers(team, (ServerPlayerEntity) event.getEntity());
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.getPlayer().getEntityWorld().isRemote) {
            PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(SaveData.teamsMap));
        }
    }

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
