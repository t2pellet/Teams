package com.daposeidonguy.teamsmod.client.keybind;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.ClientHandler;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.toasts.ToastInvite;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.network.messages.MessageTeamChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/* Handles events related to keybindings */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
class KeyBindEvents {

    /* Handles keybinded dynamic toggling of certain features */
    @SubscribeEvent
    public static void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (KeyBindHandler.showStatus.isPressed()) {
            KeyBindHandler.doDisplayStatus = !KeyBindHandler.doDisplayStatus;
        } else if (KeyBindHandler.showCompass.isPressed()) {
            KeyBindHandler.doDisplayCompass = !KeyBindHandler.doDisplayCompass;
        } else if (KeyBindHandler.acceptInvite.isPressed()) {
            ToastInvite toast = Minecraft.getInstance().getToastGui().getToast(ToastInvite.class, IToast.NO_TOKEN);
            if (toast != null) {
                toast.accepted = true;
                Minecraft.getInstance().player.sendChatMessage("/teamsmod accept");
                Minecraft.getInstance().player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
            }
        } else if (KeyBindHandler.switchChat.isPressed()) {
            try {
                NewChatGui oldGui = (NewChatGui) GuiHandler.persistentChatGUI.get(ClientHandler.mc.ingameGUI);
                GuiHandler.persistentChatGUI.set(ClientHandler.mc.ingameGUI, GuiHandler.backupChatGUI);
                GuiHandler.backupChatGUI = oldGui;
                GuiHandler.displayTeamChat = !GuiHandler.displayTeamChat;
                PacketHandler.INSTANCE.sendToServer(new MessageTeamChat(ClientHandler.mc.player.getUniqueID(), GuiHandler.displayTeamChat));
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }

        }
    }

}
