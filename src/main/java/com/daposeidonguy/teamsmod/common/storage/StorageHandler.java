package com.daposeidonguy.teamsmod.common.storage;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

public class StorageHandler {

    public static final Map<UUID, String> uuidToTeamMap = new HashMap<>(); // UUID to storage Name
    public static final Map<String, List<UUID>> teamToUuidsMap = new HashMap<>(); //Team name to list of UUIDs
    public static final Map<String, Map<String, Boolean>> teamSettingsMap = new HashMap<>(); //Team name to map of settings
    public static final Map<String, UUID> teamToOwnerMap = new HashMap<>();


    /* Syncs advancements of all players in a team */
    public static void syncPlayers(String team, ServerPlayerEntity player) {
        if (EffectiveSide.get().isServer() && player != null) {
            for (Advancement adv : ServerLifecycleHooks.getCurrentServer().getAdvancementManager().getAllAdvancements()) {
                for (UUID id : teamToUuidsMap.get(team)) {
                    ServerPlayerEntity teammate = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(id);
                    if (teammate != null) {
                        if (teammate.getAdvancements().getProgress(adv).isDone()) {
                            for (String s : teammate.getAdvancements().getProgress(adv).getCompletedCriteria()) {
                                player.getAdvancements().grantCriterion(adv, s);
                            }
                        } else if (player.getAdvancements().getProgress(adv).isDone()) {
                            for (String s : player.getAdvancements().getProgress(adv).getCompletedCriteria()) {
                                teammate.getAdvancements().grantCriterion(adv, s);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void readFromNBT(CompoundNBT nbt) {
        clearData();
        for (INBT inbt : nbt.getList("Teams", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT teamTag = (CompoundNBT) inbt;
            String teamName = teamTag.getString("Team Name");
            readPlayers(teamTag, teamName);
            if (teamTag.hasUniqueId("Owner")) { // Just for converting old worlds
                teamToOwnerMap.put(teamName, teamTag.getUniqueId("Owner"));
            } else {
                teamToOwnerMap.put(teamName, teamToUuidsMap.get(teamName).get(0));
            }
            readSettings(teamTag, teamName);
        }
    }

    /* Clears all loaded savedata */
    private static void clearData() {
        teamToUuidsMap.clear();
        uuidToTeamMap.clear();
        teamSettingsMap.clear();
        teamToOwnerMap.clear();
    }

    /* Reads team settings from NBT */
    private static void readSettings(final CompoundNBT teamTag, final String teamName) {
        CompoundNBT teamSettings = (CompoundNBT) teamTag.get("Settings");
        Map<String, Boolean> settingsMap = new HashMap<>();
        if (teamSettings == null) { // Just for converting old worlds
            settingsMap.put("disableAdvancementSync", false);
            settingsMap.put("enableFriendlyFire", false);
        } else {
            settingsMap.put("disableAdvancementSync", teamSettings.getBoolean("disableAdvancementSync"));
            settingsMap.put("enableFriendlyFire", teamSettings.getBoolean("enableFriendlyFire"));
        }
        teamSettingsMap.put(teamName, settingsMap);
    }

    /* Reads team players from NBT */
    private static void readPlayers(final CompoundNBT teamTag, final String teamName) {
        Iterator<INBT> playerTagListIterator = teamTag.getList("Player List", Constants.NBT.TAG_COMPOUND).iterator();
        List<UUID> uuidList = new ArrayList<>();
        while (playerTagListIterator.hasNext()) {
            CompoundNBT playerTag = (CompoundNBT) playerTagListIterator.next();
            UUID id = UUID.fromString(playerTag.getString("uuid"));
            uuidToTeamMap.put(id, teamName);
            uuidList.add(id);
        }
        teamToUuidsMap.put(teamName, uuidList);
    }

    public static CompoundNBT writeToNBT(CompoundNBT compound) {
        ListNBT tagList = new ListNBT();
        for (String teamName : teamToUuidsMap.keySet()) {
            CompoundNBT teamTag = new CompoundNBT();
            teamTag.putString("Team Name", teamName);
            teamTag.putUniqueId("Owner", teamToOwnerMap.get(teamName));
            teamTag.put("Player List", writePlayers(teamName));
            teamTag.put("Settings", writeSettings(teamName));
            tagList.add(teamTag);
        }
        compound.put("Teams", tagList);
        return compound;
    }

    /* Writes saved team players to NBT */
    private static ListNBT writePlayers(final String teamName) {
        ListNBT playerListTag = new ListNBT();
        for (UUID id : teamToUuidsMap.get(teamName)) {
            CompoundNBT playerTag = new CompoundNBT();
            playerTag.putString("uuid", id.toString());
            playerListTag.add(playerTag);
        }
        return playerListTag;
    }

    /* Writes saved team settings to NBT */
    private static CompoundNBT writeSettings(final String teamName) {
        CompoundNBT teamSettings = new CompoundNBT();
        teamSettings.putBoolean("disableAdvancementSync", teamSettingsMap.get(teamName).get("disableAdvancementSync"));
        teamSettings.putBoolean("enableFriendlyFire", teamSettingsMap.get(teamName).get("enableFriendlyFire"));
        return teamSettings;
    }

}
