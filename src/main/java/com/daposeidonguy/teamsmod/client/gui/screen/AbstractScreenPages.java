package com.daposeidonguy.teamsmod.client.gui.screen;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.widget.AbstractButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractScreenPages extends AbstractScreenBase {

    private static final ResourceLocation BUTTONS = new ResourceLocation("textures/gui/server_selection.png");

    protected int yOffset;
    private AbstractButton.Image prevPage;
    private AbstractButton.Image nextPage;
    private int page;

    protected AbstractScreenPages(final ITextComponent titleIn, final AbstractScreenBase parent) {
        super(titleIn, parent);
        page = 0;
    }

    @Override
    public void initGui() {
        super.initGui();
        yOffset = 25;
        prevPage = new AbstractButton.Image(GuiHandler.BUTTON_PREVPAGE, guiLeft + 10, guiTop + 72, 13, 20, 34, 6, 32, BUTTONS, press -> {
            if (press.visible) {
                for (GuiButton button : this.buttonList) {
                    if (isButtonInList(button)) button.y += 100;
                }
                this.page -= 1;
            }
        });
        nextPage = new AbstractButton.Image(GuiHandler.BUTTON_NEXTPAGE, guiLeft + WIDTH - 23, guiTop + 72, 13, 20, 17, 6, 32, BUTTONS, press -> {
            for (GuiButton button : this.buttonList) {
                if (isButtonInList(button)) button.y -= 100;
            }
            this.page += 1;
        });
    }

    private boolean isButtonInList(final GuiButton button) {
        return button.width != 13 && button != goBack;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.prevPage.visible = this.page > 0;
        this.nextPage.visible = this.page < Math.ceil(this.buttonList.size() >> 2);
        if (this.prevPage.visible) {
            this.prevPage.drawButton(mc, mouseX, mouseY, partialTicks);
        }
        if (this.nextPage.visible) {
            this.nextPage.drawButton(mc, mouseX, mouseY, partialTicks);
        }
    }
}
