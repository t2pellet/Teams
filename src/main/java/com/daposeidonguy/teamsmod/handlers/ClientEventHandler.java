package com.daposeidonguy.teamsmod.handlers;

import com.daposeidonguy.teamsmod.client.Keybind;
import com.daposeidonguy.teamsmod.team.Team;
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
        EntityPlayerSP p = Minecraft.getMinecraft().player;
        Team team = Team.getTeam(Minecraft.getMinecraft().player.getUniqueID());
        if(event.getMessage().getUnformattedComponentText().substring(3).contains(p.getDisplayNameString()) || team!=null && event.getMessage().getUnformattedComponentText().substring(3).contains(team.getName())) {
            Style newStyle = new Style();
            newStyle.setBold(true);
            event.setMessage(event.getMessage().setStyle(newStyle));
            p.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,1.0F,5.0F);
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(Keybind.display.isPressed()) {
            displayHud=!displayHud;
        }
    }
}
