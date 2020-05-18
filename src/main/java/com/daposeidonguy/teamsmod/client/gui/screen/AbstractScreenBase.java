package com.daposeidonguy.teamsmod.client.gui.screen;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.widget.AbstractButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

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
        buttonList.clear();
        this.guiLeft = (this.width - WIDTH) / 2;
        this.guiTop = (this.height - HEIGHT) / 2;
        this.CENTERED_X = guiLeft + WIDTH / 2;
        this.BUTTON_CENTERED_X = guiLeft + (WIDTH - BUTTON_WIDTH) / 2;
        goBack = this.addButton(new GuiButton(GuiHandler.BUTTON_BACK, BUTTON_CENTERED_X, guiTop + 130, BUTTON_WIDTH, BUTTON_HEIGHT, I18n.format("teamsmod.button.back")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == GuiHandler.BUTTON_BACK) {
            mc.displayGuiScreen(parent);
        } else if (button instanceof AbstractButton) {
            ((AbstractButton) button).activate();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, final float partialTicks) {
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
        mc.fontRenderer.drawString(title.getFormattedText(), CENTERED_X - mc.fontRenderer.getStringWidth(title.getFormattedText()) / 2, guiTop + 10, Color.BLACK.getRGB());
        for (GuiButton button : buttonList) {
            if (button == goBack) {
                button.visible = true;
            } else if (button.id != GuiHandler.BUTTON_NEXTPAGE && button.id != GuiHandler.BUTTON_PREVPAGE) {
                if (button.y < this.guiTop + HEIGHT - 40 && button.y >= this.guiTop + 25) {
                    button.visible = true;
                } else {
                    button.visible = false;
                }
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }


}
