package com.daposeidonguy.teamsmod.client.gui.widget;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractButton extends GuiButton {

    private IPress press;

    public AbstractButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, IPress press) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.press = press;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        if (this.enabled) {
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

    public static class Image extends GuiButtonImage {

        private static IPress press;

        public Image(int buttonId, int buttonX, int buttonY, int buttonWidth, int buttonHeight, int texX, int texY, int yOffset, ResourceLocation loc, IPress press) {
            super(buttonId, buttonX, buttonY, buttonWidth, buttonHeight, texX, texY, yOffset, loc);
            this.press = press;
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            if (this.enabled && this.visible) {
                press.onPress(this);
            }
        }
    }
}
