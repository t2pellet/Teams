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

public class SaveData extends WorldSavedData {

    public static final String NAME = TeamsMod.MODID;
    public static Map<UUID, String> teamMap = new HashMap<>(); // UUID to storage Name
    public static Map<String, List<UUID>> teamsMap = new HashMap<>(); //Team name to list of UUIDs


    public SaveData() {
        super(NAME);
    }

    public SaveData(String name) {
        super(name);
        markDirty();
    }

    public static SaveData get(ServerWorld world) {
        DimensionSavedDataManager storage = world.getSavedData();
        SaveData data = storage.getOrCreate(() -> new SaveData(), NAME);
        if (data == null) {
            data = new SaveData();
            storage.set(data);
        }
        return data;
    }

    /* Syncs advancements of all players in a command */
    public static void syncPlayers(String team, ServerPlayerEntity player) {
        if (EffectiveSide.get().isServer() && player != null) {
            for (Advancement adv : ServerLifecycleHooks.getCurrentServer().getAdvancementManager().getAllAdvancements()) {
                Iterator<UUID> uuidIterator = teamsMap.get(team).iterator();
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

    /* Adds a command */
    public void addTeam(String name, PlayerEntity player) {
        List<UUID> tempList = new ArrayList<>();
        tempList.add(player.getUniqueID());
        teamsMap.put(name, tempList);
        teamMap.put(player.getUniqueID(), name);
        markDirty();
    }

    /* Adds a player to a command */
    public void addPlayer(PlayerEntity p, UUID uid) {
        String name = teamMap.get(p.getUniqueID());
        teamsMap.get(name).add(uid);
        teamMap.put(uid, name);
        markDirty();
    }

    /* Removes a player from a command */
    public void removePlayer(PlayerEntity p, UUID uid) {
        String name = teamMap.get(p.getUniqueID());
        teamsMap.get(name).remove(uid);
        teamMap.remove(uid);
        markDirty();
    }

    /* Removes a command*/
    public void removeTeam(String name) {
        Iterator<UUID> uuidIterator = teamsMap.get(name).iterator();
        while (uuidIterator.hasNext()) {
            UUID id = uuidIterator.next();
            teamMap.remove(id);
        }
        teamsMap.remove(name);
        markDirty();
    }

    @Override
    public void read(CompoundNBT nbt) {
        teamsMap.clear();
        teamMap.clear();
        String name = "";
        Iterator<INBT> tagList = nbt.getList("Teams", Constants.NBT.TAG_COMPOUND).iterator();
        while (tagList.hasNext()) {
            CompoundNBT tagCompound = (CompoundNBT) tagList.next();
            Iterator<INBT> playerTagListIterator = tagCompound.getList("Player List", Constants.NBT.TAG_COMPOUND).iterator();
            List<UUID> uuidList = new ArrayList();
            while (playerTagListIterator.hasNext()) {
                CompoundNBT playerTag = (CompoundNBT) playerTagListIterator.next();
                UUID id = UUID.fromString(playerTag.getString("uuid"));
                name = tagCompound.getString("Team Name");
                teamMap.put(id, name);
                uuidList.add(id);
            }
            teamsMap.put(name, uuidList);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT tagList = new ListNBT();
        Iterator<String> iteratorTeams = teamsMap.keySet().iterator();
        while (iteratorTeams.hasNext()) {
            CompoundNBT tagCompound = new CompoundNBT();
            String team = iteratorTeams.next();
            tagCompound.putString("Team Name", team);


            ListNBT playerListTag = new ListNBT();
            Iterator<UUID> uuidIterator = teamsMap.get(team).iterator();
            while (uuidIterator.hasNext()) {
                UUID id = uuidIterator.next();
                CompoundNBT playerTag = new CompoundNBT();
                playerTag.putString("uuid", id.toString());
                playerListTag.add(playerTag);
            }
            tagCompound.put("Player List", playerListTag);
            tagList.add(tagCompound);
        }
        compound.put("Teams", tagList);
        return compound;
    }
}
