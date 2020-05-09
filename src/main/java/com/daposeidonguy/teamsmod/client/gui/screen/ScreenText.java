package com.daposeidonguy.teamsmod.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;

public class ScreenText extends ScreenBase {

    protected TextFieldWidget text;

    protected ScreenText(StringTextComponent title, ScreenBase parent) {
        super(title, parent);
    }

    @Override
    protected void init() {
        super.init();
        this.text = new TextFieldWidget(minecraft.fontRenderer, BUTTON_CENTERED_X, guiTop + 45, BUTTON_WIDTH, BUTTON_HEIGHT, "");
        this.setFocused(text);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        this.text.render(mouseX, mouseY, partialTicks);
        minecraft.fontRenderer.drawString("Enter Name:", guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Enter Name:") / 2, guiTop + 35, Color.GRAY.getRGB());
    }

    @Override
    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        String entry = this.text.getText();
        super.resize(p_resize_1_, p_resize_2_, p_resize_3_);
        this.text.setText(entry);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        this.text.mouseClicked(mouseX, mouseY, mouseButton);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
