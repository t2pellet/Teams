package com.daposeidonguy.teamsmod.client.keybind;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.ClientHelper;
import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.toasts.ToastInvite;
import com.daposeidonguy.teamsmod.common.network.NetworkHelper;
import com.daposeidonguy.teamsmod.common.network.messages.MessageTeamChat;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/* Handles events related to keybindings */
@Mod.EventBusSubscriber(modid = TeamsMod.MODID, value = Side.CLIENT)
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
            ToastInvite toast = Minecraft.getMinecraft().getToastGui().getToast(ToastInvite.class, IToast.NO_TOKEN);
            if (toast != null) {
                toast.accepted = true;
                Minecraft.getMinecraft().player.sendChatMessage("/teamsmod accept");
                Minecraft.getMinecraft().player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.5F);
            }
        } else if (KeyBindHandler.switchChat.isPressed() && StorageHelper.isPlayerInTeam(ClientHelper.mc.player.getUniqueID())) {
            try {
                GuiNewChat oldGui = (GuiNewChat) GuiHandler.persistentChatGUI.get(ClientHelper.mc.ingameGUI);
                GuiHandler.persistentChatGUI.set(ClientHelper.mc.ingameGUI, GuiHandler.backupChatGUI);
                GuiHandler.backupChatGUI = oldGui;
                GuiHandler.displayTeamChat = !GuiHandler.displayTeamChat;
                renderChatTime = 25;
                prevClientTick = ClientHelper.ticks;
                NetworkHelper.sendToServer(new MessageTeamChat(ClientHelper.mc.player.getUniqueID(), GuiHandler.displayTeamChat));
            } catch (IllegalAccessException ignore) {
            }
        }
    }

    @SubscribeEvent
    public static void renderChatStatus(TickEvent.RenderTickEvent event) {
        if (renderChatTime > 1 && ClientHelper.mc.currentScreen == null) {
            if (ClientHelper.ticks - prevClientTick > 0) {
                --renderChatTime;
            }
            prevClientTick = ClientHelper.ticks;
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            String renderText;
            if (GuiHandler.displayTeamChat) {
                renderText = "Showing Team Chat";
            } else {
                renderText = "Showing Server Chat";
            }
            float renderX = ClientHelper.getWindow().getScaledWidth() / 2 - ClientHelper.mc.fontRenderer.getStringWidth(renderText) / 2;
            float renderY = ClientHelper.getWindow().getScaledHeight() * 0.77F;
            Color colorText = new Color(1.0F, 1.0F, 1.0F, renderChatTime * 1.0F / 25);
            ClientHelper.mc.fontRenderer.drawString(renderText, (int) renderX, (int) renderY, colorText.getRGB());
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

}
