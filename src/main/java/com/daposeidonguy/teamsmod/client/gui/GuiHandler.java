package com.daposeidonguy.teamsmod.client.gui;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.ClientEventHandler;
import com.daposeidonguy.teamsmod.client.gui.overlay.CompassOverlay;
import com.daposeidonguy.teamsmod.client.gui.overlay.StatusOverlay;
import com.daposeidonguy.teamsmod.client.gui.screen.team.ScreenTeam;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.FontRenderer;
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
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.util.*;

@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GuiHandler {

    public static Map<UUID, Integer> hungerMap = new HashMap<>();
    public static Map<UUID, Integer> healthMap = new HashMap<>();
    public static List<UUID> priorityPlayers = new ArrayList<>();

    // Handle chat bubbles

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        String playerName = event.getPlayer().getGameProfile().getName();
        String localName = Minecraft.getInstance().player.getGameProfile().getName();
        if (!localName.equals(playerName) && ClientEventHandler.chatMap.containsKey(playerName)) {
            String text = ClientEventHandler.chatMap.get(playerName).getFirst();
            long tick = ClientEventHandler.chatMap.get(playerName).getSecond();
            if ((ClientEventHandler.ticks - tick) < 200) {
                renderBubbleHelper(event.getRenderer().getRenderManager(), event.getPlayer(), text, event.getMatrixStack(), event.getBuffers(), event.getLight());
            } else {
                ClientEventHandler.chatMap.remove(playerName);
            }
        }
    }

    // Adapted from EntityRenderer::renderName
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
            float f1 = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F);
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

    // Handle rendering of teams GUI button
    @SubscribeEvent
    public static void onGuiScreen(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof DisplayEffectsScreen) {
            DisplayEffectsScreen guiInventory = (DisplayEffectsScreen) event.getGui();
            boolean isCreative = event.getGui() instanceof CreativeScreen;
            int renderX = (TeamConfig.useAlternatePosition || isCreative) ? 2 : guiInventory.getGuiLeft() + 152;
            int renderY = (TeamConfig.useAlternatePosition || isCreative) ? 2 : guiInventory.getGuiTop() + 4;
            int renderWidth = TeamConfig.smallIcon ? 15 : 20;
            int renderHeight = TeamConfig.smallIcon ? 14 : 18;
            ResourceLocation renderLoc = TeamConfig.smallIcon ? new ResourceLocation(TeamsMod.MODID, "textures/gui/buttonsmall.png") : new ResourceLocation(TeamsMod.MODID, "textures/gui/button.png");
            ImageButton guiButtonImage = new ImageButton(renderX, renderY, renderWidth, renderHeight, 0, 0, renderHeight, renderLoc, press -> {
                Minecraft.getInstance().displayGuiScreen(new ScreenTeam());
            });
            event.addWidget(guiButtonImage);
        }
    }

    // Handle rendering of HUD elements
    @SubscribeEvent
    public static void renderHUDEvent(RenderGameOverlayEvent.Post event) {
        //Check if clientside and HUD is enabled
        if (EffectiveSide.get().isClient() &&
                !TeamConfig.disableTeamsHUD &&
                ClientEventHandler.displayHud &&
                !event.isCancelable() &&
                event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            UUID id = Minecraft.getInstance().player.getUniqueID();
            String team = SaveData.teamMap.get(id);
            if (team != null) {
                Minecraft mc = Minecraft.getInstance();
                new StatusOverlay(mc, team);
                new CompassOverlay(mc, team);
            }
        }
    }

}