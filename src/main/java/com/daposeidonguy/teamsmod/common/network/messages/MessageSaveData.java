package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.client.ClientHandler;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

/* Sent/received to update team save data and configuration data */
public class MessageSaveData extends AbstractMessage {

    public MessageSaveData(PacketBuffer buf) {
        super(buf);
    }

    public MessageSaveData(Map<String, List<UUID>> teamsMap) {
        ListNBT tagList = new ListNBT();
        Iterator<String> iteratorTeams = teamsMap.keySet().iterator();
        while (iteratorTeams.hasNext()) {
            CompoundNBT teamTag = new CompoundNBT();
            String team = iteratorTeams.next();
            teamTag.putString("Team Name", team);
            ListNBT playerListTag = new ListNBT();
            Iterator<UUID> uuidIterator = teamsMap.get(team).iterator();
            while (uuidIterator.hasNext()) {
                UUID id = uuidIterator.next();
                CompoundNBT playerTag = new CompoundNBT();
                playerTag.putString("uuid", id.toString());
                playerListTag.add(playerTag);
            }
            teamTag.put("Player List", playerListTag);
            CompoundNBT teamSettings = new CompoundNBT();
            teamSettings.putBoolean("disableAdvancementSync", StorageHandler.teamSettingsMap.get(team).get("disableAdvancementSync"));
            teamSettings.putBoolean("enableFriendlyFire", StorageHandler.teamSettingsMap.get(team).get("enableFriendlyFire"));
            teamTag.put("Settings", teamSettings);
            tagList.add(teamTag);
        }
        tag.put("Teams", tagList);
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            StorageHandler.teamToUuidsMap.clear();
            StorageHandler.uuidToTeamMap.clear();
            updateTeams(tag.getList("Teams", Constants.NBT.TAG_COMPOUND).iterator());
            updatePriorityPlayers(StorageHandler.uuidToTeamMap.get(Minecraft.getInstance().player.getUniqueID()));
        }));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void updateTeams(Iterator<INBT> tagList) {
        String teamName;
        while (tagList.hasNext()) {
            CompoundNBT teamTag = (CompoundNBT) tagList.next();
            Iterator<INBT> playerTagListIterator = teamTag.getList("Player List", Constants.NBT.TAG_COMPOUND).iterator();
            List<UUID> uuidList = new ArrayList();
            teamName = teamTag.getString("Team Name");
            while (playerTagListIterator.hasNext()) {
                CompoundNBT playerTag = (CompoundNBT) playerTagListIterator.next();
                UUID id = UUID.fromString(playerTag.getString("uuid"));
                NetworkPlayerInfo playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(id);
                if (playerInfo != null) {
                    String playerName = playerInfo.getGameProfile().getName();
                    ClientHandler.idtoNameMap.put(id, playerName);
                    ClientHandler.nametoIdMap.put(playerName, id);
                }
                StorageHandler.uuidToTeamMap.put(id, teamName);
                uuidList.add(id);
            }
            if (!uuidList.isEmpty() && !teamName.equals("")) {
                StorageHandler.teamToUuidsMap.put(teamName, uuidList);
                CompoundNBT teamSettings = (CompoundNBT) teamTag.get("Settings");
                Map<String, Boolean> settingsMap = new HashMap<>();
                settingsMap.put("disableAdvancementSync", teamSettings.getBoolean("disableAdvancementSync"));
                settingsMap.put("enableFriendlyFire", teamSettings.getBoolean("enableFriendlyFire"));
                StorageHandler.teamSettingsMap.put(teamName, settingsMap);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void updatePriorityPlayers(String myTeam) {
        if (myTeam == null) {
            GuiHandler.priorityPlayers.clear();
        } else {
            for (UUID uuid : GuiHandler.priorityPlayers) {
                String theirTeam = StorageHandler.uuidToTeamMap.get(uuid);
                if (theirTeam == null || !theirTeam.equals(myTeam)) {
                    GuiHandler.priorityPlayers.remove(uuid);
                }
            }
        }
    }

}
