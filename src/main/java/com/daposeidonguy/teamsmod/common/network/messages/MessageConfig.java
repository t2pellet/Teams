package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.common.config.ConfigHandler;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageConfig extends AbstractMessage {

    public MessageConfig(PacketBuffer buf) {
        super(buf);
    }

    public MessageConfig() {
        tag.putBoolean("disablePing", TeamConfig.disablePing);
        tag.putBoolean("disableTransfer", TeamConfig.disableInventoryTransfer);
        tag.putBoolean("disableDeathSound", TeamConfig.disableDeathSound);
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            ConfigHandler.serverDisablePing = tag.getBoolean("disablePing");
            ConfigHandler.serverDisableTransfer = tag.getBoolean("disableTransfer");
            ConfigHandler.serverDisableDeathSound = tag.getBoolean("disableDeathSound");
        }));
        ctx.get().setPacketHandled(true);
    }
}
