package com.daposeidonguy.teamsmod.common.network.messages;

import com.daposeidonguy.teamsmod.client.ClientHandler;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

import static com.daposeidonguy.teamsmod.common.storage.StorageHandler.readFromNBT;
import static com.daposeidonguy.teamsmod.common.storage.StorageHandler.writeToNBT;

/* Sent/received to update team save data and configuration data */
public class MessageSaveData extends AbstractMessage {


    public MessageSaveData() {
        tag = writeToNBT(new NBTTagCompound());
    }

    public static class MessageHandler implements IMessageHandler<MessageSaveData, IMessage> {
        @Override
        public IMessage onMessage(MessageSaveData message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                readFromNBT(message.tag);
                String myTeam = StorageHandler.uuidToTeamMap.get(ClientHandler.mc.player.getUniqueID());
                if (myTeam == null) {
                    GuiHandler.priorityPlayers.clear();
                }
                for (UUID id : GuiHandler.priorityPlayers) {
                    String theirTeam = StorageHandler.uuidToTeamMap.get(id);
                    if (!theirTeam.equals(myTeam)) {
                        GuiHandler.priorityPlayers.remove(id);
                    }
                }
            });
            return null;
        }
    }

}
