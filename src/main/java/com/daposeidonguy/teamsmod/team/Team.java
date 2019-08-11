package com.daposeidonguy.teamsmod.team;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Team {
    private String name;
    private List<UUID> players;
    public Team(String name, List<UUID> uuids) {
        this.name = name;
        players = new ArrayList();
        for(UUID id : uuids) {
            players.add(id);
        }
    }
    public String getName() {
        return name;
    }
    public static void syncPlayers(Team team, EntityPlayerMP player) {
        for(Advancement adv : player.getServerWorld().getAdvancementManager().getAdvancements()) {
            for(UUID id : team.getPlayers()) {
                EntityPlayerMP teammate = (EntityPlayerMP)player.getEntityWorld().getPlayerEntityByUUID(id);
                if(teammate != null && teammate.getAdvancements().getProgress(adv).isDone()) {
                    for (String s : teammate.getAdvancements().getProgress(adv).getCompletedCriteria()) {
                        player.getAdvancements().grantCriterion(adv,s);
                    }
                }
            }
            if(player!=null && player.getAdvancements().getProgress(adv).isDone()) {
                for (String s : player.getAdvancements().getProgress(adv).getCompletedCriteria()) {
                    player.getAdvancements().grantCriterion(adv,s);
                }

            }
        }
    }
    public List<UUID> getPlayers() {
        return players;
    }
    public void addPlayer(UUID player) {
        this.players.add(player);
    }
    public void removePlayer(UUID player) {
        this.players.remove(player);
    }
    public static Team getTeam(UUID playerUID) {
        Iterator<Team> teamIterator = SaveData.listTeams.iterator();
        while(teamIterator.hasNext()) {
            Team team = teamIterator.next();
            if (team.getPlayers().contains(playerUID)) {
                return team;
            }
        }
        return null;
    }
    public NBTTagList getPlayerListTag() {
        NBTTagList nbtTagList = new NBTTagList();
        Iterator<UUID> uuidIterator = this.getPlayers().iterator();
        while(uuidIterator.hasNext()) {
            UUID uuid = uuidIterator.next();
            NBTTagCompound tagCompound = new NBTTagCompound();
            tagCompound.setString("uuid",uuid.toString());
            nbtTagList.appendTag(tagCompound);
        }
        return nbtTagList;
    }
}
