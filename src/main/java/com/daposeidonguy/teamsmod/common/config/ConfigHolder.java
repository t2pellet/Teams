package com.daposeidonguy.teamsmod.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHolder {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec COMMON_SPEC;
    static final TeamConfig.ClientConfig CLIENT_CONFIG;
    static final TeamConfig.CommonConfig COMMON_CONFIG;

    static {
        {
            final Pair<TeamConfig.ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(TeamConfig.ClientConfig::new);
            CLIENT_CONFIG = specPair.getLeft();
            CLIENT_SPEC = specPair.getRight();
        }
        {
            final Pair<TeamConfig.CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(TeamConfig.CommonConfig::new);
            COMMON_CONFIG = specPair.getLeft();
            COMMON_SPEC = specPair.getRight();
        }
    }
}
