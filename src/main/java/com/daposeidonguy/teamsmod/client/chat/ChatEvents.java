package com.daposeidonguy.teamsmod.client.chat;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.common.config.ConfigHandler;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Style;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/* Handles events related to chat features */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
class ChatEvents {

    /* Appends prefix to message (depending on config) and plays sound and emboldens message if pinged */
    /* Also handles team messages (removes incoming message from teams chat, ensures messages otherwise added to both chats) */
    @SubscribeEvent
    public static void onChatReceived(final ClientChatReceivedEvent event) {
        if (event.getType() == ChatType.CHAT) {
            if (TeamConfig.disablePrefix && !event.getMessage().getSiblings().isEmpty()) {
                event.setMessage(event.getMessage().getSiblings().get(0));
            }
            String senderTeam = StorageHelper.getTeam(ChatHandler.lastMessageReceived.getFirst());
            String myTeam = StorageHelper.getTeam(Minecraft.getInstance().player.getUniqueID());
            boolean doPing = doPing(ChatHandler.lastMessageReceived.getSecond(), Minecraft.getInstance().player.getGameProfile().getName(), myTeam);
            if (doPing) {
                event.getMessage().setStyle(new Style().setBold(true));
            }
            handleTeamChat(event, senderTeam, myTeam);
            if (doPing && (!ChatHandler.lastMessageTeam || senderTeam.equals(myTeam))) {
                Minecraft.getInstance().player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 3.0F);
            }
        } else if (GuiHandler.displayTeamChat) {
            GuiHandler.backupChatGUI.printChatMessage(event.getMessage());
        }
    }

    /* Handles cancellation and forwarding of incoming chat messages to the appropriate chat GUI (team or global) */
    private static void handleTeamChat(final ClientChatReceivedEvent event, final String senderTeam, final String myTeam) {
        if (GuiHandler.displayTeamChat) {
            if (ChatHandler.lastMessageTeam) {
                if (senderTeam == null || !senderTeam.equals(myTeam)) {
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
                if (senderTeam != null && senderTeam.equals(myTeam)) {
                    GuiHandler.backupChatGUI.printChatMessage(event.getMessage());
                }
            } else {
                if (senderTeam != null && senderTeam.equals(myTeam)) {
                    GuiHandler.backupChatGUI.printChatMessage(event.getMessage());
                }
            }
        }
    }

    /* Returns true if player should be "pinged", and false otherwise */
    private static boolean doPing(final String msg, final String player, final String team) {
        if (TeamConfig.disablePing || ConfigHandler.serverDisablePing) {
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
