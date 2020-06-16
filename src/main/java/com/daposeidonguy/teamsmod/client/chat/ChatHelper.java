package com.daposeidonguy.teamsmod.client.chat;

import com.mojang.realmsclient.util.Pair;

import java.util.UUID;

public class ChatHelper {

    public static UUID getLastMessageSender() {
        if (ChatHandler.lastMessageReceived == null) return null;
        return ChatHandler.lastMessageReceived.first();
    }

    public static String getLastMessage() {
        return ChatHandler.lastMessageReceived.second();
    }

    public static boolean wasLastMessageTeam() {
        return ChatHandler.lastMessageTeam;
    }

    public static void setLastMessage(UUID playerId, String message) {
        ChatHandler.lastMessageReceived = Pair.of(playerId, message);
    }

    public static void setLastMessageTeam(boolean wasTeam) {
        ChatHandler.lastMessageTeam = wasTeam;
    }

    public static void clear() {
        ChatHandler.lastMessageReceived = null;
        ChatHandler.lastMessageTeam = false;
    }

}
