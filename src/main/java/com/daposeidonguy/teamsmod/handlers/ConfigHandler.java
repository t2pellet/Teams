package com.daposeidonguy.teamsmod.handlers;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraftforge.common.config.Config;

@Config(modid = TeamsMod.MODID)
public class ConfigHandler {

    public static Client client = new Client();
    public static Server server = new Server();

    public static class Client {
        @Config.Comment({"Whether to enable pinging for players and teams"})
        public boolean disablePing;
        @Config.Comment({"Whether or not to have team prefixes enabled"})
        public boolean disablePrefix;
        @Config.Comment({"Whether the Teams button in the inventory screen should be smaller"})
        public boolean smallIcon;
        @Config.Comment({"Whether to use alternate GUI button position"})
        public boolean useAlternatePosition;
        @Config.Comment({"Whether to disable chat bubbles above playerheads"})
        public boolean disableChatBubble;
        @Config.Comment({"Whether to completely disable the Teams HUD. Use if performance is an issue"})
        public boolean disableTeamsHUD;
    }

    public static class Server {
        @Config.Comment({"Whether to enable PvP for teammates"})
        public boolean enableFriendlyFire;
        @Config.Comment({"Whether to disable achievement sync for teammates"})
        public boolean disableAchievementSync;
        @Config.Comment({"Allows everyone to use /team remove"})
        public boolean noOpRemoveTeam;
        @Config.Comment({"Prevents usage of the inventor transfer feature"})
        public boolean disableInventoryTransfer;
        @Config.Comment({"Make prefixes handled serverside"})
        public boolean prefixServerSide;
        @Config.Comment({"Disable prefixes. Only works if prefixServerSide = true"})
        public boolean disablePrefix;
    }
}
