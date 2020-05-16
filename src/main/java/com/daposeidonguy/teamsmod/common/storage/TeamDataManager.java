package com.daposeidonguy.teamsmod.common.storage;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.*;

import static com.daposeidonguy.teamsmod.common.storage.StorageHandler.*;

public class TeamDataManager extends WorldSavedData {

    private TeamDataManager() {
        super(TeamsMod.MODID);
    }

    public static TeamDataManager get(World world) {
        MapStorage storage = world.getMapStorage();
        TeamDataManager data = (TeamDataManager) storage.getOrLoadData(TeamDataManager.class, TeamsMod.MODID);
        if (data == null) {
            data = new TeamDataManager();
            world.setData(TeamsMod.MODID, data);
        }
        return data;
    }

    @Override
    public void readFromNBT(@Nonnull final NBTTagCompound nbt) {
        StorageHandler.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(@Nonnull final NBTTagCompound compound) {
        return StorageHandler.writeToNBT(compound);
    }

    /* Adds a team */
    public void addTeam(String name, EntityPlayer player) {
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
