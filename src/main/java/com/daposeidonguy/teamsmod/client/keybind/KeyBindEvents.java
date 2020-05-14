package com.daposeidonguy.teamsmod.client.keybind;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.ClientHandler;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.toasts.ToastInvite;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.network.messages.MessageTeamChat;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;

/* Handles events related to keybindings */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
class KeyBindEvents {

    private static float renderChatTime;
    private static long prevClientTick;

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
                Minecraft.getInstance().player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.5F);
            }
        } else if (KeyBindHandler.switchChat.isPressed() && StorageHandler.uuidToTeamMap.get(ClientHandler.mc.player.getUniqueID()) != null) {
            try {
                NewChatGui oldGui = (NewChatGui) GuiHandler.persistentChatGUI.get(ClientHandler.mc.ingameGUI);
                GuiHandler.persistentChatGUI.set(ClientHandler.mc.ingameGUI, GuiHandler.backupChatGUI);
                GuiHandler.backupChatGUI = oldGui;
                GuiHandler.displayTeamChat = !GuiHandler.displayTeamChat;
                renderChatTime = 25;
                prevClientTick = ClientHandler.ticks;
                PacketHandler.INSTANCE.sendToServer(new MessageTeamChat(ClientHandler.mc.player.getUniqueID(), GuiHandler.displayTeamChat));
            } catch (IllegalAccessException ignore) {
            }

        }
    }

    @SubscribeEvent
    public static void renderChatStatus(TickEvent.RenderTickEvent event) {
        if (renderChatTime > 1 && Minecraft.getInstance().currentScreen == null) {
            if (ClientHandler.ticks - prevClientTick > 0) {
                --renderChatTime;
            }
            prevClientTick = ClientHandler.ticks;
            RenderSystem.pushMatrix();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            String renderText;
            if (GuiHandler.displayTeamChat) {
                renderText = "Showing Team Chat";
            } else {
                renderText = "Showing Server Chat";
            }
            float renderX = ClientHandler.mc.getMainWindow().getScaledWidth() / 2 - ClientHandler.mc.fontRenderer.getStringWidth(renderText) / 2;
            float renderY = ClientHandler.mc.getMainWindow().getScaledHeight() * 0.74F;
            Color colorText = new Color(1.0F, 1.0F, 1.0F, renderChatTime * 1.0F / 25);
            Minecraft.getInstance().fontRenderer.drawString(renderText, renderX, renderY, colorText.getRGB());
            RenderSystem.disableBlend();
            RenderSystem.popMatrix();
        }
    }

}
