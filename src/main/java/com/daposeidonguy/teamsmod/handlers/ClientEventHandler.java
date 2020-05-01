package com.daposeidonguy.teamsmod.handlers;

import com.daposeidonguy.teamsmod.client.Keybind;
import com.daposeidonguy.teamsmod.client.gui.ToastInvite;
import com.daposeidonguy.teamsmod.team.SaveData;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientEventHandler {

    public static Map<String, Pair<String, Long>> chatMap = new HashMap<>();
    public static String lastMessage;
    public static boolean displayHud = true;
    public static long ticks = 0;
    public static Map<UUID, String> idtoNameMap = new HashMap<>();
    public static Map<String, UUID> nametoIdMap = new HashMap<>();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        ticks += 1;
    }

    private boolean doPing(String msg, String player, String team) {
        if (msg.contains(player + " ") || msg.equals(player)) {
            return true;
        } else return team != null && (msg.contains(team + " ") || msg.equals(team));
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (event.getType() == ChatType.CHAT) {
            if (!ConfigHandler.client.disablePing) {
                String myName = Minecraft.getMinecraft().player.getDisplayNameString();
                String myTeam = SaveData.teamMap.get(Minecraft.getMinecraft().player.getUniqueID());
                if (doPing(lastMessage, myName, myTeam)) {
                    event.getMessage().setStyle(event.getMessage().getStyle().setBold(true));
                    Minecraft.getMinecraft().player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 3.0F);
                }
            }
            if (!ConfigHandler.client.disablePrefix && !ConfigHandler.server.prefixServerSide) {
                String message = event.getMessage().getUnformattedText();
                String senderName = message.substring(1, message.indexOf(">"));
                String teamName = SaveData.teamMap.get(nametoIdMap.get(senderName));
                if (teamName != null) {
                    TextComponentString newMessage = new TextComponentString("[" + teamName + "] " + message);
                    newMessage.setStyle(event.getMessage().getStyle());
                    event.setMessage(newMessage);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLeaveServer(WorldEvent.Unload event) {
        SaveData.teamsMap.clear();
        SaveData.teamMap.clear();
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Keybind.hud.isPressed()) {
            displayHud = !displayHud;
        } else if (Keybind.accept.isPressed()) {
            ToastInvite toast = Minecraft.getMinecraft().getToastGui().getToast(ToastInvite.class, IToast.NO_TOKEN);
            if (toast != null) {
                toast.accepted = true;
                Minecraft.getMinecraft().player.sendChatMessage("/teamsmod accept");
                Minecraft.getMinecraft().player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
            }
        }
    }
}
