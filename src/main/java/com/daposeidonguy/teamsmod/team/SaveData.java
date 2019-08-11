package com.daposeidonguy.teamsmod.team;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class SaveData extends WorldSavedData {

    public static List<Team> listTeams = new ArrayList<>();
    public static final String NAME = TeamsMod.MODID;


    public SaveData() {
        super(NAME);
    }

    public SaveData(String s) {
        super(s);
        markDirty();
    }

    public void addTeam(String name, EntityPlayer player) {
        List<UUID> tempList = new ArrayList<>();
        tempList.add(player.getUniqueID());
        Team team = new Team(name,tempList);
        listTeams.add(team);
        markDirty();
    }

    public void addPlayer(EntityPlayer p, UUID uid) {
        Team team = Team.getTeam(p.getUniqueID());
        team.addPlayer(uid);
        markDirty();
    }

    public void removePlayer(EntityPlayer p,UUID uid) {
        Team team = Team.getTeam(p.getUniqueID());
        team.removePlayer(uid);
        markDirty();
    }

    public void removeTeam(String name) {
        Iterator<Team> teamIterator = listTeams.iterator();
        while(teamIterator.hasNext()) {
            Team team = teamIterator.next();
            if(team.getName().equals(name)) {
                listTeams.remove(team);
                break;
            }
        }
    }

    public void removeTeam(UUID player) {
        Iterator<Team> teamIterator = listTeams.iterator();
        while(teamIterator.hasNext()) {
            Team team = teamIterator.next();
            if(team.getPlayers().contains(player)) {
                listTeams.remove(team);
                break;
            }
        }
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        listTeams.clear();
        NBTTagList tagList = nbt.getTagList("Teams", Constants.NBT.TAG_COMPOUND);
        for (NBTBase b : tagList) {
            NBTTagCompound tagCompound = (NBTTagCompound) b;
            NBTTagList playerTagList = tagCompound.getTagList("Player List",Constants.NBT.TAG_COMPOUND);
            List<UUID> uuidList = new ArrayList();
            for (NBTBase p : playerTagList) {
                NBTTagCompound playerTag = (NBTTagCompound) p;
                UUID id = UUID.fromString(playerTag.getString("uuid"));
                uuidList.add(id);
            }
            Team team = new Team(tagCompound.getString("Team Name"),uuidList);
            listTeams.add(team);

        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList tagList = new NBTTagList();

        Iterator<Team> iteratorTeams = listTeams.iterator();
        while(iteratorTeams.hasNext()) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            Team team = iteratorTeams.next();
            tagCompound.setString("Team Name",team.getName());
            tagCompound.setTag("Player List",team.getPlayerListTag());
            tagList.appendTag(tagCompound);
        }
        compound.setTag("Teams",tagList);
        return compound;
    }

    public static SaveData get(World world) {
        MapStorage storage = world.getMapStorage();
        SaveData data = (SaveData)storage.getOrLoadData(SaveData.class,NAME);
        if (data==null) {
            data = new SaveData();
            world.setData(NAME,data);
        }
        return data;
    }
}
