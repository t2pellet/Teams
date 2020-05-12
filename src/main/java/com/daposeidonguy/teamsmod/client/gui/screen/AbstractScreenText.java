package com.daposeidonguy.teamsmod.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.awt.*;

public abstract class AbstractScreenText extends AbstractScreenBase {

    protected TextFieldWidget text;

    protected AbstractScreenText(final ITextComponent title, final AbstractScreenBase parent) {
        super(title, parent);
    }

    @Override
    protected void init() {
        super.init();
        this.text = new TextFieldWidget(minecraft.fontRenderer, BUTTON_CENTERED_X, guiTop + 45, BUTTON_WIDTH, BUTTON_HEIGHT, "");
        this.setFocused(text);
    }

    @Override
    public void render(int mouseX, int mouseY, final float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        this.text.render(mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString(I18n.format("teamsmod.text.title"), CENTERED_X - (minecraft.fontRenderer.getStringWidth(I18n.format("teamsmod.text.title")) >> 1), guiTop + 35, Color.GRAY.getRGB());
    }

    @Override
    public void resize(@Nonnull final Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        String entry = this.text.getText();
        super.resize(p_resize_1_, p_resize_2_, p_resize_3_);
        this.text.setText(entry);
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, int mouseButton) {
        this.text.mouseClicked(mouseX, mouseY, mouseButton);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
