package com.daposeidonguy.teamsmod.client.gui.screen;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.widget.AbstractButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;
import java.io.IOException;

public abstract class AbstractScreenBase extends GuiScreen {

    protected static final int WIDTH = 250;
    protected static final int HEIGHT = 165;
    protected static final int BUTTON_WIDTH = 120;
    protected static final int BUTTON_HEIGHT = 20;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");
    private final AbstractScreenBase parent;
    protected int guiTop, guiLeft;
    protected int CENTERED_X;
    protected int BUTTON_CENTERED_X;
    protected GuiButton goBack;
    private ITextComponent title;

    protected AbstractScreenBase(final ITextComponent titleIn, final AbstractScreenBase parent) {
        this.title = titleIn;
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - WIDTH) / 2;
        this.guiTop = (this.height - HEIGHT) / 2;
        this.CENTERED_X = guiLeft + WIDTH / 2;
        this.BUTTON_CENTERED_X = guiLeft + (WIDTH - BUTTON_WIDTH) / 2;
        goBack = new AbstractButton.Basic(GuiHandler.BUTTON_BACK, BUTTON_CENTERED_X, guiTop + 130, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.button.back"), btn -> {
            mc.displayGuiScreen(parent);
        });
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int state) throws IOException {
        if (goBack.mousePressed(this.mc, mouseX, mouseY)) {
            net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, goBack, this.buttonList);
            if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
                goBack = event.getButton();
                this.selectedButton = goBack;
                goBack.playPressSound(this.mc.getSoundHandler());
                this.actionPerformed(goBack);
                if (this.equals(this.mc.currentScreen))
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
            }
        }
        super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, final float partialTicks) {
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
        for (GuiButton button : this.buttonList) {
            if (button.y < this.guiTop + HEIGHT - 40 && button.y >= this.guiTop + 25) {
                button.drawButton(mc, mouseX, mouseY, partialTicks);
            }
        }
        this.goBack.drawButton(mc, mouseX, mouseY, partialTicks);
        mc.fontRenderer.drawString(title.getFormattedText(), CENTERED_X - mc.fontRenderer.getStringWidth(title.getFormattedText()) / 2, guiTop + 10, Color.BLACK.getRGB());
    }


}
