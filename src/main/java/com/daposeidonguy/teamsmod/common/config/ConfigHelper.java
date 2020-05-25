package com.daposeidonguy.teamsmod.common.config;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.network.messages.MessageConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigHelper {

    public static boolean serverDisableDeathSound = false;
    public static boolean serverDisableTransfer = false;
    public static boolean serverDisablePing = false;
    public static boolean serverDisableStatus = false;
    public static boolean serverDisableCompass = false;

    @SubscribeEvent
    public static void onConfigUpdate(ConfigChangedEvent.OnConfigChangedEvent event) {
        System.out.println("LALALA");
        if (event.getModID().equals(TeamsMod.MODID)) {
            System.out.println("CONFIG CHANGED");
            ConfigManager.sync(TeamsMod.MODID, Config.Type.INSTANCE);
            PacketHandler.INSTANCE.sendToAll(new MessageConfig());
        }
    }
}
