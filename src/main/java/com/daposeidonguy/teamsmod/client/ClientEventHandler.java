package com.daposeidonguy.teamsmod.client;

import com.daposeidonguy.teamsmod.client.gui.toasts.ToastInvite;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientEventHandler {

    //Chat
    public static Map<String, com.mojang.datafixers.util.Pair<String, Long>> chatMap = new HashMap<>();
    public static String lastMessageReceived;
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

    @SubscribeEvent
    public static void onChatMessage(ClientChatReceivedEvent event) {
        if (event.getType() == ChatType.CHAT) {
            String message = event.getMessage().getString();
            int messageStart = message.indexOf(">");
            String senderName = message.substring(1, messageStart);
            if (!TeamConfig.disablePing) {
                String playerName = Minecraft.getInstance().player.getGameProfile().getName();
                String teamName = SaveData.teamMap.get(Minecraft.getInstance().player.getUniqueID());
                if (lastMessageReceived.contains(playerName) || (teamName != null && lastMessageReceived.contains(teamName))) {
                    event.getMessage().setStyle(new Style().setBold(true));
                    Minecraft.getInstance().player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 3.0F);
                }
            }
            if (!TeamConfig.disablePrefix && !TeamConfig.prefixServerSide) {
                UUID senderUID = nametoIdMap.get(senderName);
                if (SaveData.teamMap.containsKey(senderUID)) {
                    StringTextComponent newMessage = new StringTextComponent("[" + SaveData.teamMap.get(senderUID) + "] " + message);
                    newMessage.setStyle(event.getMessage().getStyle());
                    event.setMessage(newMessage);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLeaveServer(WorldEvent.Unload event) {
        SaveData.teamsMap.clear();
        SaveData.teamMap.clear();
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindings.hud.isPressed()) {
            displayHud = !displayHud;
        } else if (KeyBindings.accept.isPressed()) {
            ToastInvite toast = Minecraft.getInstance().getToastGui().getToast(ToastInvite.class, IToast.NO_TOKEN);
            if (toast != null) {
                toast.accepted = true;
                Minecraft.getInstance().player.sendChatMessage("/teamsmod accept");
                Minecraft.getInstance().player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
            }
        }
    }
}
