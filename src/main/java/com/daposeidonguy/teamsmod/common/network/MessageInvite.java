package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.client.gui.toasts.ToastInvite;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageInvite {

    private String teamName;

    public MessageInvite() {
    }

    public MessageInvite(String teamName) {
        this.teamName = teamName;
    }

    public MessageInvite(PacketBuffer buf) {
        teamName = buf.readString();
    }

    public void encode(PacketBuffer buf) {
        buf.writeString(teamName);
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            displayToast();
        }));
    }

    @OnlyIn(Dist.CLIENT)
    public void displayToast() {
        Minecraft.getInstance().getToastGui().add(new ToastInvite(teamName));
    }
}
