package com.daposeidonguy.teamsmod.client.gui;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.ClientEventHandler;
import com.daposeidonguy.teamsmod.client.gui.overlay.OverlayTeam;
import com.daposeidonguy.teamsmod.client.gui.screen.GuiTeam;
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
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class GuiHandler {
    public static Map<UUID, Integer> hungerMap = new HashMap<>();
    public static Map<UUID, Integer> healthMap = new HashMap<>();


    public static GuiHandler instance() {
        return new GuiHandler();
    }

    private void renderChat(EntityRendererManager renderManager, PlayerEntity player, String text, MatrixStack stack, IRenderTypeBuffer buffer, int light) {
        double d0 = renderManager.squareDistanceTo(player);
        if (!(d0 > 4096.0D)) {
            boolean flag = !player.isDiscrete();
            float f = player.getHeight() + 0.75F;
            stack.push();
            stack.translate(0.0D, (double)f, 0.0D);
            stack.rotate(renderManager.getCameraOrientation());
            stack.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = stack.getLast().getMatrix();
            float f1 = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F);
            int j = (int)(f1 * 255.0F) << 24;
            FontRenderer fontrenderer = renderManager.getFontRenderer();
            float f2 = (float)(-fontrenderer.getStringWidth(text) / 2);
            fontrenderer.renderString(text, f2, 0, 553648127, false, matrix4f, buffer, flag, j, light);
            if (flag) {
                fontrenderer.renderString(text, f2, 0, -1, false, matrix4f, buffer, false, 0, light);
            }
            stack.pop();
        }
    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Pre event) {
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
    public void showGuiButton(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof InventoryScreen) {
            InventoryScreen guiInventory = (InventoryScreen) event.getGui();
            ImageButton guiButtonImage;
            if (!TeamConfig.smallIcon) {
                if (TeamConfig.useAlternatePosition) {
                    guiButtonImage = new ImageButton(4, 4, 20, 18, 0, 0, 18, new ResourceLocation(TeamsMod.MODID, "textures/gui/button.png"), (pressable) -> {
                        Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                        Minecraft.getInstance().displayGuiScreen(new GuiTeam(new StringTextComponent("Team")));
                    });
                } else {
                    guiButtonImage = new ImageButton(guiInventory.getGuiLeft() + 152, guiInventory.getGuiTop() + 4, 20, 18, 0, 0, 18, new ResourceLocation(TeamsMod.MODID, "textures/gui/button.png"), (pressable) -> {
                        Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                        Minecraft.getInstance().displayGuiScreen(new GuiTeam(new StringTextComponent("Team")));
                    });
                }
            } else {
                if (TeamConfig.useAlternatePosition) {
                    guiButtonImage = new ImageButton(2, 2, 15, 14, 0, 0, 13, new ResourceLocation(TeamsMod.MODID, "textures/gui/buttonsmall.png"), (pressable) -> {
                        Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                        Minecraft.getInstance().displayGuiScreen(new GuiTeam(new StringTextComponent("Team")));
                    });
                } else {
                    guiButtonImage = new ImageButton(guiInventory.getGuiLeft() + 155, guiInventory.getGuiTop() + 5, 15, 14, 0, 0, 13, new ResourceLocation(TeamsMod.MODID, "textures/gui/buttonsmall.png"), (pressable) -> {
                        Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                        Minecraft.getInstance().displayGuiScreen(new GuiTeam(new StringTextComponent("Team")));
                    });
                }
            }
            event.addWidget(guiButtonImage);
        } else if (event.getGui() instanceof CreativeScreen) {
            ImageButton guiButtonImage;
            if (!TeamConfig.smallIcon) {
                guiButtonImage = new ImageButton(4, 4, 20, 18, 0, 0, 18, new ResourceLocation(TeamsMod.MODID, "textures/gui/button.png"), (pressable) -> {
                    Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                    Minecraft.getInstance().displayGuiScreen(new GuiTeam(new StringTextComponent("Team")));
                });

            } else {
                guiButtonImage = new ImageButton(2, 2, 15, 14, 0, 0, 13, new ResourceLocation(TeamsMod.MODID, "textures/gui/buttonsmall.png"), (pressable) -> {
                    Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                    Minecraft.getInstance().displayGuiScreen(new GuiTeam(new StringTextComponent("Team")));
                });
            }
            event.addWidget(guiButtonImage);
        }
    }

    @SubscribeEvent
    public void renderHUDEvent(RenderGameOverlayEvent.Post event) {
        //Check if clientside and HUD is enabled
        if (EffectiveSide.get().isClient() &&
                !TeamConfig.disableTeamsHUD &&
                ClientEventHandler.displayHud &&
                !event.isCancelable() &&
                event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            Minecraft mc = Minecraft.getInstance();
            UUID id = mc.player.getUniqueID();
            String team = SaveData.teamMap.get(id);
            if (team != null) {
                int offsety = 0;
                int count = 0;
                Iterator<UUID> uuidIterator = SaveData.teamsMap.get(team).iterator();

                //Iterate through players in storage (up to 4 of them)
                while (uuidIterator.hasNext() && count < 4) {
                    UUID uid = uuidIterator.next();
                    //Dont render players own health and hunger
                    if (!uid.equals(id) && (mc.world.getPlayerByUuid(uid) != null)) {
                        NetworkPlayerInfo info = mc.player.connection.getPlayerInfo(uid);
                        int health;
                        try {
                            health = healthMap.get(uid);
                        } catch (NullPointerException ex) {
                            health = 20;
                        }
                        int hunger;
                        try {
                            hunger = hungerMap.get(uid);
                        } catch (NullPointerException ex) {
                            hunger = 20;
                        }
                        String name = ClientEventHandler.idtoNameMap.get(uid);
                        if (name == null) {
                            name = info.getGameProfile().getName();
                        }
                        ResourceLocation skin = info.getLocationSkin();
                        new OverlayTeam(mc, offsety, health, hunger, name, skin);
                        offsety += 46;
                        count += 1;
                    }
                }
            }
        }
    }

}