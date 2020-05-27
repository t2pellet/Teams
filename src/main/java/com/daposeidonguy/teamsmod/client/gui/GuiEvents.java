package com.daposeidonguy.teamsmod.client.gui;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.ClientHelper;
import com.daposeidonguy.teamsmod.client.gui.overlay.CompassOverlay;
import com.daposeidonguy.teamsmod.client.gui.overlay.StatusOverlay;
import com.daposeidonguy.teamsmod.client.gui.screen.team.ScreenMain;
import com.daposeidonguy.teamsmod.client.gui.widget.ChatButton;
import com.daposeidonguy.teamsmod.client.keybind.KeyBindHandler;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.network.NetworkHelper;
import com.daposeidonguy.teamsmod.common.network.messages.MessageTeamChat;
import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.FontRenderer;
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
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = TeamsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GuiEvents {

    /* Displays chat bubbles */
    @SubscribeEvent
    public static void onRenderPlayer(final RenderPlayerEvent.Pre event) {
        String playerName = event.getPlayer().getGameProfile().getName();
        String localName = ClientHelper.mc.player.getGameProfile().getName();
        if (!localName.equals(playerName) && GuiHandler.chatMap.containsKey(playerName) && !TeamConfig.disableChatBubble) {
            String text = GuiHandler.chatMap.get(playerName).getFirst();
            long tick = GuiHandler.chatMap.get(playerName).getSecond();
            if ((ClientHelper.ticks - tick) < 200) {
                renderBubbleHelper(event.getRenderer().getRenderManager(), event.getPlayer(), text, event.getMatrixStack(), event.getBuffers(), event.getLight());
            } else {
                GuiHandler.chatMap.remove(playerName);
            }
        }
    }

    // Adapted from EntityRenderer::renderName to render chat bubble
    private static void renderBubbleHelper(final EntityRendererManager renderManager, final PlayerEntity player, final String text, final MatrixStack stack, final IRenderTypeBuffer buffer, int light) {
        double d0 = renderManager.squareDistanceTo(player);
        if (!(d0 > 2048.0D)) {
            boolean flag = !player.isDiscrete();
            float f = player.getHeight() + 0.85F;
            stack.push();
            stack.translate(0.0D, (double) f, 0.0D);
            stack.rotate(renderManager.getCameraOrientation());
            stack.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = stack.getLast().getMatrix();
            float f1 = ClientHelper.mc.gameSettings.getTextBackgroundOpacity(0.25F);
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
    public static void onInventoryScreen(final GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof DisplayEffectsScreen) {
            DisplayEffectsScreen guiInventory = (DisplayEffectsScreen) event.getGui();
            boolean isCreative = event.getGui() instanceof CreativeScreen;
            int renderX = (TeamConfig.useAlternatePosition || isCreative) ? 2 : guiInventory.getGuiLeft() + 152;
            renderX += TeamConfig.smallIcon ? 5 : 0;
            int renderY = (TeamConfig.useAlternatePosition || isCreative) ? 2 : guiInventory.getGuiTop() + 4;
            int renderWidth = TeamConfig.smallIcon ? 15 : 20;
            int renderHeight = TeamConfig.smallIcon ? 13 : 18;
            ResourceLocation renderLoc = TeamConfig.smallIcon ? new ResourceLocation(TeamsMod.MODID, "textures/gui/buttonsmall.png") : new ResourceLocation(TeamsMod.MODID, "textures/gui/button.png");
            ImageButton guiButtonImage = new ImageButton(renderX, renderY, renderWidth, renderHeight, 0, 0, renderHeight, renderLoc, press -> {
                ClientHelper.mc.displayGuiScreen(new ScreenMain());
            });
            event.addWidget(guiButtonImage);
        }
    }

    /* Handles teams chat tab and button */
    @SubscribeEvent
    public static void onChatScreen(final GuiScreenEvent.InitGuiEvent.Post event) throws IllegalAccessException {
        if (event.getGui() instanceof ChatScreen) {
            MainWindow window = ClientHelper.mc.getMainWindow();
            String myTeam = StorageHelper.getTeam(ClientHelper.mc.player.getUniqueID());
            if (myTeam != null) {
                int defaultWidth = ClientHelper.mc.fontRenderer.getStringWidth("Display: Server Chat");
                ChatButton button = new ChatButton((int) (window.getScaledWidth() * 0.99 - defaultWidth), (int) (window.getScaledHeight() * 0.89), defaultWidth, 10);
                event.addWidget(button);
            } else if (GuiHandler.displayTeamChat) {
                GuiHandler.displayTeamChat = false;
                NewChatGui oldGui = (NewChatGui) GuiHandler.persistentChatGUI.get(ClientHelper.mc.ingameGUI);
                GuiHandler.persistentChatGUI.set(ClientHelper.mc.ingameGUI, GuiHandler.backupChatGUI);
                GuiHandler.backupChatGUI = oldGui;
                NetworkHelper.sendToServer(new MessageTeamChat(ClientHelper.mc.player.getUniqueID(), GuiHandler.displayTeamChat));
            }
        }
    }

    /* Render HUD elements */
    @SubscribeEvent
    public static void renderHUDEvent(final RenderGameOverlayEvent.Post event) {
        //Check if clientside and HUD is enabled
        if (EffectiveSide.get().isClient() &&
                !event.isCancelable() &&
                event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            UUID id = ClientHelper.mc.player.getUniqueID();
            String team = StorageHelper.getTeam(id);
            if (team != null) {
                if (!TeamConfig.disableCompassOverlay && KeyBindHandler.doDisplayCompass) {
                    new CompassOverlay(ClientHelper.mc, team);
                }
                if (!TeamConfig.disableStatusOverlay && KeyBindHandler.doDisplayStatus) {
                    new StatusOverlay(ClientHelper.mc, team);
                }
            }
        }
    }

}