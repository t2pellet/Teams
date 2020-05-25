package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.network.messages.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.UUID;

/* Handles events to send network messages */
/* Note : SaveData messages are handled by SaveDataEvents */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID)
public class PacketEvents {

    /* Events related to sending MessageHealth and MessageHunger packets */

    private static int ticks = 0;

    /* Updates tick counter and sends Hunger and Health packet every 250 ticks */
    @SubscribeEvent
    public static void tickEvent(final TickEvent.ServerTickEvent event) {
        ticks += 1;
        if (ticks == 200 && FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            List<EntityPlayerMP> playerMPList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
            for (EntityPlayerMP playerMP : playerMPList) {
                PacketHandler.sendToTeam(playerMP, new MessageHealth(playerMP.getUniqueID(), MathHelper.ceil(playerMP.getHealth())));
                PacketHandler.sendToTeam(playerMP, new MessageHunger(playerMP.getUniqueID(), MathHelper.ceil(playerMP.getFoodStats().getFoodLevel())));
            }
            ticks = 0;
        }
    }

    /* Sends Hunger and Health packet when a player is hurt */
    @SubscribeEvent
    public static void onPlayerDamage(final LivingHurtEvent event) {
        if (event.getEntity() instanceof EntityPlayer && FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            UUID playerID = event.getEntity().getUniqueID();
            EntityPlayerMP playerEntity = (EntityPlayerMP) event.getEntityLiving();
            PacketHandler.sendToTeam(playerEntity, new MessageHealth(playerID, MathHelper.ceil(event.getEntityLiving().getHealth() - event.getAmount())));
            PacketHandler.sendToTeam(playerEntity, new MessageHunger(playerID, playerEntity.getFoodStats().getFoodLevel()));
        }
    }

    /* Sends Hunger and Health Packet when a player heals */
    @SubscribeEvent
    public static void onPlayerHeal(final LivingHealEvent event) {
        if (event.getEntity() instanceof EntityPlayer && FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            UUID playerID = event.getEntity().getUniqueID();
            EntityPlayerMP playerEntity = (EntityPlayerMP) event.getEntityLiving();
            PacketHandler.sendToTeam(playerEntity, new MessageHealth(playerID, MathHelper.ceil(event.getEntityLiving().getHealth() + event.getAmount())));
            PacketHandler.sendToTeam(playerEntity, new MessageHunger(playerID, playerEntity.getFoodStats().getFoodLevel()));
        }
    }

    /* Miscellaneous events */

    /* Sends Chat packet to all players when ServerChatEvent fires */
    @SubscribeEvent
    public static void onPlayerChat(final ServerChatEvent event) {
        boolean teamChat = event.getPlayer().getEntityData().getBoolean("teamChat");
        PacketHandler.INSTANCE.sendToAll(new MessageNewChat(event.getPlayer().getGameProfile().getName(), event.getMessage(), teamChat));
    }

    /* Sends MessageDeath packet to all players on the dead players team when LivingDeathEvent fires */
    @SubscribeEvent
    public static void onPlayerDeath(final LivingDeathEvent event) {
        if (event.getEntity() instanceof EntityPlayer && FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            if (!TeamConfig.common.disableDeathSound) {
                PacketHandler.sendToTeam((EntityPlayerMP) event.getEntityLiving(), new MessageDeath());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerMove(final LivingEvent.LivingUpdateEvent event) {
        if (!event.getEntity().getEntityWorld().isRemote && event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) event.getEntityLiving();
            PacketHandler.sendToTeam(playerMP, new MessagePos(playerMP));
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.player.world.isRemote) {
            PacketHandler.INSTANCE.sendTo(new MessageConfig(), (EntityPlayerMP) event.player);
        }
    }

}
