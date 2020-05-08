package com.daposeidonguy.teamsmod.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TeamConfig {

    //Client
    public static boolean disablePing;
    public static boolean disablePrefix;
    public static boolean smallIcon;
    public static boolean useAlternatePosition;
    public static boolean disableChatBubble;
    public static boolean disableTeamsHUD;

    //Server
    public static boolean enableFriendlyFire;
    public static boolean disableAchievementSync;
    public static boolean noOpRemoveTeam;
    public static boolean disableInventoryTransfer;
    public static boolean disablePrefixServer;

    public static class ClientConfig {
        final ForgeConfigSpec.BooleanValue disablePing;
        final ForgeConfigSpec.BooleanValue disablePrefix;
        final ForgeConfigSpec.BooleanValue smallIcon;
        final ForgeConfigSpec.BooleanValue useAlternatePosition;
        final ForgeConfigSpec.BooleanValue disableChatBubble;
        final ForgeConfigSpec.BooleanValue disableTeamsHUD;

        ClientConfig(final ForgeConfigSpec.Builder builder) {
            builder.push("Gui");
            smallIcon = builder.comment("Use smaller button to open teams GUI").define("smallIcon", false);
            useAlternatePosition = builder.comment("Use alternate position for teams GUI button").define("useAlternatePosition", false);
            disableTeamsHUD = builder.comment("Disable HUD display showing teammate health and hunger").define("disableTeamsHUD", false);
            builder.pop();

            builder.push("Chat");
            disablePing = builder.comment("Disable bold text and ping sound when your command or playername is typed in chat").define("disablePing", false);
            disablePrefix = builder.comment("Disable command names as chat prefixes").define("disablePrefix", false);
            disableChatBubble = builder.comment("Disable chat bubbles above player heads").define("disableChatBubble", false);
            builder.pop();
        }
    }

    public static class ServerConfig {
        final ForgeConfigSpec.BooleanValue enableFriendlyFire;
        final ForgeConfigSpec.BooleanValue disableAchievementSync;
        final ForgeConfigSpec.BooleanValue noOpRemoveTeam;
        final ForgeConfigSpec.BooleanValue disableInventoryTransfer;
        final ForgeConfigSpec.BooleanValue disablePrefix;

        ServerConfig(final ForgeConfigSpec.Builder builder) {
            builder.push("Team");
            enableFriendlyFire = builder.comment("Enable damaging teammates").define("enableFriendlyFire", false);
            disableAchievementSync = builder.comment("Disable synchronized achievements").define("disableAchievementSync", false);
            noOpRemoveTeam = builder.comment("Allow non-operators to delete teams").define("noOpRemoveTeam", false);
            disableInventoryTransfer = builder.comment("Disable the inventory transfer feature").define("disableInventoryTransfer", false);
            builder.pop();

            builder.push("Chat");
            disablePrefix = builder.comment("Disable chat prefixes. Only applies if chatServerside = true").define("disablePrefix", false);
            builder.pop();
        }
    }
}
