package com.daposeidonguy.teamsmod.common.config;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraftforge.common.config.Config;

@Config(modid = TeamsMod.MODID)
public class TeamConfig {

    public static ClientConfig client = new ClientConfig();
    public static CommonConfig common = new CommonConfig();
    public static ServerConfig server = new ServerConfig();

    public static class ClientConfig {
        @Config.Comment("Uses small icon for button to open Teams GUI")
        public boolean smallIcon;
        @Config.Comment("Uses alternate position for button to open Teams GUI")
        public boolean useAlternatePosition;
        @Config.Comment("Disables chat bubbles from rendering")
        public boolean disableChatBubble;
        @Config.Comment("Disables status overlay from rendering")
        public boolean disableStatusOverlay;
        @Config.Comment("Disables compass overlay from rendering")
        public boolean disableCompassOverlay;
    }

    public static class CommonConfig {
        @Config.Comment("Disables player ping")
        public boolean disablePing;
        @Config.Comment("Disables player prefixes")
        public boolean disablePrefix;
        @Config.Comment("Disables death sound when teammate dies")
        public boolean disableDeathSound;
    }

    public static class ServerConfig {
        @Config.Comment("Enables friendly fire with teammates")
        public boolean enableFriendlyFire;
        @Config.Comment("Disables advancement syncing with teammates")
        public boolean disableAdvancementSync;
        @Config.Comment("Allows all players to remove teams")
        public boolean noOpRemoveTeam;
        @Config.Comment("Disables the inventory transfer feature for teams")
        public boolean disableInventoryTransfer;
        @Config.Comment("Force disable status overlay on connected clients")
        public boolean forceDisableStatus;
        @Config.Comment("Force disable compass overlay on connected clients")
        public boolean forceDisableCompass;
    }


}
