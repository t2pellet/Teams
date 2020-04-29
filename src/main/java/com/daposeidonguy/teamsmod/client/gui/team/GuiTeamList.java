package com.daposeidonguy.teamsmod.client.gui.team;

import com.daposeidonguy.teamsmod.client.gui.GuiScrollable;
import com.daposeidonguy.teamsmod.team.SaveData;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GuiTeamList extends GuiScreen {

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
        Iterator<String> teamIterator = SaveData.teamsMap.keySet().iterator();
        while (teamIterator.hasNext()) {
            String team = teamIterator.next();
            GuiButton button = new GuiButton(Integer.MIN_VALUE + 8, guiLeft + WIDTH / 2 - 60, guiTop + yoffset, 120, 20, team);
            scrollList.add(button);
            yoffset += 25;
        }

        buttonscrollist = new GuiScrollable(mc, 242, 100, this.guiTop + 25, this.guiTop + 125, guiLeft + 3, 25, this, scrollList);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
        mc.fontRenderer.drawString("Teams List", guiLeft + WIDTH / 2 - mc.fontRenderer.getStringWidth("Teams List") / 2, guiTop + 10, Color.BLACK.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
        buttonscrollist.drawScreen(mouseX, mouseY, partialTicks);
    }

}
