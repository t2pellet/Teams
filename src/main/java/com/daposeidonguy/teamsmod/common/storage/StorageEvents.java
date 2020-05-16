package com.daposeidonguy.teamsmod.common.storage;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.network.messages.MessageSaveData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/* Handles events relating to updating team save data */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID)
public class StorageEvents {

    public static TeamDataManager data;

    /* Sends SaveData packet to player on login */
    @SubscribeEvent
    public static void playerLogIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            event.player.getEntityData().setBoolean("teamChat", false);
            PacketHandler.INSTANCE.sendToAll(new MessageSaveData());
            PacketHandler.INSTANCE.sendTo(new MessageSaveData(), (EntityPlayerMP) event.player);
        }
    }

}
