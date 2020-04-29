package com.daposeidonguy.teamsmod.client.gui.team;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.awt.*;

public class GuiTeam extends GuiScreen {

    private static final int WIDTH = 250;
    private static final int HEIGHT = 165;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");
    private static final FontRenderer fontRenderer = FMLClientHandler.instance().getClient().fontRenderer;
    private int guiTop, guiLeft;

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - WIDTH) / 2;
        this.guiTop = (this.height - HEIGHT) / 2;

        this.buttonList.add(new GuiButton(Integer.MIN_VALUE + 1, guiLeft + WIDTH / 2 - 60, guiTop + 40, 120, 20, "Create/Manage Team"));
        this.buttonList.add(new GuiButton(Integer.MIN_VALUE + 2, guiLeft + WIDTH / 2 - 60, guiTop + 70, 120, 20, "List Teams"));
        this.buttonList.add(new GuiButton(Integer.MIN_VALUE + 3, guiLeft + WIDTH / 2 - 60, guiTop + 100, 120, 20, "Transfer Items"));
        this.buttonList.add(new GuiButton(Integer.MIN_VALUE + 4, guiLeft + WIDTH / 2 - 60, guiTop + 130, 120, 20, "Close menu"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
        GuiTeam.fontRenderer.drawString("Teams GUI", guiLeft + WIDTH / 2 - GuiTeam.fontRenderer.getStringWidth("Teams GUI") / 2, guiTop + 10, Color.BLACK.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
