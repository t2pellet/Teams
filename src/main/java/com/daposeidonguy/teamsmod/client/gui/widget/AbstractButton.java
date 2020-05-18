package com.daposeidonguy.teamsmod.client.gui.widget;

import net.minecraft.client.gui.GuiButton;

public abstract class AbstractButton extends GuiButton {

    private IPress press;

    public AbstractButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, IPress press) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.press = press;
    }

    public void activate() {
        if (this.enabled && this.visible) {
            press.onPress(this);
        }
    }

    public interface IPress {
        void onPress(GuiButton button);
    }

    public static class Basic extends AbstractButton {

        public Basic(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, IPress press) {
            super(buttonId, x, y, widthIn, heightIn, buttonText, press);
        }

    }
}
