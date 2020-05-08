package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.client.ClientUtils;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
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

/* Sent received to update command save data and configuration data */
public class MessageSaveData extends AbstractMessage {

    public MessageSaveData(PacketBuffer buf) {
        super(buf);
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
        tag.put("Teams", tagList);
        tag.putBoolean("prefixServerSide", TeamConfig.prefixServerSide);
        tag.putBoolean("disablePrefixServer", TeamConfig.disablePrefixServer);
    }

    public void onMessage(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            SaveData.teamsMap.clear();
            SaveData.teamMap.clear();
            TeamConfig.prefixServerSide = tag.getBoolean("prefixServerSide");
            TeamConfig.disablePrefixServer = tag.getBoolean("disablePrefixServer");
            updateTeams(tag.getList("Teams", Constants.NBT.TAG_COMPOUND).iterator());
            updatePriorityPlayers(SaveData.teamMap.get(Minecraft.getInstance().player.getUniqueID()));
        }));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void updateTeams(Iterator<INBT> tagList) {
        String teamName = "";
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
                    ClientUtils.idtoNameMap.put(id, playerName);
                    ClientUtils.nametoIdMap.put(playerName, id);
                }
                SaveData.teamMap.put(id, teamName);
                uuidList.add(id);
            }
            SaveData.teamsMap.put(teamName, uuidList);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void updatePriorityPlayers(String myTeam) {
        if (myTeam == null) {
            GuiHandler.priorityPlayers.clear();
        } else {
            for (UUID uuid : GuiHandler.priorityPlayers) {
                String theirTeam = SaveData.teamMap.get(uuid);
                if (theirTeam == null || !theirTeam.equals(myTeam)) {
                    GuiHandler.priorityPlayers.remove(uuid);
                }
            }
        }
    }

}
