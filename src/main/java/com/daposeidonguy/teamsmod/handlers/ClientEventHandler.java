package com.daposeidonguy.teamsmod.handlers;

import com.daposeidonguy.teamsmod.client.Keybind;
import com.daposeidonguy.teamsmod.team.SaveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.Style;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class ClientEventHandler {

    public static boolean displayHud = true;

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if(!ConfigHandler.disablePing) {
            EntityPlayerSP p = Minecraft.getMinecraft().player;
            String team = SaveData.teamMap.get(p.getUniqueID());
            int slice = event.getMessage().getUnformattedComponentText().indexOf(">");
            if(slice>=0) {
                if(event.getMessage().getUnformattedComponentText().substring(slice).contains(p.getDisplayNameString()) || team!=null && (event.getMessage().getUnformattedComponentText().substring(slice).contains(' ' + team + ' ') || event.getMessage().getUnformattedComponentText().substring(slice).equals(' ' + team))) {
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
