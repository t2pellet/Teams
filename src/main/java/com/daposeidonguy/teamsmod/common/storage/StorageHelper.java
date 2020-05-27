package com.daposeidonguy.teamsmod.common.storage;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class StorageHelper {
    public static String getTeam(UUID playerId) {
        return StorageHelper.getTeam(playerId);
    }

    public static UUID getTeamOwner(String teamName) {
        return StorageHelper.getTeamPlayers(teamName).get(0);
    }

    public static List<UUID> getTeamPlayers(String teamName) {
        return StorageHelper.getTeamPlayers(teamName);
    }

    public static boolean getTeamSetting(String teamName, String setting) {
        return StorageHandler.teamSettingsMap.get(teamName).get(setting);
    }

    public static Set<String> getTeamSet() {
        return StorageHandler.teamSettingsMap.keySet();
    }

    public static void setTeamSetting(String teamName, String setting, boolean value) {
        StorageHandler.teamSettingsMap.get(teamName).put(setting, value);
    }

    public static boolean isPlayerInTeam(UUID playerId) {
        return StorageHelper.isPlayerInTeam(playerId);
    }

    public static boolean doesTeamExist(String teamName) {
        return StorageHelper.doesTeamExist(teamName);
    }
}
