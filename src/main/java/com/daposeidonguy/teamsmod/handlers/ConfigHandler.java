package com.daposeidonguy.teamsmod.handlers;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraftforge.common.config.Config;

@Config(modid= TeamsMod.MODID)
public class ConfigHandler {
    @Config.Comment({"Whether or not to have team prefixes enabled. Serverside"})
    public static boolean disablePrefix;
    @Config.Comment({"Whether to enable pinging for players and teams. Clientside"})
    public static boolean disablePing;
    @Config.Comment({"Whether to enable PvP for teammates. Serverside"})
    public static boolean enableFriendlyFire;
    @Config.Comment({"Whether to disable achievement sync for teammates"})
    public static boolean disableAchievementSync;
    @Config.Comment({"Allows everyone to use /team remove"})
    public static boolean noOpRemoveTeam;
}
