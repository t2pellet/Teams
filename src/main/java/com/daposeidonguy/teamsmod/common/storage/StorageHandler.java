package com.daposeidonguy.teamsmod.common.storage;

import com.daposeidonguy.teamsmod.client.ClientHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

public class StorageHandler {

    static final Map<UUID, String> uuidToTeamMap = new HashMap<>(); // UUID to storage Name
    static final Map<String, List<UUID>> teamToUuidsMap = new HashMap<>(); //Team name to list of UUIDs
    static final Map<String, Map<String, Boolean>> teamSettingsMap = new HashMap<>(); //Team name to map of settings
    static final Map<String, UUID> teamToOwnerMap = new HashMap<>();


    /* Syncs advancements of all players in a team */
    public static void syncAdvancements(final String team, final ServerPlayerEntity player) {
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

    public static void readFromNBT(final CompoundNBT nbt) {
        clearData();
        try {
            for (INBT inbt : nbt.getList("Teams", Constants.NBT.TAG_COMPOUND)) {
                CompoundNBT teamTag = (CompoundNBT) inbt;
                String teamName = teamTag.getString("Team Name");
                ListNBT playersTag = teamTag.getList("Player List", Constants.NBT.TAG_COMPOUND);
                if (playersTag.size() == 0) {
                    continue;
                }
                readPlayers(teamTag, teamName);
                CompoundNBT tagPlayer = (CompoundNBT) playersTag.get(0);
                teamToOwnerMap.put(teamName, tagPlayer.getUniqueId("uuid"));
                readSettings(teamTag, teamName);
            }
        } catch (Exception doNothing) {
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
            UUID playerId = UUID.fromString(playerTag.getString("uuid"));
            addPlayerMapping(playerId);
            uuidToTeamMap.put(playerId, teamName);
            uuidList.add(playerId);
        }
        teamToUuidsMap.put(teamName, uuidList);
    }

    private static void addPlayerMapping(final UUID playerId) {
        if (EffectiveSide.get().isClient()) {
            NetworkPlayerInfo playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(playerId);
            if (playerInfo != null) {
                String playerName = playerInfo.getGameProfile().getName();
                ClientHelper.addPlayerMapping(playerName, playerId);
            }
        }
    }

    public static CompoundNBT writeToNBT(final CompoundNBT compound) {
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
