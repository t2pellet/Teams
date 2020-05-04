package com.daposeidonguy.teamsmod.client.gui;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.ClientEventHandler;
import com.daposeidonguy.teamsmod.client.ClientUtils;
import com.daposeidonguy.teamsmod.client.gui.overlay.OverlayTeam;
import com.daposeidonguy.teamsmod.client.gui.screen.team.ScreenTeam;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.network.play.NetworkPlayerInfo;
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

    private static void renderChat(EntityRendererManager renderManager, PlayerEntity player, String text, MatrixStack stack, IRenderTypeBuffer buffer, int light) {
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

    @SubscribeEvent
    public static void renderPlayer(RenderPlayerEvent.Pre event) {
        String playerName = event.getPlayer().getGameProfile().getName();
        String localName = Minecraft.getInstance().player.getGameProfile().getName();
        if (!localName.equals(playerName) && ClientEventHandler.chatMap.containsKey(playerName)) {
            String text = ClientEventHandler.chatMap.get(playerName).getFirst();
            long tick = ClientEventHandler.chatMap.get(playerName).getSecond();
            if ((ClientEventHandler.ticks - tick) < 200) {
                renderChat(event.getRenderer().getRenderManager(), event.getPlayer(), text, event.getMatrixStack(), event.getBuffers(), event.getLight());
            } else {
                ClientEventHandler.chatMap.remove(playerName);
            }
        }
    }

    @SubscribeEvent
    public static void showGuiButton(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof InventoryScreen) {
            InventoryScreen guiInventory = (InventoryScreen) event.getGui();
            ImageButton guiButtonImage;
            if (!TeamConfig.smallIcon) {
                if (TeamConfig.useAlternatePosition) {
                    guiButtonImage = new ImageButton(4, 4, 20, 18, 0, 0, 18, new ResourceLocation(TeamsMod.MODID, "textures/gui/button.png"), (pressable) -> {
                        Minecraft.getInstance().displayGuiScreen(new ScreenTeam());
                    });
                } else {
                    guiButtonImage = new ImageButton(guiInventory.getGuiLeft() + 152, guiInventory.getGuiTop() + 4, 20, 18, 0, 0, 18, new ResourceLocation(TeamsMod.MODID, "textures/gui/button.png"), (pressable) -> {
                        Minecraft.getInstance().displayGuiScreen(new ScreenTeam());
                    });
                }
            } else {
                if (TeamConfig.useAlternatePosition) {
                    guiButtonImage = new ImageButton(2, 2, 15, 14, 0, 0, 13, new ResourceLocation(TeamsMod.MODID, "textures/gui/buttonsmall.png"), (pressable) -> {
                        Minecraft.getInstance().displayGuiScreen(new ScreenTeam());
                    });
                } else {
                    guiButtonImage = new ImageButton(guiInventory.getGuiLeft() + 155, guiInventory.getGuiTop() + 5, 15, 14, 0, 0, 13, new ResourceLocation(TeamsMod.MODID, "textures/gui/buttonsmall.png"), (pressable) -> {
                        Minecraft.getInstance().displayGuiScreen(new ScreenTeam());
                    });
                }
            }
            event.addWidget(guiButtonImage);
        } else if (event.getGui() instanceof CreativeScreen) {
            ImageButton guiButtonImage;
            if (!TeamConfig.smallIcon) {
                guiButtonImage = new ImageButton(4, 4, 20, 18, 0, 0, 18, new ResourceLocation(TeamsMod.MODID, "textures/gui/button.png"), (pressable) -> {
                    Minecraft.getInstance().displayGuiScreen(new ScreenTeam());
                });

            } else {
                guiButtonImage = new ImageButton(2, 2, 15, 14, 0, 0, 13, new ResourceLocation(TeamsMod.MODID, "textures/gui/buttonsmall.png"), (pressable) -> {
                    Minecraft.getInstance().displayGuiScreen(new ScreenTeam());
                });
            }
            event.addWidget(guiButtonImage);
        }
    }

    private static boolean renderPlayerHUD(UUID uuid, int offsety) {
        Minecraft mc = Minecraft.getInstance();
        NetworkPlayerInfo info = mc.player.connection.getPlayerInfo(uuid);
        if (info == null) {
            return false;
        }
        int health;
        try {
            health = healthMap.get(uuid);
        } catch (NullPointerException ex) {
            health = 20;
        }
        if (health < 0) {
            return false;
        }
        int hunger;
        try {
            hunger = hungerMap.get(uuid);
        } catch (NullPointerException ex) {
            hunger = 20;
        }
        String name = ClientUtils.getOnlineUsernameFromUUID(uuid);
        ResourceLocation skin = info.getLocationSkin();
        new OverlayTeam(mc, offsety, health, hunger, name, skin);
        return true;
    }

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
                int offsety = 0;
                int count = 0;
                Iterator<UUID> priorityIterator = priorityPlayers.iterator();
                while (priorityIterator.hasNext() && count < 4) {
                    UUID uuid = priorityIterator.next();
                    renderPlayerHUD(uuid, offsety);
                    offsety += 46;
                    count += 1;
                }
                Iterator<UUID> teamIterator = SaveData.teamsMap.get(team).iterator();
                while (teamIterator.hasNext() && count < 4) {
                    UUID uuid = teamIterator.next();
                    if (!priorityPlayers.contains(uuid)) {
                        if (!uuid.equals(id)) {
                            if (renderPlayerHUD(uuid, offsety)) {
                                offsety += 46;
                                count += 1;
                            }
                        }
                    }
                }
            }
        }
    }

}