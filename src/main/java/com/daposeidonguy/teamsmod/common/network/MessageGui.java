package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.inventory.InterfaceTransfer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;
import java.util.function.Supplier;

public class MessageGui {

    private CompoundNBT tag = new CompoundNBT();

    public MessageGui() {
    }

    public MessageGui(UUID id, String name) {
        tag.putString("id", id.toString());
        tag.putString("name", name);
    }

    public MessageGui(PacketBuffer buf) {
        tag = buf.readCompoundTag();
    }

    public void encode(PacketBuffer buf) {
        buf.writeCompoundTag(tag);
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (EffectiveSide.get().isServer() && !TeamConfig.disableInventoryTransfer) {
                if (tag.getString("id") != null && tag.getString("name") != null) {
                    PlayerEntity p = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(UUID.fromString(tag.getString("id")));
                    if (p != null) {
                        TeamsMod.logger.debug(new StringTextComponent("Received MessageGui"));
                        NetworkHooks.openGui((ServerPlayerEntity) p, new InterfaceTransfer(tag.getString("name")));
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
