package com.daposeidonguy.teamsmod.client;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.client.gui.GuiHud;
import com.daposeidonguy.teamsmod.client.gui.team.GuiTeam;
import com.daposeidonguy.teamsmod.client.gui.team.GuiTeamList;
import com.daposeidonguy.teamsmod.client.gui.team.GuiTeamManager;
import com.daposeidonguy.teamsmod.client.gui.team.GuiTransferPlayers;
import com.daposeidonguy.teamsmod.handlers.ClientEventHandler;
import com.daposeidonguy.teamsmod.handlers.ConfigHandler;
import com.daposeidonguy.teamsmod.inventory.ContainerTransfer;
import com.daposeidonguy.teamsmod.inventory.GuiTransfer;
import com.daposeidonguy.teamsmod.network.MessageGui;
import com.daposeidonguy.teamsmod.network.PacketHandler;
import com.daposeidonguy.teamsmod.team.SaveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class GuiHandler {
    public static Map<UUID, Integer> hungerMap = new HashMap<>();
    public static Map<UUID, Integer> healthMap = new HashMap<>();

    public static Method renderName = ReflectionHelper.findMethod(Render.class, "renderLivingLabel", "func_147906_a", Entity.class, String.class, Double.TYPE, Double.TYPE, Double.TYPE, Integer.TYPE);

    public static GuiHandler instance() {
        return new GuiHandler();
    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Pre event) throws InvocationTargetException, IllegalAccessException {
        String playerName = event.getEntityPlayer().getDisplayNameString();
        if (!ConfigHandler.client.disableChatBubble && ClientEventHandler.chatMap.containsKey(playerName)) {
            String localName = FMLClientHandler.instance().getClientPlayerEntity().getName();
            if (!localName.equals(playerName)) {
                String text = ClientEventHandler.chatMap.get(playerName).first();
                long tick = ClientEventHandler.chatMap.get(playerName).second();
                if ((ClientEventHandler.ticks - tick) < 200) {
                    GuiHandler.renderName.invoke(event.getRenderer(), event.getEntityPlayer(), text, event.getX(), event.getY() + 0.5, event.getZ(), 64);
                } else {
                    ClientEventHandler.chatMap.remove(playerName);
                }
            }
        }
    }

    @SubscribeEvent
    public void showGuiButton(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiInventory) {
            GuiInventory guiInventory = (GuiInventory) event.getGui();
            GuiButtonImage guiButtonImage;
            if (!ConfigHandler.client.smallIcon) {
                if (ConfigHandler.client.useAlternatePosition) {
                    guiButtonImage = new GuiButtonImage(Integer.MIN_VALUE, 4, 4, 20, 18, 0, 0, 18, new ResourceLocation(TeamsMod.MODID, "textures/gui/button.png"));
                } else {
                    guiButtonImage = new GuiButtonImage(Integer.MIN_VALUE, guiInventory.getGuiLeft() + 152, guiInventory.getGuiTop() + 4, 20, 18, 0, 0, 18, new ResourceLocation(TeamsMod.MODID, "textures/gui/button.png"));
                }
            } else {
                if (ConfigHandler.client.useAlternatePosition) {
                    guiButtonImage = new GuiButtonImage(Integer.MIN_VALUE, 2, 2, 15, 14, 0, 0, 13, new ResourceLocation(TeamsMod.MODID, "textures/gui/buttonsmall.png"));
                } else {
                    guiButtonImage = new GuiButtonImage(Integer.MIN_VALUE, guiInventory.getGuiLeft() + 155, guiInventory.getGuiTop() + 5, 15, 14, 0, 0, 13, new ResourceLocation(TeamsMod.MODID, "textures/gui/buttonsmall.png"));
                }
            }
            guiButtonImage.displayString = "team";
            event.getButtonList().add(guiButtonImage);
        } else if (event.getGui() instanceof GuiContainerCreative) {
            GuiButtonImage guiButtonImage;
            if (!ConfigHandler.client.smallIcon) {
                guiButtonImage = new GuiButtonImage(Integer.MIN_VALUE, 4, 4, 20, 18, 0, 0, 18, new ResourceLocation(TeamsMod.MODID, "textures/gui/button.png"));

            } else {
                guiButtonImage = new GuiButtonImage(Integer.MIN_VALUE, 2, 2, 15, 14, 0, 0, 13, new ResourceLocation(TeamsMod.MODID, "textures/gui/buttonsmall.png"));
            }
            guiButtonImage.displayString = "team";
            event.getButtonList().add(guiButtonImage);
        }
    }

    @SubscribeEvent
    public void buttonClick(GuiScreenEvent.ActionPerformedEvent event) {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            switch (event.getButton().id) {
                case Integer.MIN_VALUE:
                    FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeam());
                    break;
                case Integer.MIN_VALUE + 4:
                    if (event.getGui() instanceof GuiTeam) {
                        FMLClientHandler.instance().getClient().displayGuiScreen(null);
                    } else {
                        FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeam());
                    }
                    break;
                case Integer.MIN_VALUE + 2:
                    FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeamList());
                    break;
                case Integer.MIN_VALUE + 1:
                    if (!SaveData.teamMap.containsKey(FMLClientHandler.instance().getClientPlayerEntity().getUniqueID())) {
                        FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeamManager.GuiTeamCreator());

                    } else {
                        FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeamManager.GuiTeamEditor(SaveData.teamMap.get(FMLClientHandler.instance().getClientPlayerEntity().getUniqueID())));
                    }
                    break;
                case Integer.MIN_VALUE + 7:
                    FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage("/teamsmod leave");
                    FMLClientHandler.instance().getClient().displayGuiScreen(null);
                    break;
                case Integer.MIN_VALUE + 3:
                    FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTransferPlayers());
                    break;
                case Integer.MIN_VALUE + 9:
                    EntityPlayerSP p = FMLClientHandler.instance().getClientPlayerEntity();
                    FMLClientHandler.instance().getClient().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    if (!ConfigHandler.server.disableInventoryTransfer) {
                        FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTransfer(new ContainerTransfer(FMLClientHandler.instance().getClientPlayerEntity().inventory, event.getButton().displayString), FMLClientHandler.instance().getClientPlayerEntity().inventory));
                        if (p != null && p.getEntityWorld().isRemote) {
                            PacketHandler.INSTANCE.sendToServer(new MessageGui(p.getUniqueID(), event.getButton().displayString));
                        }
                    } else {
                        FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString("That feature is disabled"));
                        FMLClientHandler.instance().getClient().displayGuiScreen(null);
                    }
            }
        }
    }

    @SubscribeEvent
    public void renderHUDEvent(RenderGameOverlayEvent.Post event) {
        //Check if clientside and HUD is enabled
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT &&
                !ConfigHandler.client.disableTeamsHUD &&
                ClientEventHandler.displayHud &&
                !event.isCancelable() &&
                event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            Minecraft mc = FMLClientHandler.instance().getClient();
            UUID id = mc.player.getUniqueID();
            String team = SaveData.teamMap.get(id);
            if (team != null) {
                int offsety = 0;
                int count = 0;
                Iterator<UUID> uuidIterator = SaveData.teamsMap.get(team).iterator();
                //Iterate through players in team (up to 4 of them)
                while (uuidIterator.hasNext() && count < 4) {
                    UUID uid = uuidIterator.next();
                    //Dont render players own health and hunger
                    if (!uid.equals(id)) {
                        NetworkPlayerInfo info = Minecraft.getMinecraft().player.connection.getPlayerInfo(uid);
                        if (info == null) {
                            return;
                        }
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
                        new GuiHud(mc, offsety, health, hunger, name, skin);
                        offsety += 46;
                        count += 1;
                    }
                }
            }
        }
    }

}