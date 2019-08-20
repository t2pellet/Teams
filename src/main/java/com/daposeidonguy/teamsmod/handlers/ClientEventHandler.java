package com.daposeidonguy.teamsmod.handlers;

import com.daposeidonguy.teamsmod.client.Keybind;
import com.daposeidonguy.teamsmod.team.SaveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import java.util.UUID;

public class ClientEventHandler {

    public static boolean displayHud = true;

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        String text = event.getMessage().getUnformattedText();
        int slice = text.indexOf(">") + 1;
        if(slice>0) {
            if(!ConfigHandler.client.disablePrefix) {
                String playerName = text.substring(1,slice-1);
                UUID uid = FMLClientHandler.instance().getClient().world.getPlayerEntityByName(playerName).getUniqueID();
                if(SaveData.teamMap.containsKey(uid)) {
                    String message = "[" + SaveData.teamMap.get(uid) + "]" + " <" + playerName + "> "  + text.substring(slice+1);
                    event.setMessage(new TextComponentString(message));
                }
            }
            if(!ConfigHandler.client.disablePing) {
                EntityPlayerSP p = Minecraft.getMinecraft().player;
                String team = SaveData.teamMap.get(p.getUniqueID());
                if(text.substring(slice).contains(p.getDisplayNameString()) || (team!=null && ((text.substring(slice).contains(' ' + team + ' ')) || text.substring(slice).equals(' ' + team) || text.indexOf(team)==text.length()-team.length()))) {
                    Style newStyle = new Style();
                    newStyle.setBold(true);
                    event.setMessage(event.getMessage().setStyle(newStyle));
                    p.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,1.0F,5.0F);
                }
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(Keybind.display.isPressed()) {
            displayHud=!displayHud;
        }
    }
}
