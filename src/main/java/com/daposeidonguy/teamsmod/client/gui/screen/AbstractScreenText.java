package com.daposeidonguy.teamsmod.client.gui.screen;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;

public abstract class AbstractScreenText extends AbstractScreenBase {

    protected GuiTextField text;

    protected AbstractScreenText(final ITextComponent title, final AbstractScreenBase parent) {
        super(title, parent);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.text = new GuiTextField(GuiHandler.TEXT_FIELD, mc.fontRenderer, BUTTON_CENTERED_X, guiTop + 45, BUTTON_WIDTH, BUTTON_HEIGHT);
        text.setFocused(true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.text.drawTextBox();
        mc.fontRenderer.drawString(I18n.format("teamsmod.text.title"), CENTERED_X - mc.fontRenderer.getStringWidth(I18n.format("teamsmod.text.title")) / 2, guiTop + 35, Color.GRAY.getRGB());
    }

    @Override
    public void onResize(@Nonnull final Minecraft mcIn, int w, int h) {
        String entry = this.text.getText();
        super.onResize(mcIn, w, h);
        this.text.setText(entry);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.text.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.text.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }
}
