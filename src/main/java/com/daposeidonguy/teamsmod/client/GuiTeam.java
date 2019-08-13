package com.daposeidonguy.teamsmod.client;

import com.daposeidonguy.teamsmod.handlers.ClientEventHandler;
import com.daposeidonguy.teamsmod.network.MessageRequestHunger;
import com.daposeidonguy.teamsmod.network.PacketHandler;
import com.daposeidonguy.teamsmod.team.Team;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiTeam extends Gui {

    public GuiTeam(Minecraft mc, int offsety, int offsetx, @Nullable GameProfile profile, int stat,String key) {
        ScaledResolution resolution = new ScaledResolution(mc);
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        if (key.equals("health")) {
            mc.renderEngine.bindTexture(new ResourceLocation("textures/gui/icons.png"));
            drawTexturedModalRect((int) Math.round(width * 0.001) + offsetx + 20, (height / 4 - 5) + offsety, 53, 0, 9, 9);
            drawString(mc.fontRenderer, String.valueOf(stat), (int) Math.round(width * 0.001) + offsetx + 30, (height / 4 - 4) + offsety, Color.WHITE.getRGB());

        } else if (key.equals("hunger")) {
            mc.renderEngine.bindTexture(new ResourceLocation("textures/gui/icons.png"));
            drawTexturedModalRect((int) Math.round(width * 0.001) + offsetx + 46, (height / 4 - 5) + offsety, 16, 36, 9, 9);
            drawString(mc.fontRenderer, String.valueOf(stat), (int) Math.round(width * 0.001) + offsetx + 58, (height / 4 - 5) + offsety, Color.WHITE.getRGB());
        } else {
            mc.renderEngine.bindTexture(((AbstractClientPlayer)mc.player.world.getPlayerEntityByUUID(profile.getId())).getLocationSkin());
            GL11.glPushMatrix();
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            drawTexturedModalRect((int) Math.round(width * 0.001) + offsetx + 4, (height / 2 - 16) + 2 * offsety, 32, 32, 32, 32);
            GL11.glPopMatrix();
            drawString(mc.fontRenderer,profile.getName(), (int) Math.round(width * 0.001) + offsetx + 20, (height / 4 - 4) + offsety, Color.WHITE.getRGB());
        }
    }


    public static class GuiHandler {

        public static Map<UUID,Integer> hungerMap = new HashMap<>();

        public static GuiHandler instance() {
            return new GuiHandler();
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
                                new GuiTeam(mc, offsety+16, 0, null,MathHelper.ceil(clientP.getHealth()),"health");
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
}
