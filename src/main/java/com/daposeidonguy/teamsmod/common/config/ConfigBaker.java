package com.daposeidonguy.teamsmod.common.config;

public class ConfigBaker {

    public static void bakeClient() {
        TeamConfig.smallIcon = ConfigHolder.CLIENT_CONFIG.smallIcon.get();
        TeamConfig.useAlternatePosition = ConfigHolder.CLIENT_CONFIG.useAlternatePosition.get();
        TeamConfig.disableTeamsHUD = ConfigHolder.CLIENT_CONFIG.disableTeamsHUD.get();
        TeamConfig.disablePing = ConfigHolder.CLIENT_CONFIG.disablePing.get();
        TeamConfig.disablePrefix = ConfigHolder.CLIENT_CONFIG.disablePrefix.get();
        TeamConfig.disableChatBubble = ConfigHolder.CLIENT_CONFIG.disableChatBubble.get();
    }

    public static void bakeServer() {
        TeamConfig.enableFriendlyFire = ConfigHolder.SERVER_CONFIG.enableFriendlyFire.get();
        TeamConfig.disableAchievementSync = ConfigHolder.SERVER_CONFIG.disableAchievementSync.get();
        TeamConfig.disableInventoryTransfer = ConfigHolder.SERVER_CONFIG.disableInventoryTransfer.get();
        TeamConfig.noOpRemoveTeam = ConfigHolder.SERVER_CONFIG.noOpRemoveTeam.get();
    }
}
