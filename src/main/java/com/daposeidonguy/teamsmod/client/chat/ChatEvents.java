package com.daposeidonguy.teamsmod.client.chat;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.common.config.ConfigHelper;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Style;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/* Handles events related to chat features */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID, value = Side.CLIENT)
class ChatEvents {

    /* Appends prefix to message (depending on config) and plays sound and emboldens message if pinged */
    /* Also handles team messages (removes incoming message from teams chat, ensures messages otherwise added to both chats) */
    @SubscribeEvent
    public static void onChatReceived(final ClientChatReceivedEvent event) {
        if (event.getType() == ChatType.CHAT) {
            if (ChatHelper.getLastMessageSender() == null) return;
            if (TeamConfig.common.disablePrefix && !event.getMessage().getSiblings().isEmpty()) {
                event.setMessage(event.getMessage().getSiblings().get(0));
            }
            String senderTeam = StorageHelper.getTeam(ChatHelper.getLastMessageSender());
            String myTeam = StorageHelper.getTeam(Minecraft.getMinecraft().player.getUniqueID());
            boolean doPing = doPing(ChatHelper.getLastMessage(), Minecraft.getMinecraft().player.getGameProfile().getName(), myTeam);
            if (doPing) {
                event.getMessage().setStyle(new Style().setBold(true));
            }
            handleTeamChat(event, senderTeam, myTeam);
            if (doPing && (!ChatHelper.wasLastMessageTeam() || senderTeam.equals(myTeam))) {
                Minecraft.getMinecraft().player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 3.0F);
            }
            ChatHelper.clear();
        } else if (GuiHandler.displayTeamChat) {
            GuiHandler.backupChatGUI.printChatMessage(event.getMessage());
        }
    }

    /* Handles cancellation and forwarding of incoming chat messages to the appropriate chat GUI (team or global) */
    private static void handleTeamChat(final ClientChatReceivedEvent event, final String senderTeam, final String myTeam) {
        if (GuiHandler.displayTeamChat) {
            if (ChatHelper.wasLastMessageTeam()) {
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
            if (ChatHelper.wasLastMessageTeam()) {
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
        if (TeamConfig.common.disablePing || ConfigHelper.serverDisablePing) {
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
