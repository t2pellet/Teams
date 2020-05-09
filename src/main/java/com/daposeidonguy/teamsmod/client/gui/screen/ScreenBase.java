package com.daposeidonguy.teamsmod.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenBase extends Screen {

    protected static final int WIDTH = 250;
    protected static final int HEIGHT = 165;
    protected static final int BUTTON_WIDTH = 120;
    protected static final int BUTTON_HEIGHT = 20;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");

    protected int guiTop, guiLeft;
    protected int BUTTON_CENTERED_X;
    protected Button goBack;
    protected ScreenBase parent;

    protected ScreenBase(ITextComponent titleIn, ScreenBase parent) {
        super(titleIn);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - WIDTH) / 2;
        this.guiTop = (this.height - HEIGHT) / 2;
        this.BUTTON_CENTERED_X = guiLeft + (WIDTH - BUTTON_WIDTH) / 2;
        goBack = new Button(BUTTON_CENTERED_X, guiTop + 130, BUTTON_WIDTH, BUTTON_HEIGHT, "Go back", btn -> minecraft.displayGuiScreen(parent));
        children.add(goBack);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND);
        blit(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);

        int numButtons = this.buttons.size();
        for (int i = 0; i < numButtons; ++i) {
            Widget button = this.buttons.get(i);
            if (button.y < this.guiTop + HEIGHT - 40 && button.y >= this.guiTop + 25) {
                this.buttons.get(i).render(mouseX, mouseY, partialTicks);
            }
        }
        this.goBack.render(mouseX, mouseY, partialTicks);
    }


}
