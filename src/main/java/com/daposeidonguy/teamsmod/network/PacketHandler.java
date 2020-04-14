package com.daposeidonguy.teamsmod.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {


    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("teamsmod");

    public static void registerMessagesServer() {
        PacketHandler.INSTANCE.registerMessage(MessageSaveData.MessageHandler.class, MessageSaveData.class, 1, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(MessageGui.MessageHandler.class, MessageGui.class, 4, Side.SERVER);
    }

    public static void registerMessagesClient() {
        PacketHandler.INSTANCE.registerMessage(MessageSaveData.MessageHandler.class, MessageSaveData.class, 1, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(MessageInvite.MessageHandler.class, MessageInvite.class, 2, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(MessageHunger.MessageHandler.class, MessageHunger.class, 3, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(MessageHealth.MessageHandler.class, MessageHealth.class, 5, Side.CLIENT);
    }

}
