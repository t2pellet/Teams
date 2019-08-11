package com.daposeidonguy.teamsmod.network;

import com.daposeidonguy.teamsmod.team.SaveData;
import com.daposeidonguy.teamsmod.team.Team;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class MessageSaveData implements IMessage {

    private NBTTagCompound tagTeam = new NBTTagCompound();

    public MessageSaveData() {

    }

    public MessageSaveData(List<Team> listTeam) {
        NBTTagList tagList = new NBTTagList();
        Iterator<Team> iteratorTeams = listTeam.iterator();
        while(iteratorTeams.hasNext()) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            Team team = iteratorTeams.next();
            tagCompound.setString("Team Name",team.getName());
            tagCompound.setTag("Player List",team.getPlayerListTag());
            tagList.appendTag(tagCompound);
        }
        if(!FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().isRemote) {
            this.tagTeam.setTag("Teams",tagList);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tagTeam = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf,tagTeam);
    }

    public static class MessageHandler implements IMessageHandler<MessageSaveData, IMessage> {
        @Override
        public IMessage onMessage(MessageSaveData message, MessageContext ctx) {
            NBTTagCompound nbt = message.tagTeam;
            Minecraft.getMinecraft().addScheduledTask(() -> {
                SaveData.listTeams.clear();
                NBTTagList tagList = nbt.getTagList("Teams", Constants.NBT.TAG_COMPOUND);
                for (NBTBase b : tagList) {
                    NBTTagCompound tagCompound = (NBTTagCompound) b;
                    NBTTagList playerTagList = tagCompound.getTagList("Player List", Constants.NBT.TAG_COMPOUND);
                    List<UUID> uuidList = new ArrayList();
                    for (NBTBase p : playerTagList) {
                        NBTTagCompound playerTag = (NBTTagCompound) p;
                        UUID id = UUID.fromString(playerTag.getString("uuid"));
                        uuidList.add(id);
                    }
                    Team team = new Team(tagCompound.getString("Team Name"), uuidList);
                    SaveData.listTeams.add(team);
                }
            });
            return null;
        }
    }
}
