package com.daposeidonguy.teamsmod.client;

import com.daposeidonguy.teamsmod.handlers.ClientEventHandler;
import com.daposeidonguy.teamsmod.network.MessageRequest;
import com.daposeidonguy.teamsmod.network.PacketHandler;
import com.daposeidonguy.teamsmod.team.Team;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.UUID;

public class GuiTeam extends Gui {

    public static int hunger = 20;

    public GuiTeam(Minecraft mc, String text, int offsety, int offsetx, @Nullable ResourceLocation profile) {
        ScaledResolution resolution = new ScaledResolution(mc);
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        if(profile==null) {
            mc.renderEngine.bindTexture(new ResourceLocation("textures/gui/icons.png"));
            drawTexturedModalRect((int) Math.round(width * 0.001) + offsetx + 20, (height / 4 - 5) + offsety, 53, 0, 9, 9);
            drawString(mc.fontRenderer,text, (int) Math.round(width*0.001)+offsetx+30,(height/4-4)+offsety, Color.WHITE.getRGB());
            mc.renderEngine.bindTexture(new ResourceLocation("textures/gui/icons.png"));
            drawTexturedModalRect((int)Math.round(width*0.001)+offsetx+46,(height/4-5)+offsety,16,36,9,9);
            drawString(mc.fontRenderer,String.valueOf(MathHelper.ceil(GuiTeam.hunger)),(int)Math.round(width*0.001)+offsetx+58,(height/4-5)+offsety,Color.WHITE.getRGB());
        } else {
            mc.renderEngine.bindTexture(profile);
            GL11.glPushMatrix();
            GL11.glScalef(0.5F,0.5F,0.5F);
            drawTexturedModalRect((int) Math.round(width * 0.001) + offsetx + 4, (height / 2 - 16) + 2*offsety,32 , 32, 32,32);
            GL11.glPopMatrix();
            drawString(mc.fontRenderer,text, (int) Math.round(width*0.001)+offsetx+20,(height/4-4)+offsety, Color.WHITE.getRGB());
        }
    }


    public static class GuiHandler {

        public static GuiHandler instance() {
            return new GuiHandler();
        }

        @SubscribeEvent
        public void RenderGuiEvent(RenderGameOverlayEvent.Post event) {
            Minecraft mc = Minecraft.getMinecraft();
            if (!event.isCancelable() && event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE && ClientEventHandler.displayHud) {
                int offsety = 0;
                UUID id = mc.getConnection().getGameProfile().getId();
                Team team = Team.getTeam(id);
                if (team!=null) {
                    for (UUID uid : team.getPlayers()) {
                        for(EntityPlayer p : mc.player.getEntityWorld().playerEntities) {
                            if(p.getUniqueID().equals(uid) && !uid.equals(id)) {
                                PacketHandler.INSTANCE.sendToServer(new MessageRequest(id,uid));
                                AbstractClientPlayer clientP = (AbstractClientPlayer)p;
                                new GuiTeam(mc,p.getDisplayNameString(),offsety,0,clientP.getLocationSkin());
                                new GuiTeam(mc,String.valueOf(MathHelper.ceil(p.getHealth())),offsety+16,0,null);
                                offsety+=46;
                            }
                        }
                    }
                }
            }
        }
    }
}
