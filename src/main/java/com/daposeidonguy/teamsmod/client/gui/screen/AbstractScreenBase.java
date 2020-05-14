package com.daposeidonguy.teamsmod.client.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

public abstract class AbstractScreenBase extends Screen {

    protected static final int WIDTH = 250;
    protected static final int HEIGHT = 165;
    protected static final int BUTTON_WIDTH = 120;
    protected static final int BUTTON_HEIGHT = 20;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");
    private final AbstractScreenBase parent;
    protected int guiTop, guiLeft;
    protected int CENTERED_X;
    protected int BUTTON_CENTERED_X;
    protected Button goBack;

    protected AbstractScreenBase(final ITextComponent titleIn, final AbstractScreenBase parent) {
        super(titleIn);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - WIDTH) / 2;
        this.guiTop = (this.height - HEIGHT) / 2;
        this.CENTERED_X = guiLeft + WIDTH / 2;
        this.BUTTON_CENTERED_X = guiLeft + (WIDTH - BUTTON_WIDTH) / 2;
        goBack = new Button(BUTTON_CENTERED_X, guiTop + 130, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.button.back"), btn -> minecraft.displayGuiScreen(parent));
        children.add(goBack);
    }

    @Override
    public void render(int mouseX, int mouseY, final float partialTicks) {
        assert minecraft != null;
        renderBackground();
        minecraft.getTextureManager().bindTexture(BACKGROUND);
        blit(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
        for (Widget button : this.buttons) {
            if (button.y < this.guiTop + HEIGHT - 40 && button.y >= this.guiTop + 25) {
                button.render(mouseX, mouseY, partialTicks);
            }
        }
        this.goBack.render(mouseX, mouseY, partialTicks);
        this.font.drawString(title.getFormattedText(), CENTERED_X - font.getStringWidth(title.getFormattedText()) / 2, guiTop + 10, Color.BLACK.getRGB());
    }


}
