package com.daposeidonguy.teamsmod.common.storage;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

public class StorageHandler extends WorldSavedData {

    public static Map<UUID, String> uuidToTeamMap = new HashMap<>(); // UUID to storage Name
    public static Map<String, List<UUID>> teamToUuidsMap = new HashMap<>(); //Team name to list of UUIDs
    public static Map<String, Map<String, Boolean>> teamSettingsMap = new HashMap<>(); //Team name to map of settings

    public StorageHandler() {
        super(TeamsMod.MODID);
    }

    public StorageHandler(String name) {
        super(name);
        markDirty();
    }

    public static StorageHandler get(ServerWorld world) {
        DimensionSavedDataManager storage = world.getSavedData();
        StorageHandler data = storage.getOrCreate(() -> new StorageHandler(), TeamsMod.MODID);
        if (data == null) {
            data = new StorageHandler();
            storage.set(data);
        }
        return data;
    }

    /* Syncs advancements of all players in a team */
    public static void syncPlayers(String team, ServerPlayerEntity player) {
        if (EffectiveSide.get().isServer() && player != null) {
            for (Advancement adv : ServerLifecycleHooks.getCurrentServer().getAdvancementManager().getAllAdvancements()) {
                Iterator<UUID> uuidIterator = teamToUuidsMap.get(team).iterator();
                while (uuidIterator.hasNext()) {
                    UUID id = uuidIterator.next();
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

    /* Adds a team */
    public void addTeam(String name, PlayerEntity player) {
        List<UUID> tempList = new ArrayList<>();
        tempList.add(player.getUniqueID());
        teamToUuidsMap.put(name, tempList);
        uuidToTeamMap.put(player.getUniqueID(), name);
        Map<String, Boolean> newSettingsMap = new HashMap<>();
        newSettingsMap.put("disableAdvancementSync", false);
        newSettingsMap.put("enableFriendlyFire", false);
        teamSettingsMap.put(name, newSettingsMap);
        markDirty();
    }

    /* Adds a player to a team */
    public void addPlayer(PlayerEntity p, UUID uid) {
        String name = uuidToTeamMap.get(p.getUniqueID());
        teamToUuidsMap.get(name).add(uid);
        uuidToTeamMap.put(uid, name);
        markDirty();
    }

    /* Removes a player from a team */
    public void removePlayer(PlayerEntity p, UUID uid) {
        String name = uuidToTeamMap.get(p.getUniqueID());
        teamToUuidsMap.get(name).remove(uid);
        uuidToTeamMap.remove(uid);
        markDirty();
    }

    /* Removes a team*/
    public void removeTeam(String name) {
        Iterator<UUID> uuidIterator = teamToUuidsMap.get(name).iterator();
        while (uuidIterator.hasNext()) {
            UUID id = uuidIterator.next();
            uuidToTeamMap.remove(id);
        }
        teamToUuidsMap.remove(name);
        teamSettingsMap.remove(name);
        markDirty();
    }

    @Override
    public void read(CompoundNBT nbt) {
        teamToUuidsMap.clear();
        uuidToTeamMap.clear();
        teamSettingsMap.clear();
        String teamName;
        Iterator<INBT> tagList = nbt.getList("Teams", Constants.NBT.TAG_COMPOUND).iterator();
        while (tagList.hasNext()) {
            CompoundNBT teamTag = (CompoundNBT) tagList.next();
            Iterator<INBT> playerTagListIterator = teamTag.getList("Player List", Constants.NBT.TAG_COMPOUND).iterator();
            List<UUID> uuidList = new ArrayList();
            teamName = teamTag.getString("Team Name");
            while (playerTagListIterator.hasNext()) {
                CompoundNBT playerTag = (CompoundNBT) playerTagListIterator.next();
                UUID id = UUID.fromString(playerTag.getString("uuid"));
                uuidToTeamMap.put(id, teamName);
                uuidList.add(id);
            }
            teamToUuidsMap.put(teamName, uuidList);
            CompoundNBT teamSettings = (CompoundNBT) teamTag.get("Settings");
            Map<String, Boolean> settingsMap = new HashMap<>();
            if (teamSettings == null) {
                settingsMap.put("disableAdvancementSync", false);
                settingsMap.put("enableFriendlyFire", false);
            } else {
                settingsMap.put("disableAdvancementSync", teamSettings.getBoolean("disableAdvancementSync"));
                settingsMap.put("enableFriendlyFire", teamSettings.getBoolean("enableFriendlyFire"));
            }
            teamSettingsMap.put(teamName, settingsMap);

        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT tagList = new ListNBT();
        Iterator<String> iteratorTeams = teamToUuidsMap.keySet().iterator();
        while (iteratorTeams.hasNext()) {
            CompoundNBT teamTag = new CompoundNBT();
            String team = iteratorTeams.next();
            teamTag.putString("Team Name", team);
            ListNBT playerListTag = new ListNBT();
            Iterator<UUID> uuidIterator = teamToUuidsMap.get(team).iterator();
            while (uuidIterator.hasNext()) {
                UUID id = uuidIterator.next();
                CompoundNBT playerTag = new CompoundNBT();
                playerTag.putString("uuid", id.toString());
                playerListTag.add(playerTag);
            }
            teamTag.put("Player List", playerListTag);
            CompoundNBT teamSettings = new CompoundNBT();
            teamSettings.putBoolean("disableAdvancementSync", teamSettingsMap.get(team).get("disableAdvancementSync"));
            teamSettings.putBoolean("enableFriendlyFire", teamSettingsMap.get(team).get("enableFriendlyFire"));
            teamTag.put("Settings", teamSettings);
            tagList.add(teamTag);
        }
        compound.put("Teams", tagList);
        return compound;
    }
}
