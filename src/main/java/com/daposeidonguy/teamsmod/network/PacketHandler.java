package com.daposeidonguy.teamsmod.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {


    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("teamsmod");

    public static void registerMessages(Side side) {
        PacketHandler.INSTANCE.registerMessage(MessageSaveData.MessageHandler.class, MessageSaveData.class, 1, side);
        PacketHandler.INSTANCE.registerMessage(MessageInvite.MessageHandler.class, MessageInvite.class, 2, side);
        PacketHandler.INSTANCE.registerMessage(MessageHunger.MessageHandler.class, MessageHunger.class, 3, side);
        PacketHandler.INSTANCE.registerMessage(MessageGui.MessageHandler.class, MessageGui.class, 4, side);
        PacketHandler.INSTANCE.registerMessage(MessageHealth.MessageHandler.class, MessageHealth.class, 5, side);
        PacketHandler.INSTANCE.registerMessage(MessageDeath.MessageHandler.class, MessageDeath.class, 6, side);
    }

}
