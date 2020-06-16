package com.daposeidonguy.teamsmod.client.chat;

import com.mojang.datafixers.util.Pair;

import java.util.UUID;

public class ChatHelper {

    public static UUID getLastMessageSender() {
        if (ChatHandler.lastMessageReceived == null) return null;
        return ChatHandler.lastMessageReceived.getFirst();
    }

    public static String getLastMessage() {
        return ChatHandler.lastMessageReceived.getSecond();
    }

    public static boolean wasLastMessageTeam() {
        return ChatHandler.lastMessageTeam;
    }

    public static void setLastMessage(UUID playerId, String message) {
        ChatHandler.lastMessageReceived = new Pair<>(playerId, message);
    }

    public static void setLastMessageTeam(boolean wasTeam) {
        ChatHandler.lastMessageTeam = wasTeam;
    }

    public static void clear() {
        ChatHandler.lastMessageTeam = false;
        ChatHandler.lastMessageReceived = null;
    }

}
