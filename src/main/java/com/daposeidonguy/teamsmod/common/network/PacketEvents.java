package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.network.messages.MessageDeath;
import com.daposeidonguy.teamsmod.common.network.messages.MessageHealth;
import com.daposeidonguy.teamsmod.common.network.messages.MessageHunger;
import com.daposeidonguy.teamsmod.common.network.messages.MessageNewChat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
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

import java.util.UUID;

/* Handles events to send network messages */
/* Note : SaveData messages are handled by SaveDataEvents */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PacketEvents {

    /* Events related to sending MessageHealth and MessageHunger packets */

    private static int ticks = 0;

    /* Updates tick counter and sends Hunger and Health packet every 250 ticks */
    @SubscribeEvent
    public static void tickEvent(final TickEvent.ServerTickEvent event) {
        ticks += 1;
        if (ticks == 250 && EffectiveSide.get().isServer()) {
            for (ServerPlayerEntity playerMP : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                PacketHandler.sendToTeam(playerMP, new MessageHealth(playerMP.getUniqueID(), MathHelper.ceil(playerMP.getHealth())));
                PacketHandler.sendToTeam(playerMP, new MessageHunger(playerMP.getUniqueID(), MathHelper.ceil(playerMP.getFoodStats().getFoodLevel())));
            }
            ticks = 0;
        }
    }

    /* Sends Hunger and Health packet when a player is hurt */
    @SubscribeEvent
    public static void onPlayerDamage(final LivingHurtEvent event) {
        if (event.getEntity() instanceof PlayerEntity && EffectiveSide.get().isServer()) {
            UUID playerID = event.getEntity().getUniqueID();
            ServerPlayerEntity playerEntity = (ServerPlayerEntity) event.getEntityLiving();
            PacketHandler.sendToTeam(playerEntity, new MessageHealth(playerID, MathHelper.ceil(event.getEntityLiving().getHealth() - event.getAmount())));
            PacketHandler.sendToTeam(playerEntity, new MessageHunger(playerID, playerEntity.getFoodStats().getFoodLevel()));
        }
    }

    /* Sends Hunger and Health Packet when a player heals */
    @SubscribeEvent
    public static void onPlayerHeal(final LivingHealEvent event) {
        if (event.getEntity() instanceof PlayerEntity && EffectiveSide.get().isServer()) {
            UUID playerID = event.getEntity().getUniqueID();
            ServerPlayerEntity playerEntity = (ServerPlayerEntity) event.getEntityLiving();
            PacketHandler.sendToTeam(playerEntity, new MessageHealth(playerID, MathHelper.ceil(event.getEntityLiving().getHealth() - event.getAmount())));
            PacketHandler.sendToTeam(playerEntity, new MessageHunger(playerID, playerEntity.getFoodStats().getFoodLevel()));
        }
    }

    /* Miscellaneous events */

    /* Sends Chat packet to all players when ServerChatEvent fires */
    @SubscribeEvent
    public static void onPlayerChat(final ServerChatEvent event) {
        boolean teamChat = event.getPlayer().getPersistentData().getBoolean("teamChat");
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageNewChat(event.getPlayer().getGameProfile().getName(), event.getMessage(), teamChat));
    }

    /* Sends MessageDeath packet to all players on the dead players team when LivingDeathEvent fires */
    @SubscribeEvent
    public static void onPlayerDeath(final LivingDeathEvent event) {
        if (event.getEntity() instanceof PlayerEntity && EffectiveSide.get().isServer()) {
            if (!TeamConfig.disableDeathSound) {
                PacketHandler.sendToTeam((ServerPlayerEntity) event.getEntityLiving(), new MessageDeath());
            }
        }
    }

}
