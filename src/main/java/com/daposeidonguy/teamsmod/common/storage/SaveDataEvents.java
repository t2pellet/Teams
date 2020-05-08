package com.daposeidonguy.teamsmod.common.storage;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.network.messages.MessageSaveData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.PacketDistributor;

/* Handles events relating to updating command save data */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SaveDataEvents {

    /* Sends SaveData packet to all players and syncs players on the command of the logged in player on player login */
    @SubscribeEvent
    public static void playerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (EffectiveSide.get().isServer()) {
            event.getPlayer().getPersistentData().putBoolean("teamChat", false);
            PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(SaveData.teamsMap));
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new MessageSaveData(SaveData.teamsMap));
        }
    }

    /* Sends SaveData packet to all players on player logout */
    @SubscribeEvent
    public static void playerLogOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (EffectiveSide.get().isServer()) {
            PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(SaveData.teamsMap));
        }
    }

    /* Gets savedata when the server starts */
    @SubscribeEvent
    public static void serverStart(FMLServerStartingEvent event) {
        SaveData.get(event.getServer().getWorld(DimensionType.OVERWORLD));
    }

}
