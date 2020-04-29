package com.daposeidonguy.teamsmod.client.gui.team;

import com.daposeidonguy.teamsmod.client.gui.GuiScrollable;
import com.daposeidonguy.teamsmod.team.SaveData;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class GuiTransferPlayers extends GuiScreen {

    private static final int WIDTH = 250;
    private static final int HEIGHT = 165;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");
    private int guiTop, guiLeft;
    private GuiScrollingList buttonscrollist;


    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - WIDTH) / 2;
        this.guiTop = (this.height - HEIGHT) / 2;

        this.buttonList.add(new GuiButton(Integer.MIN_VALUE + 4, guiLeft + WIDTH / 2 - 60, guiTop + 130, 120, 20, "Go back"));

        List<GuiButton> scrollList = new ArrayList<>();
        int yoffset = 30;
        String name = null;
        if (SaveData.teamMap.containsKey(FMLClientHandler.instance().getClientPlayerEntity().getUniqueID())) {
            name = SaveData.teamMap.get(FMLClientHandler.instance().getClientPlayerEntity().getUniqueID());
        }
        if (name == null) {
            FMLClientHandler.instance().getClient().displayGuiScreen(null);
            FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString("You are not in a team!"));
            return;
        }
        Iterator<UUID> teamIterator = SaveData.teamsMap.get(name).iterator();
        while (teamIterator.hasNext()) {
            UUID uid = teamIterator.next();
            if (mc.world.getPlayerEntityByUUID(uid) != null) {
                String team = mc.world.getPlayerEntityByUUID(uid).getDisplayNameString();
                if (team != mc.player.getDisplayNameString()) {
                    GuiButton button = new GuiButton(Integer.MIN_VALUE + 9, guiLeft + WIDTH / 2 - 60, guiTop + yoffset, 120, 20, team);
                    scrollList.add(button);
                    yoffset += 25;
                }
            }
        }

        buttonscrollist = new GuiScrollable(mc, 242, 100, this.guiTop + 25, this.guiTop + 125, guiLeft + 3, 25, this, scrollList);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
        mc.fontRenderer.drawString("Player List", guiLeft + WIDTH / 2 - mc.fontRenderer.getStringWidth("Player List") / 2, guiTop + 10, Color.BLACK.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
        buttonscrollist.drawScreen(mouseX, mouseY, partialTicks);
    }

}
