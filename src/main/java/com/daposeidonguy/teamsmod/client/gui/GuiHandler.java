package com.daposeidonguy.teamsmod.client.gui;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.ClientUtils;
import com.daposeidonguy.teamsmod.client.gui.overlay.CompassOverlay;
import com.daposeidonguy.teamsmod.client.gui.overlay.StatusOverlay;
import com.daposeidonguy.teamsmod.client.gui.screen.team.ScreenTeam;
import com.daposeidonguy.teamsmod.client.gui.widget.ClearButton;
import com.daposeidonguy.teamsmod.client.keybind.KeyBindHandler;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.network.messages.MessageTeamChat;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.lang.reflect.Field;
import java.util.*;

@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GuiHandler {

    public static Minecraft mc = Minecraft.getInstance();
    public static Map<UUID, Integer> hungerMap = new HashMap<>();
    public static Map<UUID, Integer> healthMap = new HashMap<>();
    public static Map<String, Pair<String, Long>> chatMap = new HashMap<>();
    public static List<UUID> priorityPlayers = new ArrayList<>();
    public static boolean displayTeamChat = false;
    public static NewChatGui backupChatGUI = new NewChatGui(mc);
    public static Field persistentChatGUI = ObfuscationReflectionHelper.findField(IngameGui.class, "field_73840_e");


    /* Displays chat bubbles */
    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        String playerName = event.getPlayer().getGameProfile().getName();
        String localName = mc.player.getGameProfile().getName();
        if (!localName.equals(playerName) && GuiHandler.chatMap.containsKey(playerName)) {
            String text = GuiHandler.chatMap.get(playerName).getFirst();
            long tick = GuiHandler.chatMap.get(playerName).getSecond();
            if ((ClientUtils.ticks - tick) < 200) {
                renderBubbleHelper(event.getRenderer().getRenderManager(), event.getPlayer(), text, event.getMatrixStack(), event.getBuffers(), event.getLight());
            } else {
                GuiHandler.chatMap.remove(playerName);
            }
        }
    }

    // Adapted from EntityRenderer::renderName to render chat bubble
    private static void renderBubbleHelper(EntityRendererManager renderManager, PlayerEntity player, String text, MatrixStack stack, IRenderTypeBuffer buffer, int light) {
        double d0 = renderManager.squareDistanceTo(player);
        if (!(d0 > 2048.0D)) {
            boolean flag = !player.isDiscrete();
            float f = player.getHeight() + 0.85F;
            stack.push();
            stack.translate(0.0D, (double) f, 0.0D);
            stack.rotate(renderManager.getCameraOrientation());
            stack.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = stack.getLast().getMatrix();
            float f1 = mc.gameSettings.getTextBackgroundOpacity(0.25F);
            int j = (int) (f1 * 255.0F) << 24;
            FontRenderer fontrenderer = renderManager.getFontRenderer();
            float f2 = (float) (-fontrenderer.getStringWidth(text) / 2);
            fontrenderer.renderString(text, f2, 0, 553648127, false, matrix4f, buffer, flag, j, light);
            if (flag) {
                fontrenderer.renderString(text, f2, 0, -1, false, matrix4f, buffer, false, 0, light);
            }
            stack.pop();
        }
    }

    /* Show Teams GUI button in inventory screens */
    @SubscribeEvent
    public static void onInventoryScreen(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof DisplayEffectsScreen) {
            DisplayEffectsScreen guiInventory = (DisplayEffectsScreen) event.getGui();
            boolean isCreative = event.getGui() instanceof CreativeScreen;
            int renderX = (TeamConfig.useAlternatePosition || isCreative) ? 2 : guiInventory.getGuiLeft() + 152;
            int renderY = (TeamConfig.useAlternatePosition || isCreative) ? 2 : guiInventory.getGuiTop() + 4;
            int renderWidth = TeamConfig.smallIcon ? 15 : 20;
            int renderHeight = TeamConfig.smallIcon ? 14 : 18;
            ResourceLocation renderLoc = TeamConfig.smallIcon ? new ResourceLocation(TeamsMod.MODID, "textures/gui/buttonsmall.png") : new ResourceLocation(TeamsMod.MODID, "textures/gui/button.png");
            ImageButton guiButtonImage = new ImageButton(renderX, renderY, renderWidth, renderHeight, 0, 0, renderHeight, renderLoc, press -> {
                mc.displayGuiScreen(new ScreenTeam());
            });
            event.addWidget(guiButtonImage);
        }
    }

    /* Handles teams chat tab and button */
    @SubscribeEvent
    public static void onChatScreen(GuiScreenEvent.InitGuiEvent.Post event) throws IllegalAccessException {
        if (event.getGui() instanceof ChatScreen) {
            MainWindow window = mc.getMainWindow();
            int buttonWidth1 = mc.fontRenderer.getStringWidth("Showing Server Chat");
            int buttonWidth2 = mc.fontRenderer.getStringWidth("Showing Team Chat");
            String myTeam = SaveData.teamMap.get(mc.player.getUniqueID());
            if (myTeam != null) {
                ClearButton button = chatToggleButton(window, buttonWidth1, buttonWidth2);
                event.addWidget(button);
            } else if (displayTeamChat) {
                displayTeamChat = false;
                NewChatGui oldGui = (NewChatGui) persistentChatGUI.get(mc.ingameGUI);
                persistentChatGUI.set(mc.ingameGUI, backupChatGUI);
                backupChatGUI = oldGui;
                PacketHandler.INSTANCE.sendToServer(new MessageTeamChat(mc.player.getUniqueID(), displayTeamChat));
            }
        }
    }

    /* Returns button to switch chat screens */
    private static ClearButton chatToggleButton(MainWindow window, int buttonWidth1, int buttonWidth2) {
        ClearButton button = new ClearButton((int) (window.getScaledWidth() * 0.99 - buttonWidth1), (int) (window.getScaledHeight() * 0.90), buttonWidth1, 10, "Showing Server Chat", btn -> {
            try {
                NewChatGui oldGui = (NewChatGui) persistentChatGUI.get(mc.ingameGUI);
                persistentChatGUI.set(mc.ingameGUI, backupChatGUI);
                backupChatGUI = oldGui;
                displayTeamChat = !displayTeamChat;
                PacketHandler.INSTANCE.sendToServer(new MessageTeamChat(mc.player.getUniqueID(), displayTeamChat));
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
            if (displayTeamChat) {
                btn.setMessage("Showing Team Chat");
                btn.setWidth(buttonWidth2);
                btn.x = (int) (window.getScaledWidth() * 0.99 - buttonWidth2);
            } else {
                btn.setMessage("Showing Server Chat");
                btn.setWidth(buttonWidth1);
                btn.x = (int) (window.getScaledWidth() * 0.99 - buttonWidth1);
            }
        });
        if (displayTeamChat) {
            button.setMessage("Showing Team Chat");
            button.setWidth(buttonWidth2);
            button.x = (int) (window.getScaledWidth() * 0.99 - buttonWidth2);
        }
        return button;
    }

    /* Render HUD elements */
    @SubscribeEvent
    public static void renderHUDEvent(RenderGameOverlayEvent.Post event) {
        //Check if clientside and HUD is enabled
        if (EffectiveSide.get().isClient() &&
                !TeamConfig.disableTeamsHUD &&
                KeyBindHandler.doDisplayHud &&
                !event.isCancelable() &&
                event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            UUID id = mc.player.getUniqueID();
            String team = SaveData.teamMap.get(id);
            if (team != null) {
                new StatusOverlay(mc, team);
                new CompassOverlay(mc, team);
            }
        }
    }

}