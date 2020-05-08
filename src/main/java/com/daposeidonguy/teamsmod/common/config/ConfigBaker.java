package com.daposeidonguy.teamsmod.common.config;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigBaker {

    @SubscribeEvent
    public static void configEvent(ModConfig.ModConfigEvent event) {
        if (event.getConfig().getSpec() == ConfigHolder.CLIENT_SPEC) {
            ConfigBaker.bakeClient();
        } else if (event.getConfig().getSpec() == ConfigHolder.SERVER_SPEC) {
            ConfigBaker.bakeServer();
        }
    }

    /* Retrieves values from the client config */
    public static void bakeClient() {
        TeamConfig.smallIcon = ConfigHolder.CLIENT_CONFIG.smallIcon.get();
        TeamConfig.useAlternatePosition = ConfigHolder.CLIENT_CONFIG.useAlternatePosition.get();
        TeamConfig.disableTeamsHUD = ConfigHolder.CLIENT_CONFIG.disableTeamsHUD.get();
        TeamConfig.disablePing = ConfigHolder.CLIENT_CONFIG.disablePing.get();
        TeamConfig.disablePrefix = ConfigHolder.CLIENT_CONFIG.disablePrefix.get();
        TeamConfig.disableChatBubble = ConfigHolder.CLIENT_CONFIG.disableChatBubble.get();
    }

    /* Retrieves values from the server config */
    public static void bakeServer() {
        TeamConfig.enableFriendlyFire = ConfigHolder.SERVER_CONFIG.enableFriendlyFire.get();
        TeamConfig.disableAchievementSync = ConfigHolder.SERVER_CONFIG.disableAchievementSync.get();
        TeamConfig.disableInventoryTransfer = ConfigHolder.SERVER_CONFIG.disableInventoryTransfer.get();
        TeamConfig.noOpRemoveTeam = ConfigHolder.SERVER_CONFIG.noOpRemoveTeam.get();
        TeamConfig.disablePrefixServer = ConfigHolder.SERVER_CONFIG.disablePrefix.get();
    }
}