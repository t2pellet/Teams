package com.daposeidonguy.teamsmod.client;

import com.daposeidonguy.teamsmod.client.gui.toasts.ToastInvite;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
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

    public static Map<String, com.mojang.datafixers.util.Pair<String, Long>> chatMap = new HashMap<>();
    public static boolean displayHud = true;
    public static long ticks = 0;
    public static Map<UUID, String> idtoNameMap = new HashMap<>();
    public static Map<String, UUID> nametoIdMap = new HashMap<>();

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        ticks += 1;
    }

    @SubscribeEvent
    public static void onChatMessage(ClientChatReceivedEvent event) {
        int len = event.getMessage().getString().length();
        if (event.getType() == ChatType.CHAT) {
            int slice = event.getMessage().getString().indexOf(">");
            if (slice == -1) {
                return;
            }
            slice += 1;
            if (0 < slice && slice < len) {
                String senderName = event.getMessage().getString().substring(1, slice - 1);
                String receivedText = event.getMessage().getString().substring(slice + 1);
                if (!TeamConfig.disableChatBubble) {
                    Pair<String, Long> chatMessage = new Pair(receivedText, ticks);
                    chatMap.put(senderName, chatMessage);
                }
                if (!TeamConfig.disablePrefix) {
                    UUID senderUID = nametoIdMap.get(senderName);
                    if (SaveData.teamMap.containsKey(senderUID)) {
                        StringTextComponent newMessage = new StringTextComponent("[" + SaveData.teamMap.get(senderUID) + "]" + " <" + senderName + "> " + receivedText);
                        newMessage.setStyle(event.getMessage().getStyle());
                        event.setMessage(newMessage);
                    }
                }
                if (!TeamConfig.disablePing) {
                    ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
                    String teamName = SaveData.teamMap.get(clientPlayer.getUniqueID());
                    String clientName = clientPlayer.getGameProfile().getName();
                    if (receivedText.contains(clientName) || (teamName != null && receivedText.contains(teamName))) {
                        Style bold = new Style();
                        bold.setBold(true);
                        StringTextComponent newMessage = new StringTextComponent(event.getMessage().getString());
                        newMessage.setStyle(bold);
                        event.setMessage(newMessage);
                        clientPlayer.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 5.0F);
                    }
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
