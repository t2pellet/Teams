package com.daposeidonguy.teamsmod.client;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.handlers.ClientEventHandler;
import com.daposeidonguy.teamsmod.team.SaveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class GuiHandler {


    public static Map<UUID,Integer> hungerMap = new HashMap<>();
    public static Map<UUID,Integer> healthMap = new HashMap<>();

    public static GuiHandler instance() {
        return new GuiHandler();
    }

    @SubscribeEvent
    public void showGuiButton(GuiScreenEvent.InitGuiEvent.Post event) {
        if(event.getGui() instanceof GuiInventory) {
            GuiInventory guiInventory = (GuiInventory)event.getGui();
            GuiButtonImage guiButtonImage = new GuiButtonImage(Integer.MIN_VALUE,guiInventory.getGuiLeft()+150,guiInventory.getGuiTop()+5, 20, 18,0,0,18,new ResourceLocation(TeamsMod.MODID,"textures/gui/button.png"));
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
        if (FMLClientHandler.instance().getSide() == Side.CLIENT) {
            Minecraft mc = FMLClientHandler.instance().getClient();
            UUID id = mc.player.getUniqueID();

            if (SaveData.teamMap.containsKey(id) && !event.isCancelable() && event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE && ClientEventHandler.displayHud) {
                String team = SaveData.teamMap.get(id);
                int offsety = 0;
                int count = 0;
                Iterator<UUID> uuidIterator = SaveData.teamsMap.get(team).iterator();
                while(uuidIterator.hasNext() && count<5) {
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
                                String name = p.getDisplayNameString();
                                ResourceLocation loc = p.getLocationSkin();
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