package com.daposeidonguy.teamsmod.client.gui.screen;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractScreenPages extends AbstractScreenBase {

    private static final ResourceLocation BUTTONS = new ResourceLocation("textures/gui/server_selection.png");

    protected int yOffset;
    private Button prevPage;
    private Button nextPage;
    private int page;

    protected AbstractScreenPages(ITextComponent titleIn, AbstractScreenBase parent) {
        super(titleIn, parent);
        page = 0;
    }

    @Override
    protected void init() {
        super.init();
        yOffset = 25;
        prevPage = new ImageButton(guiLeft + 10, guiTop + 72, 13, 20, 34, 6, 32, BUTTONS, press -> {
            if (press.visible) {
                for (Widget button : this.buttons) {
                    if (button.getWidth() != 13 && button != goBack) {
                        button.y += 100;
                    }
                }
                this.page -= 1;
            }
        });
        children.add(prevPage);
        nextPage = new ImageButton(guiLeft + WIDTH - 23, guiTop + 72, 13, 20, 17, 6, 32, BUTTONS, press -> {
            if (press.visible) {
                for (Widget button : this.buttons) {
                    if (button.getWidth() != 13 && button != goBack) {
                        button.y -= 100;
                    }
                }
                this.page += 1;
            }
        });
        children.add(nextPage);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        this.prevPage.visible = this.page > 0;
        this.nextPage.visible = this.page < Math.ceil(this.buttons.size() >> 2);
        if (this.prevPage.visible) {
            this.prevPage.render(mouseX, mouseY, partialTicks);
        }
        if (this.nextPage.visible) {
            this.nextPage.render(mouseX, mouseY, partialTicks);
        }
    }
}
