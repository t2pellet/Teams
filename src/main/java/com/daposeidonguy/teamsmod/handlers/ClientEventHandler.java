package com.daposeidonguy.teamsmod.handlers;

import com.daposeidonguy.teamsmod.client.Keybind;
import com.daposeidonguy.teamsmod.team.SaveData;
import com.mojang.realmsclient.util.Pair;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientEventHandler {

    public static Map<String, Pair<String, Long>> chatMap = new HashMap<>();
    public static boolean displayHud = true;
    public static long ticks = 0;
    public static Map<UUID, String> idtoNameMap = new HashMap<>();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        ticks += 1;
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        int len = event.getMessage().getUnformattedText().length();
        if (event.getType() == ChatType.CHAT) {
            int slice = event.getMessage().getUnformattedText().indexOf(">");
            if (slice == -1) {
                return;
            }
            slice += 1;
            if (0 < slice && slice < len) {
                String senderName = event.getMessage().getUnformattedText().substring(1, slice - 1);
                String receivedText = event.getMessage().getUnformattedText().substring(slice + 1);
                if (!ConfigHandler.client.disableChatBubble) {
                    Pair<String, Long> chatMessage = Pair.of(receivedText, ticks);
                    chatMap.put(senderName, chatMessage);
                }
                if (!ConfigHandler.client.disablePrefix) {
                    UUID senderUID = FMLClientHandler.instance().getWorldClient().getPlayerEntityByName(senderName).getUniqueID();
                    if (SaveData.teamMap.containsKey(senderUID)) {
                        TextComponentString newMessage = new TextComponentString("[" + SaveData.teamMap.get(senderUID) + "]" + " <" + senderName + "> " + receivedText);
                        newMessage.setStyle(event.getMessage().getStyle());
                        event.setMessage(newMessage);
                    }
                }
                if (!ConfigHandler.client.disablePing) {
                    EntityPlayerSP clientPlayer = FMLClientHandler.instance().getClientPlayerEntity();
                    String teamName = SaveData.teamMap.get(clientPlayer.getUniqueID());
                    String clientName = clientPlayer.getDisplayNameString();
                    if (receivedText.contains(clientName) || (teamName != null && receivedText.contains(teamName))) {
                        Style bold = new Style();
                        bold.setBold(true);
                        TextComponentString newMessage = new TextComponentString(event.getMessage().getUnformattedText());
                        newMessage.setStyle(bold);
                        event.setMessage(newMessage);
                        clientPlayer.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 5.0F);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingAttackEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            UUID clientUUID = FMLClientHandler.instance().getClientPlayerEntity().getUniqueID();
            if (SaveData.teamMap.containsKey(clientUUID) && event.getEntityLiving().getHealth() < event.getAmount()) {
                String team = SaveData.teamMap.get(clientUUID);
                if (SaveData.teamsMap.get(team).contains(event.getEntityLiving().getUniqueID())) {
                    FMLClientHandler.instance().getClientPlayerEntity().playSound(SoundEvents.ENTITY_LIGHTNING_THUNDER, 1.0F, 5.0F);
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
        if (Keybind.display.isPressed()) {
            displayHud = !displayHud;
        }
    }
}
