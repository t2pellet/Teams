package com.daposeidonguy.teamsmod.common.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("teamsmod", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void register() {
        int id = 0;
        INSTANCE.registerMessage(id++, MessageDeath.class, MessageDeath::encode, MessageDeath::new, MessageDeath::onMessage);
        INSTANCE.registerMessage(id++, MessageGui.class, MessageGui::encode, MessageGui::new, MessageGui::onMessage);
        INSTANCE.registerMessage(id++, MessageHealth.class, MessageHealth::encode, MessageHealth::new, MessageHealth::onMessage);
        INSTANCE.registerMessage(id++, MessageHunger.class, MessageHunger::encode, MessageHunger::new, MessageHunger::onMessage);
        INSTANCE.registerMessage(id++, MessageSaveData.class, MessageSaveData::encode, MessageSaveData::new, MessageSaveData::onMessage);
        INSTANCE.registerMessage(id, MessageInvite.class, MessageInvite::encode, MessageInvite::new, MessageInvite::onMessage);
    }

}
