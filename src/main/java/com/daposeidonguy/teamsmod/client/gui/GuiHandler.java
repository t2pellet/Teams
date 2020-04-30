package com.daposeidonguy.teamsmod.client.gui;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.ClientEventHandler;
import com.daposeidonguy.teamsmod.client.gui.overlay.OverlayTeam;
import com.daposeidonguy.teamsmod.client.gui.screen.GuiTeam;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class GuiHandler {
    public static Map<UUID, Integer> hungerMap = new HashMap<>();
    public static Map<UUID, Integer> healthMap = new HashMap<>();

    public static Method renderName = ObfuscationReflectionHelper.findMethod(EntityRenderer.class, "func_225629_a_", Entity.class, String.class, MatrixStack.class, IRenderTypeBuffer.class, Integer.TYPE);

    public static GuiHandler instance() {
        return new GuiHandler();
    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Pre event) throws InvocationTargetException, IllegalAccessException {
        String playerName = event.getPlayer().getGameProfile().getName();
        if (!TeamConfig.disableChatBubble && ClientEventHandler.chatMap.containsKey(playerName)) {
            String localName = Minecraft.getInstance().player.getGameProfile().getName();
            if (!localName.equals(playerName)) {
                String text = ClientEventHandler.chatMap.get(playerName).getFirst();
                long tick = ClientEventHandler.chatMap.get(playerName).getSecond();
                if ((ClientEventHandler.ticks - tick) < 200) {
                    BlockPos pos = event.getPlayer().getPosition();
                    MatrixStack stack = event.getMatrixStack();
                    stack.scale(0, 0.5f, 0);
                    GuiHandler.renderName.invoke(event.getRenderer(), event.getPlayer(), text, event.getMatrixStack(), event.getBuffers(), 64);
                } else {
                    ClientEventHandler.chatMap.remove(playerName);
                }
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