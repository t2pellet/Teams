package com.daposeidonguy.teamsmod.client;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.handlers.ClientEventHandler;
import com.daposeidonguy.teamsmod.network.MessageRequestHunger;
import com.daposeidonguy.teamsmod.network.PacketHandler;
import com.daposeidonguy.teamsmod.team.Team;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiHandler {

    public static Map<UUID,Integer> hungerMap = new HashMap<>();

    public static GuiHandler instance() {
        return new GuiHandler();
    }

    @SubscribeEvent
    public void otherEvent(GuiScreenEvent.InitGuiEvent.Post event) {
        if(event.getGui() instanceof GuiInventory) {
            GuiInventory guiInventory = (GuiInventory)event.getGui();
            GuiButtonImage guiButtonImage = new GuiButtonImage(Integer.MIN_VALUE,guiInventory.getGuiLeft()+125,guiInventory.height / 2 - 22, 20, 18,0,0,18,new ResourceLocation(TeamsMod.MODID,"textures/gui/button.png"));
            guiButtonImage.displayString="team";
            event.getButtonList().add(guiButtonImage);
        }
    }

    @SubscribeEvent
    public void anotherEvent(GuiScreenEvent.ActionPerformedEvent event) {
        if (FMLCommonHandler.instance().getSide()== Side.CLIENT) {
            switch(event.getButton().id) {
                case Integer.MIN_VALUE:
                    FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeamEditor());
                    break;
                case Integer.MIN_VALUE+4:
                    FMLClientHandler.instance().getClient().displayGuiScreen(null);
                    break;
                case Integer.MIN_VALUE+2:
                    FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeamEditor.GuiTeamList());
                    break;
                case Integer.MIN_VALUE+1:
                    if(Team.getTeam(FMLClientHandler.instance().getClientPlayerEntity().getUniqueID())==null) {
                        FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeamEditor.GuiTeamManager());
                    } else {
                        FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeamEditor.GuiTeamManager1());
                    }
                    break;
            }

        }
    }

    @SubscribeEvent
    public void RenderGuiEvent(RenderGameOverlayEvent.Post event) {
        if (FMLClientHandler.instance().getSide() == Side.CLIENT) {
            Minecraft mc = FMLClientHandler.instance().getClient();
            UUID id = mc.player.getUniqueID();
            Team team = Team.getTeam(id);
            if (team != null && !event.isCancelable() && event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE && ClientEventHandler.displayHud) {
                int offsety = 0;
                for (UUID uid : team.getPlayers()) {
                    if(!uid.equals(id)) {
                        PacketHandler.INSTANCE.sendToServer(new MessageRequestHunger(id,uid));
                        AbstractClientPlayer clientP = (AbstractClientPlayer) mc.player.world.getPlayerEntityByUUID(uid);
                        if(clientP!=null) {
                            new GuiTeam(mc, offsety+16, 0, null, MathHelper.ceil(clientP.getHealth()),"health");
                            if(hungerMap.containsKey(uid)) {
                                new GuiTeam(Minecraft.getMinecraft(), offsety+16, 0, null, hungerMap.get(uid),"hunger");
                            }
                            new GuiTeam(mc, offsety, 0,clientP.getGameProfile(), 0,"profile");
                            offsety += 46;
                        }
                    }
                }
            }
        }
    }
}