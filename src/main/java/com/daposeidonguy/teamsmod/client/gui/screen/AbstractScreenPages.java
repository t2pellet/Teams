package com.daposeidonguy.teamsmod.client.gui.screen;

import com.daposeidonguy.teamsmod.client.gui.GuiHandler;
import com.daposeidonguy.teamsmod.client.gui.widget.AbstractButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.io.IOException;

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
    protected void mouseClicked(int mouseX, int mouseY, int state) throws IOException {
        if (prevPage.mousePressed(this.mc, mouseX, mouseY)) {
            net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, prevPage, this.buttonList);
            if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
                prevPage = (AbstractButton.Image) event.getButton();
                this.selectedButton = prevPage;
                prevPage.playPressSound(this.mc.getSoundHandler());
                this.actionPerformed(prevPage);
                if (this.equals(this.mc.currentScreen))
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
            }
        }
        if (nextPage.mousePressed(this.mc, mouseX, mouseY)) {
            net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, nextPage, this.buttonList);
            if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
                nextPage = (AbstractButton.Image) event.getButton();
                this.selectedButton = nextPage;
                nextPage.playPressSound(this.mc.getSoundHandler());
                this.actionPerformed(nextPage);
                if (this.equals(this.mc.currentScreen))
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
            }
        }
        super.mouseClicked(mouseX, mouseY, state);
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
