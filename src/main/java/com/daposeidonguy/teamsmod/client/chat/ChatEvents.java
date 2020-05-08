package com.daposeidonguy.teamsmod.client.chat;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.ClientUtils;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

/* Handles events related to chat features */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChatEvents {

    /* Appends prefix to message (depending on config) and plays sound and emboldens message if pinged */
    /* Also handles command messages (removes incoming message from teams chat, ensures messages otherwise added to both chats) */
    @SubscribeEvent
    public static void onChatReceived(ClientChatReceivedEvent event) {
        if (event.getType() == ChatType.CHAT) {
            if (!TeamConfig.disablePrefix && !TeamConfig.prefixServerSide) {
                String message = event.getMessage().getString();
                String senderName = message.substring(1, message.indexOf(">"));
                UUID senderUID = ClientUtils.nametoIdMap.get(senderName);
                if (SaveData.teamMap.containsKey(senderUID)) {
                    StringTextComponent newMessage = new StringTextComponent("[" + SaveData.teamMap.get(senderUID) + "] " + message);
                    newMessage.setStyle(event.getMessage().getStyle());
                    event.setMessage(newMessage);
                }
            }
            String senderTeam = SaveData.teamMap.get(ChatHandler.lastMessageReceived.getFirst());
            String myTeam = SaveData.teamMap.get(Minecraft.getInstance().player.getUniqueID());
            boolean doPing = doPing(ChatHandler.lastMessageReceived.getSecond(), Minecraft.getInstance().player.getGameProfile().getName(), myTeam);
            if (doPing) {
                event.getMessage().setStyle(new Style().setBold(true));
            }
            handleTeamChat(event, senderTeam, myTeam);
            if (doPing && (!ChatHandler.lastMessageTeam || (ChatHandler.lastMessageTeam && myTeam != null && senderTeam.equals(myTeam)))) {
                Minecraft.getInstance().player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 3.0F);
            }
        } else if (GuiHandler.displayTeamChat) {
            GuiHandler.backupChatGUI.printChatMessage(event.getMessage());
        }
    }

    /* Handles cancellation and forwarding of incoming chat messages to the appropriate chat GUI (command or global) */
    private static void handleTeamChat(ClientChatReceivedEvent event, String senderTeam, String myTeam) {
        if (GuiHandler.displayTeamChat) {
            if (ChatHandler.lastMessageTeam) {
                if (!senderTeam.equals(myTeam)) {
                    event.setCanceled(true);
                }
            } else {
                GuiHandler.backupChatGUI.printChatMessage(event.getMessage());
                if (senderTeam == null || !senderTeam.equals(myTeam)) {
                    event.setCanceled(true);
                }
            }
        } else {
            if (ChatHandler.lastMessageTeam) {
                event.setCanceled(true);
                if (senderTeam != null && myTeam != null && senderTeam.equals(myTeam)) {
                    GuiHandler.backupChatGUI.printChatMessage(event.getMessage());
                }
            } else {
                if (senderTeam != null && myTeam != null && senderTeam.equals(myTeam)) {
                    GuiHandler.backupChatGUI.printChatMessage(event.getMessage());
                }
            }
        }
    }

    /* Returns true if player should be "pinged", and false otherwise */
    private static boolean doPing(String msg, String player, String team) {
        if (TeamConfig.disablePing) {
            return false;
        }
        boolean mentionsPlayer = msg.contains(" " + player) || msg.contains(player + " ") || msg.equals(player);
        if (team == null) {
            return mentionsPlayer;
        } else {
            boolean mentionsTeam = msg.contains(" " + team) || msg.contains(team + " ") || msg.equals(team);
            return mentionsPlayer || mentionsTeam;
        }
    }

}
