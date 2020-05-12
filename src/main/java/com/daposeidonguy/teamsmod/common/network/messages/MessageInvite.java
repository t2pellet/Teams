package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.client.gui.toasts.ToastInvite;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/* Sent to player invited to a team */
public class MessageInvite extends AbstractMessage {

    public MessageInvite(final PacketBuffer buf) {
        super(buf);
    }

    public MessageInvite(final String teamName) {
        tag.putString("teamName", teamName);
    }

    public void onMessage(final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> this::displayToast));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void displayToast() {
        Minecraft.getInstance().getToastGui().add(new ToastInvite(tag.getString("teamName")));
    }
}
