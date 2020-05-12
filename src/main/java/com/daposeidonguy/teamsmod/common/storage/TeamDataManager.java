package com.daposeidonguy.teamsmod.common.storage;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.*;

import static com.daposeidonguy.teamsmod.common.storage.StorageHandler.*;

public class TeamDataManager extends WorldSavedData {

    private TeamDataManager() {
        super(TeamsMod.MODID);
    }

    private TeamDataManager(String name) {
        super(name);
        markDirty();
    }

    public static TeamDataManager get(final ServerWorld world) {
        DimensionSavedDataManager storage = world.getSavedData();
        return storage.getOrCreate(TeamDataManager::new, TeamsMod.MODID);
    }

    @Override
    public void read(@Nonnull final CompoundNBT nbt) {
        readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public CompoundNBT write(@Nonnull final CompoundNBT compound) {
        return writeToNBT(compound);
    }

    /* Adds a team */
    public void addTeam(String name, PlayerEntity player) {
        List<UUID> tempList = new ArrayList<>();
        tempList.add(player.getUniqueID());
        teamToUuidsMap.put(name, tempList);
        teamToOwnerMap.put(name, player.getUniqueID());
        uuidToTeamMap.put(player.getUniqueID(), name);
        Map<String, Boolean> newSettingsMap = new HashMap<>();
        newSettingsMap.put("disableAdvancementSync", false);
        newSettingsMap.put("enableFriendlyFire", false);
        teamSettingsMap.put(name, newSettingsMap);
        markDirty();
    }

    /* Adds a player to a team */
    public void addPlayer(final String team, final UUID uid) {
        teamToUuidsMap.get(team).add(uid);
        uuidToTeamMap.put(uid, team);
        markDirty();
    }

    /* Removes a player from a team */
    public void removePlayer(final String team, final UUID uid) {
        teamToUuidsMap.get(team).remove(uid);
        uuidToTeamMap.remove(uid);
        markDirty();
    }


    /* Removes a team*/
    public void removeTeam(final String name) {
        for (UUID id : teamToUuidsMap.get(name)) {
            uuidToTeamMap.remove(id);
        }
        teamToUuidsMap.remove(name);
        teamSettingsMap.remove(name);
        teamToOwnerMap.remove(name);
        markDirty();
    }
}
