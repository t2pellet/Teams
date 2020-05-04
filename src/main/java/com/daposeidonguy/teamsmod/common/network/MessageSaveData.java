package com.daposeidonguy.teamsmod.common.network;

import com.daposeidonguy.teamsmod.client.ClientEventHandler;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

public class MessageSaveData {

    private CompoundNBT tagTeam = new CompoundNBT();

    public MessageSaveData() {

    }

    public MessageSaveData(Map<String, List<UUID>> teamsMap) {

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
        tagTeam.put("Teams", tagList);
    }

    public MessageSaveData(PacketBuffer buf) {
        tagTeam = buf.readCompoundTag();
    }

    public void encode(PacketBuffer buf) {
        buf.writeCompoundTag(tagTeam);
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            SaveData.teamsMap.clear();
            SaveData.teamMap.clear();
            String teamName = "";
            Iterator<INBT> tagList = tagTeam.getList("Teams", Constants.NBT.TAG_COMPOUND).iterator();
            while (tagList.hasNext()) {
                CompoundNBT tagCompound = (CompoundNBT) tagList.next();
                Iterator<INBT> playerTagListIterator = tagCompound.getList("Player List", Constants.NBT.TAG_COMPOUND).iterator();
                List<UUID> uuidList = new ArrayList();
                while (playerTagListIterator.hasNext()) {
                    CompoundNBT playerTag = (CompoundNBT) playerTagListIterator.next();
                    UUID id = UUID.fromString(playerTag.getString("uuid"));
                    teamName = tagCompound.getString("Team Name");
                    NetworkPlayerInfo playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(id);
                    if (playerInfo != null) {
                        String playerName = playerInfo.getGameProfile().getName();
                        ClientEventHandler.idtoNameMap.put(id, playerName);
                        ClientEventHandler.nametoIdMap.put(playerName, id);
                    }
                    SaveData.teamMap.put(id, teamName);
                    uuidList.add(id);
                }
                SaveData.teamsMap.put(teamName, uuidList);
            }
            String myTeam = SaveData.teamMap.get(Minecraft.getInstance().player.getUniqueID());
            if (myTeam == null) {
                GuiHandler.priorityPlayers.clear();
            } else {
                for (UUID uuid : SaveData.teamsMap.get(myTeam)) {
                    String theirTeam = SaveData.teamMap.get(uuid);
                    if (theirTeam == null || !theirTeam.equals(myTeam)) {
                        GuiHandler.priorityPlayers.remove(uuid);
                    }
                }
            }
        }));
        ctx.get().setPacketHandled(true);
    }

}
