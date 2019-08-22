package com.daposeidonguy.teamsmod.team;

import com.daposeidonguy.teamsmod.TeamsMod;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.*;

public class SaveData extends WorldSavedData {

    public static Map<UUID,String> teamMap = new HashMap<>();
    public static Map<String,List<UUID>> teamsMap = new HashMap<>();
    public static final String NAME = TeamsMod.MODID;


    public SaveData() {
        super(NAME);
    }

    public SaveData(String name) {
        super(name);
        markDirty();
    }

    public void addTeam(String name, EntityPlayer player) {
        List<UUID> tempList = new ArrayList<>();
        tempList.add(player.getUniqueID());
        teamsMap.put(name,tempList);
        teamMap.put(player.getUniqueID(),name);
        markDirty();
    }

    public void addPlayer(EntityPlayer p, UUID uid) {
        String name = teamMap.get(p.getUniqueID());
        teamsMap.get(name).add(uid);
        teamMap.put(uid,name);
        markDirty();
    }

    public void removePlayer(EntityPlayer p,UUID uid) {
        String name = teamMap.get(p.getUniqueID());
        teamsMap.get(name).remove(uid);
        teamMap.remove(uid);
        markDirty();
    }

    public void removeTeam(String name) {
        Iterator<UUID> uuidIterator = teamsMap.get(name).iterator();
        while(uuidIterator.hasNext()) {
            UUID id = uuidIterator.next();
            teamMap.remove(id);
        }
        teamsMap.remove(name);
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        teamsMap.clear();
        teamMap.clear();
        String name = "";
        Iterator<NBTBase> tagList = nbt.getTagList("Teams", Constants.NBT.TAG_COMPOUND).iterator();
        while(tagList.hasNext()) {
            NBTTagCompound tagCompound = (NBTTagCompound)tagList.next();
            Iterator<NBTBase> playerTagListIterator = tagCompound.getTagList("Player List",Constants.NBT.TAG_COMPOUND).iterator();
            List<UUID> uuidList = new ArrayList();
            while(playerTagListIterator.hasNext()) {
                NBTTagCompound playerTag = (NBTTagCompound)playerTagListIterator.next();
                UUID id = UUID.fromString(playerTag.getString("uuid"));
                name = tagCompound.getString("Team Name");
                teamMap.put(id,name);
                uuidList.add(id);
            }
            teamsMap.put(name,uuidList);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList tagList = new NBTTagList();
        Iterator<String> iteratorTeams = teamsMap.keySet().iterator();
        while(iteratorTeams.hasNext()) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            String team = iteratorTeams.next();
            tagCompound.setString("Team Name",team);


            NBTTagList playerListTag = new NBTTagList();
            Iterator<UUID> uuidIterator = teamsMap.get(team).iterator();
            while(uuidIterator.hasNext()) {
                UUID id = uuidIterator.next();
                NBTTagCompound playerTag = new NBTTagCompound();
                playerTag.setString("uuid",id.toString());
                playerListTag.appendTag(playerTag);
            }
            tagCompound.setTag("Player List",playerListTag);
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

    public static void syncPlayers(String team, EntityPlayerMP player) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().isRemote && player!=null) {
            for (Advancement adv : FMLCommonHandler.instance().getMinecraftServerInstance().getAdvancementManager().getAdvancements()) {
                Iterator<UUID> uuidIterator = teamsMap.get(team).iterator();
                while (uuidIterator.hasNext()) {
                    UUID id = uuidIterator.next();
                    EntityPlayerMP teammate = (EntityPlayerMP) FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getPlayerEntityByUUID(id);
                    if (teammate != null) {
                        if(teammate.getAdvancements().getProgress(adv).isDone()) {
                            for (String s : teammate.getAdvancements().getProgress(adv).getCompletedCriteria()) {
                                player.getAdvancements().grantCriterion(adv, s);
                            }
                        } else if (player.getAdvancements().getProgress(adv).isDone()) {
                            for (String s : player.getAdvancements().getProgress(adv).getCompletedCriteria()) {
                                teammate.getAdvancements().grantCriterion(adv,s);
                            }
                        }
                    }
                }
            }
        }
    }
}
