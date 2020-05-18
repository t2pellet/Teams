package com.daposeidonguy.teamsmod.common.storage;

import com.daposeidonguy.teamsmod.client.ClientHandler;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.*;

public class StorageHandler {

    public static final Map<UUID, String> uuidToTeamMap = new HashMap<>(); // UUID to storage Name
    public static final Map<String, List<UUID>> teamToUuidsMap = new HashMap<>(); //Team name to list of UUIDs
    public static final Map<String, Map<String, Boolean>> teamSettingsMap = new HashMap<>(); //Team name to map of settings
    public static final Map<String, UUID> teamToOwnerMap = new HashMap<>();


    /* Syncs advancements of all players in a team */
    public static void syncPlayers(final String team, final EntityPlayerMP player) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer() && player != null) {
            Iterable<Advancement> advancements = FMLCommonHandler.instance().getMinecraftServerInstance().getAdvancementManager().getAdvancements();
            for (Advancement adv : advancements) {
                for (UUID id : teamToUuidsMap.get(team)) {
                    EntityPlayerMP teammate = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(id);
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

    public static void readFromNBT(final NBTTagCompound nbt) {
        clearData();
        try {
            for (NBTBase nbtBase : nbt.getTagList("Teams", Constants.NBT.TAG_COMPOUND)) {
                NBTTagCompound teamTag = (NBTTagCompound) nbtBase;
                String teamName = teamTag.getString("Team Name");
                NBTTagList playersTag = teamTag.getTagList("Player List", Constants.NBT.TAG_COMPOUND);
                if (playersTag.tagCount() == 0) {
                    continue;
                }
                readPlayers(teamTag, teamName);
                NBTTagCompound tagPlayer = (NBTTagCompound) playersTag.get(0);
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
    private static void readSettings(final NBTTagCompound teamTag, final String teamName) {
        NBTTagCompound teamSettings = (NBTTagCompound) teamTag.getTag("Settings");
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
    private static void readPlayers(final NBTTagCompound teamTag, final String teamName) {
        Iterator<NBTBase> playerTagListIterator = teamTag.getTagList("Player List", Constants.NBT.TAG_COMPOUND).iterator();
        List<UUID> uuidList = new ArrayList<>();
        while (playerTagListIterator.hasNext()) {
            NBTTagCompound playerTag = (NBTTagCompound) playerTagListIterator.next();
            UUID playerId = UUID.fromString(playerTag.getString("uuid"));
            addPlayerMapping(playerId);
            uuidToTeamMap.put(playerId, teamName);
            uuidList.add(playerId);
        }
        teamToUuidsMap.put(teamName, uuidList);
    }

    private static void addPlayerMapping(final UUID playerId) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            try {
                String name = Minecraft.getMinecraft().getConnection().getPlayerInfo(playerId).getGameProfile().getName();
                ClientHandler.nametoIdMap.put(name, playerId);
                ClientHandler.idtoNameMap.put(playerId, name);
            } catch (NullPointerException ignore) {
            }
        }
    }

    public static NBTTagCompound writeToNBT(final NBTTagCompound compound) {
        NBTTagList tagList = new NBTTagList();
        for (String teamName : teamToUuidsMap.keySet()) {
            NBTTagCompound teamTag = new NBTTagCompound();
            teamTag.setString("Team Name", teamName);
            teamTag.setTag("Player List", writePlayers(teamName));
            teamTag.setTag("Settings", writeSettings(teamName));
            tagList.appendTag(teamTag);
        }
        compound.setTag("Teams", tagList);
        return compound;
    }

    /* Writes saved team players to NBT */
    private static NBTTagList writePlayers(final String teamName) {
        NBTTagList playerListTag = new NBTTagList();
        for (UUID id : teamToUuidsMap.get(teamName)) {
            NBTTagCompound playerTag = new NBTTagCompound();
            playerTag.setString("uuid", id.toString());
            playerListTag.appendTag(playerTag);
        }
        return playerListTag;
    }

    /* Writes saved team settings to NBT */
    private static NBTTagCompound writeSettings(final String teamName) {
        NBTTagCompound teamSettings = new NBTTagCompound();
        teamSettings.setBoolean("disableAdvancementSync", teamSettingsMap.get(teamName).get("disableAdvancementSync"));
        teamSettings.setBoolean("enableFriendlyFire", teamSettingsMap.get(teamName).get("enableFriendlyFire"));
        return teamSettings;
    }

}
