package com.daposeidonguy.teamsmod.client.keybind;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.gui.toasts.ToastInvite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/* Handles events related to keybindings */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KeyBindEvents {

    /* Handles keybinded dynamic toggling of certain features */
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindHandler.showHud.isPressed()) {
            KeyBindHandler.doDisplayHud = !KeyBindHandler.doDisplayHud;
        } else if (KeyBindHandler.acceptInvite.isPressed()) {
            ToastInvite toast = Minecraft.getInstance().getToastGui().getToast(ToastInvite.class, IToast.NO_TOKEN);
            if (toast != null) {
                toast.accepted = true;
                Minecraft.getInstance().player.sendChatMessage("/teamsmod accept");
                Minecraft.getInstance().player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
            }
        }
    }

}
