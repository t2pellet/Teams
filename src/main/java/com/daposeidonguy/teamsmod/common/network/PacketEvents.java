package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.network.messages.MessageDeath;
import com.daposeidonguy.teamsmod.common.network.messages.MessageHealth;
import com.daposeidonguy.teamsmod.common.network.messages.MessageHunger;
import com.daposeidonguy.teamsmod.common.network.messages.MessageNewChat;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Iterator;
import java.util.UUID;

/* Handles events to send network messages */
/* Note : SaveData messages are handled by SaveDataEvents */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PacketEvents {

    public static int ticks = 0;

    /* Updates tick counter and sends Hunger and Health packet every 250 ticks */
    @SubscribeEvent
    public static void tickEvent(TickEvent.ServerTickEvent event) {
        ticks += 1;
        if (ticks == 250 && EffectiveSide.get().isServer()) {
            Iterator<ServerPlayerEntity> playerMPIterator = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().iterator();
            while (playerMPIterator.hasNext()) {
                ServerPlayerEntity playerMP = playerMPIterator.next();
                PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageHealth(playerMP.getUniqueID(), (int) playerMP.getHealth()));
                PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageHunger(playerMP.getUniqueID(), playerMP.getFoodStats().getFoodLevel()));
            }
            ticks = 0;
        }
    }

    /* Sends Hunger and Health packet when a player is hurt */
    @SubscribeEvent
    public static void onPlayerDamage(LivingHurtEvent event) {
        if (event.getEntity() instanceof PlayerEntity && EffectiveSide.get().isServer()) {
            String teamName = SaveData.teamMap.get(event.getEntity().getUniqueID());
            if (teamName != null) {
                PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageHealth(event.getEntity().getUniqueID(), Math.round(event.getEntityLiving().getHealth() - event.getAmount())));
                PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageHunger(event.getEntity().getUniqueID(), ((PlayerEntity) event.getEntity()).getFoodStats().getFoodLevel()));
            }
        }
    }

    /* Sends Hunger and Health Packet when a player heals */
    @SubscribeEvent
    public static void onPlayerHeal(LivingHealEvent event) {
        if (event.getEntity() instanceof PlayerEntity && EffectiveSide.get().isServer()) {
            if (SaveData.teamMap.containsKey(event.getEntity().getUniqueID())) {
                PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageHealth(event.getEntity().getUniqueID(), (int) ((PlayerEntity) event.getEntity()).getHealth()));
                PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageHunger(event.getEntity().getUniqueID(), ((PlayerEntity) event.getEntity()).getFoodStats().getFoodLevel()));
            }
        }
    }

    /* Sends Chat packet to all players and sets prefix (if set in config) when ServerChatEvent fires */
    @SubscribeEvent
    public static void onPlayerChat(ServerChatEvent event) {
        boolean teamChat = event.getPlayer().getPersistentData().getBoolean("teamChat");
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageNewChat(event.getPlayer().getGameProfile().getName(), event.getMessage(), teamChat));
        if (!TeamConfig.disablePrefixServer) {
            String teamName = SaveData.teamMap.get(event.getPlayer().getUniqueID());
            if (teamName != null) {
                StringTextComponent prefix = new StringTextComponent("[" + teamName + "] ");
                event.setComponent(prefix.appendSibling(event.getComponent()));
            }
        }
    }

    /* Sends MessageDeath packet to all players on the dead players command when LivingDeathEvent fires */
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof PlayerEntity && EffectiveSide.get().isServer()) {
            String team = SaveData.teamMap.get(event.getEntity().getUniqueID());
            if (team != null) {
                Iterator<UUID> uuidIterator = SaveData.teamsMap.get(team).iterator();
                while (uuidIterator.hasNext()) {
                    UUID uuid = uuidIterator.next();
                    if (!event.getEntity().getUniqueID().equals(uuid)) {
                        ServerPlayerEntity playerMP = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(uuid);
                        if (playerMP != null) {
                            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> playerMP), new MessageDeath());
                        }
                    }
                }
            }
        }
    }

}
