package com.daposeidonguy.teamsmod.client;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.toasts.ToastInvite;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {

    //Chat
    public static Pair<UUID, String> lastMessageReceived;
    public static boolean lastMessageTeam = false;
    //Util
    public static Map<UUID, String> idtoNameMap = new HashMap<>();
    public static Map<String, UUID> nametoIdMap = new HashMap<>();
    public static long ticks = 0;
    //Settings
    public static boolean displayHud = true;

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        ticks += 1;
    }

    /* Appends prefix to message (depending on config) and plays sound and emboldens message if pinged */
    /* Also handles team messages (removes incoming message from teams chat, ensures messages otherwise added to both chats) */
    @SubscribeEvent
    public static void onChatReceived(ClientChatReceivedEvent event) {
        if (event.getType() == ChatType.CHAT) {
            if (!TeamConfig.disablePrefix && !TeamConfig.prefixServerSide) {
                String message = event.getMessage().getString();
                String senderName = message.substring(1, message.indexOf(">"));
                UUID senderUID = nametoIdMap.get(senderName);
                if (SaveData.teamMap.containsKey(senderUID)) {
                    StringTextComponent newMessage = new StringTextComponent("[" + SaveData.teamMap.get(senderUID) + "] " + message);
                    newMessage.setStyle(event.getMessage().getStyle());
                    event.setMessage(newMessage);
                }
            }
            String senderTeam = SaveData.teamMap.get(ClientEventHandler.lastMessageReceived.getFirst());
            String myTeam = SaveData.teamMap.get(Minecraft.getInstance().player.getUniqueID());
            boolean doPing = doPing(lastMessageReceived.getSecond(), Minecraft.getInstance().player.getGameProfile().getName(), myTeam);
            if (doPing) {
                event.getMessage().setStyle(new Style().setBold(true));
            }
            handleTeamChat(event, senderTeam, myTeam);
            if (doPing && (!lastMessageTeam || (lastMessageTeam && myTeam != null && senderTeam.equals(myTeam)))) {
                Minecraft.getInstance().player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 3.0F);
            }
        } else if (GuiHandler.displayTeamChat) {
            GuiHandler.backupChatGUI.printChatMessage(event.getMessage());
        }
    }

    /* Handles cancellation and forwarding of incoming chat messages to the appropriate chat GUI (team or global) */
    private static void handleTeamChat(ClientChatReceivedEvent event, String senderTeam, String myTeam) {
        if (GuiHandler.displayTeamChat) {
            if (lastMessageTeam) {
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
            if (lastMessageTeam) {
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

    /* Handles keybinded dynamic toggling of certain features */
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindings.showHud.isPressed()) {
            displayHud = !displayHud;
        } else if (KeyBindings.acceptInvite.isPressed()) {
            ToastInvite toast = Minecraft.getInstance().getToastGui().getToast(ToastInvite.class, IToast.NO_TOKEN);
            if (toast != null) {
                toast.accepted = true;
                Minecraft.getInstance().player.sendChatMessage("/teamsmod accept");
                Minecraft.getInstance().player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
            }
        }
    }
}
