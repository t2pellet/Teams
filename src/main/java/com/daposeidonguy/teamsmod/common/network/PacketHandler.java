package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "2";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TeamsMod.MODID, "main"),
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
        INSTANCE.registerMessage(id++, MessageInvite.class, MessageInvite::encode, MessageInvite::new, MessageInvite::onMessage);
        INSTANCE.registerMessage(id, MessageChat.class, MessageChat::encode, MessageChat::new, MessageChat::onMessage);
    }

}
