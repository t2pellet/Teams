package com.daposeidonguy.teamsmod.client.gui.screen;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractScreenPages extends AbstractScreenBase {

    private static final ResourceLocation BUTTONS = new ResourceLocation("textures/gui/server_selection.png");

    protected int yOffset;
    private int page;
    private GuiButton prevPage;
    private GuiButton nextPage;

    public AbstractScreenPages(ITextComponent titleIn, AbstractScreenBase parent) {
        super(titleIn, parent);
    }

    @Override
    public void initGui() {
        super.initGui();
        yOffset = 25;
        page = 0;
        prevPage = this.addButton(new GuiButtonImage(GuiHandler.BUTTON_PREVPAGE, guiLeft + 10, guiTop + 72, 13, 20, 34, 6, 32, BUTTONS));
        nextPage = this.addButton(new GuiButtonImage(GuiHandler.BUTTON_NEXTPAGE, guiLeft + WIDTH - 23, guiTop + 72, 13, 20, 17, 6, 32, BUTTONS));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == GuiHandler.BUTTON_PREVPAGE) {
            for (GuiButton listButton : buttonList) {
                if (listButton.id != prevPage.id && listButton.id != nextPage.id && listButton.id != goBack.id) {
                    listButton.y += 100;
                }
            }
            --page;
        } else if (button.id == GuiHandler.BUTTON_NEXTPAGE) {
            for (GuiButton listButton : buttonList) {
                if (listButton.id != prevPage.id && listButton.id != nextPage.id && listButton.id != goBack.id) {
                    listButton.y -= 100;
                }
            }
            ++page;
        }
        super.actionPerformed(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        prevPage.visible = page > 0;
        nextPage.visible = page < (buttonList.size() - 3) / 4 && (buttonList.size() - 3) % 4 > 0;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
