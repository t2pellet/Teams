package com.daposeidonguy.teamsmod.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHolder {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec SERVER_SPEC;
    static final TeamConfig.ClientConfig CLIENT_CONFIG;
    static final TeamConfig.ServerConfig SERVER_CONFIG;

    static {
        {
            final Pair<TeamConfig.ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(TeamConfig.ClientConfig::new);
            CLIENT_CONFIG = specPair.getLeft();
            CLIENT_SPEC = specPair.getRight();
        }
        {
            final Pair<TeamConfig.ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(TeamConfig.ServerConfig::new);
            SERVER_CONFIG = specPair.getLeft();
            SERVER_SPEC = specPair.getRight();
        }
    }
}
