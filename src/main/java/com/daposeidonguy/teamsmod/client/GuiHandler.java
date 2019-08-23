package com.daposeidonguy.teamsmod.client;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.handlers.ClientEventHandler;
import com.daposeidonguy.teamsmod.handlers.ConfigHandler;
import com.daposeidonguy.teamsmod.team.SaveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
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
    public static Map<UUID,Integer> hungerMap = new HashMap<>();
    public static Map<UUID,Integer> healthMap = new HashMap<>();

    public static Method renderName = ReflectionHelper.findMethod(Render.class,"renderLivingLabel","func_147906_a",Entity.class, String.class,Double.TYPE,Double.TYPE,Double.TYPE,Integer.TYPE);

    public static GuiHandler instance() {
        return new GuiHandler();
    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Pre event) {
        if(!ConfigHandler.client.disableChatBubble && ClientEventHandler.chatMap.containsKey(event.getEntityPlayer().getDisplayNameString())) {
            EntityPlayerSP local = FMLClientHandler.instance().getClientPlayerEntity();
            if (!local.getUniqueID().equals(event.getEntityPlayer().getUniqueID())) {
                String text = ClientEventHandler.chatMap.get(event.getEntityPlayer().getDisplayNameString()).first();
                long tick = ClientEventHandler.chatMap.get(event.getEntityPlayer().getDisplayNameString()).second();
                if((ClientEventHandler.ticks - tick) < 200) {
                    try {
                        GuiHandler.renderName.invoke(event.getRenderer(),event.getEntityPlayer(),text,event.getX(),event.getY()+0.5,event.getZ(),64);
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    ClientEventHandler.chatMap.remove(event.getEntityPlayer().getDisplayNameString());
                }
            }
        }
    }

    @SubscribeEvent
    public void showGuiButton(GuiScreenEvent.InitGuiEvent.Post event) {
        if(event.getGui() instanceof GuiInventory) {
            GuiInventory guiInventory = (GuiInventory)event.getGui();
            GuiButtonImage guiButtonImage;
            if(!ConfigHandler.client.smallIcon) {
                guiButtonImage = new GuiButtonImage(Integer.MIN_VALUE,guiInventory.getGuiLeft()+150,guiInventory.getGuiTop()+5, 20, 18,0,0,18,new ResourceLocation(TeamsMod.MODID,"textures/gui/button.png"));
            } else {
                guiButtonImage = new GuiButtonImage(Integer.MIN_VALUE,guiInventory.getGuiLeft()+155,guiInventory.getGuiTop()+5, 15, 14,0,0,13,new ResourceLocation(TeamsMod.MODID,"textures/gui/buttonsmall.png"));
            }
            guiButtonImage.displayString="team";
            event.getButtonList().add(guiButtonImage);
        }
    }

    @SubscribeEvent
    public void buttonClick(GuiScreenEvent.ActionPerformedEvent event) {
        if (FMLCommonHandler.instance().getSide()== Side.CLIENT) {
            switch(event.getButton().id) {
                case Integer.MIN_VALUE:
                    FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeamEditor());
                    break;
                case Integer.MIN_VALUE+4:
                    if(event.getGui() instanceof GuiTeamEditor) {
                        FMLClientHandler.instance().getClient().displayGuiScreen(null);
                    } else {
                        FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeamEditor());
                    }
                    break;
                case Integer.MIN_VALUE+2:
                    FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeamEditor.GuiTeamList());
                    break;
                case Integer.MIN_VALUE+1:
                    if(!SaveData.teamMap.containsKey(FMLClientHandler.instance().getClientPlayerEntity().getUniqueID())) {
                        FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeamEditor.GuiTeamManager());

                    } else {
                        FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeamEditor.GuiTeamManager1(SaveData.teamMap.get(FMLClientHandler.instance().getClientPlayerEntity().getUniqueID())));
                    }
                    break;
                case Integer.MIN_VALUE+7:
                    FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage("/team leave");
                    FMLClientHandler.instance().getClient().displayGuiScreen(null);
                    break;
                case Integer.MIN_VALUE+3:
                    FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeamEditor.GuiPlayerList());
            }
        }
    }

    @SubscribeEvent
    public void RenderGuiEvent(RenderGameOverlayEvent.Post event) {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT && !ConfigHandler.client.disableTeamsHUD) {
            Minecraft mc = FMLClientHandler.instance().getClient();
            UUID id = mc.player.getUniqueID();

            if (SaveData.teamMap.containsKey(id) && !event.isCancelable() && event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE && ClientEventHandler.displayHud) {
                String team = SaveData.teamMap.get(id);
                int offsety = 0;
                int count = 0;
                Iterator<UUID> uuidIterator = SaveData.teamsMap.get(team).iterator();
                while(uuidIterator.hasNext() && count<4) {
                    UUID uid = uuidIterator.next();
                    if(!uid.equals(id)) {
                        int hunger = 20;
                        if (hungerMap.containsKey(uid)) {
                            hunger = hungerMap.get(uid);
                        }
                        int health = 0;
                        if (healthMap.containsKey(uid)) {
                            health = healthMap.get(uid);
                        }
                        if(health!=0) {
                            AbstractClientPlayer p = (AbstractClientPlayer)mc.world.getPlayerEntityByUUID(uid);
                            if(p!=null) {
                                String name = ClientEventHandler.idtoNameMap.get(uid);
                                ResourceLocation loc;
                                if(ClientEventHandler.skinMap.containsKey(name)) {
                                    loc = ClientEventHandler.skinMap.get(name);
                                } else {
                                    loc = Minecraft.getMinecraft().getConnection().getPlayerInfo(uid).getLocationSkin();
                                }
                                new GuiTeam(mc, offsety, health, hunger, name, loc);
                                offsety += 46;
                                count+=1;
                            }
                        }
                    }
                }
            }
        }
    }
}