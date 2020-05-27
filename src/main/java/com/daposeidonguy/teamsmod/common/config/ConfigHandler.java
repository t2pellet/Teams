package com.daposeidonguy.teamsmod.common.config;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.network.NetworkHelper;
import com.daposeidonguy.teamsmod.common.network.messages.MessageConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigHandler {

    public static boolean serverDisablePing = false;
    public static boolean serverDisableDeathSound = false;
    public static boolean serverDisableTransfer = false;

    @SubscribeEvent
    public static void configEvent(final ModConfig.ModConfigEvent event) {
        if (event.getConfig().getSpec() == ConfigHolder.CLIENT_SPEC) {
            ConfigHandler.bakeClient();
        } else if (event.getConfig().getSpec() == ConfigHolder.COMMON_SPEC) {
            ConfigHandler.bakeCommon();
            if (TeamsMod.doneSetup) {
                DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
                    NetworkHelper.sendToAll(new MessageConfig());
                });
            }
        }
    }

    /* Updates config to player on login */
    @SubscribeEvent
    public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (EffectiveSide.get().isServer()) {
            NetworkHelper.sendToPlayer((ServerPlayerEntity) event.getPlayer(), new MessageConfig());
        }
    }

    /* Retrieves values from the client config */
    private static void bakeClient() {
        TeamConfig.smallIcon = ConfigHolder.CLIENT_CONFIG.smallIcon.get();
        TeamConfig.useAlternatePosition = ConfigHolder.CLIENT_CONFIG.useAlternatePosition.get();
        TeamConfig.disableStatusOverlay = ConfigHolder.CLIENT_CONFIG.disableStatusOverlay.get();
        TeamConfig.disableCompassOverlay = ConfigHolder.CLIENT_CONFIG.disableCompassOverlay.get();
        TeamConfig.disableChatBubble = ConfigHolder.CLIENT_CONFIG.disableChatBubble.get();
    }

    /* Retrieves values from the common config */
    private static void bakeCommon() {
        /* Common */
        TeamConfig.disablePing = ConfigHolder.COMMON_CONFIG.disablePing.get();
        TeamConfig.disablePrefix = ConfigHolder.COMMON_CONFIG.disablePrefix.get();
        TeamConfig.disableDeathSound = ConfigHolder.COMMON_CONFIG.disableDeathSound.get();
        /* Server */
        TeamConfig.enableFriendlyFire = ConfigHolder.COMMON_CONFIG.enableFriendlyFire.get();
        TeamConfig.disableAdvancementSync = ConfigHolder.COMMON_CONFIG.disableAdvancementSync.get();
        TeamConfig.noOpRemoveTeam = ConfigHolder.COMMON_CONFIG.noOpRemoveTeam.get();
        TeamConfig.disableInventoryTransfer = ConfigHolder.COMMON_CONFIG.disableInventoryTransfer.get();
    }
}
