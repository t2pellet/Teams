package com.daposeidonguy.teamsmod.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TeamConfig {

    //Client
    public static boolean smallIcon;
    public static boolean useAlternatePosition;
    public static boolean disableChatBubble;
    public static boolean disableStatusOverlay;
    public static boolean disableCompassOverlay;

    //Common
    public static boolean disablePing;
    public static boolean disablePrefix;
    public static boolean disableDeathSound;

    //Server
    public static boolean enableFriendlyFire;
    public static boolean disableAdvancementSync;
    public static boolean noOpRemoveTeam;
    public static boolean disableInventoryTransfer;


    public static class ClientConfig {
        final ForgeConfigSpec.BooleanValue smallIcon;
        final ForgeConfigSpec.BooleanValue useAlternatePosition;
        final ForgeConfigSpec.BooleanValue disableChatBubble;
        final ForgeConfigSpec.BooleanValue disableStatusOverlay;
        final ForgeConfigSpec.BooleanValue disableCompassOverlay;

        ClientConfig(final ForgeConfigSpec.Builder builder) {
            builder.push("Gui");
            smallIcon = builder.comment("Use smaller button to open teams GUI").define("smallIcon", false);
            useAlternatePosition = builder.comment("Use alternate position for teams GUI button").define("useAlternatePosition", false);
            disableStatusOverlay = builder.comment("Disable HUD display showing teammate health and hunger").define("disableTeamsHUD", false);
            disableCompassOverlay = builder.comment("Disable HUD display showing teammate location").define("disableCompassOverlay", false);
            builder.pop();
            builder.push("Miscellaneous");
            disableChatBubble = builder.comment("Disable chat bubbles above player heads").define("disableChatBubble", false);
            builder.pop();
        }
    }

    public static class CommonConfig {
        /* Common */
        final ForgeConfigSpec.BooleanValue disablePrefix;
        final ForgeConfigSpec.BooleanValue disablePing;
        final ForgeConfigSpec.BooleanValue disableDeathSound;
        /* Server */
        final ForgeConfigSpec.BooleanValue enableFriendlyFire;
        final ForgeConfigSpec.BooleanValue disableAdvancementSync;
        final ForgeConfigSpec.BooleanValue noOpRemoveTeam;
        final ForgeConfigSpec.BooleanValue disableInventoryTransfer;

        CommonConfig(final ForgeConfigSpec.Builder builder) {
            builder.push("Common").push("Chat");
            disablePrefix = builder.comment("Disable chat prefixes").define("disablePrefix", false);
            disablePing = builder.comment("Disable ping sound when your name or your team name is mentioned in chat").define("disablePing", false);
            builder.pop().push("Miscellaneous");
            disableDeathSound = builder.comment("Disable thunder sound when a teammate dies").define("disableDeathSound", false);
            builder.pop(2);

            builder.push("Server").push("Team");
            enableFriendlyFire = builder.comment("Enable damaging teammates").define("enableFriendlyFire", false);
            disableAdvancementSync = builder.comment("Disable synchronized advancements").define("disableAdvancementSync", false);
            noOpRemoveTeam = builder.comment("Allow non-operators to delete teams").define("noOpRemoveTeam", false);
            disableInventoryTransfer = builder.comment("Disable the inventory transfer feature").define("disableInventoryTransfer", false);
            builder.pop(2);
        }
    }

}
