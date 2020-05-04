package com.daposeidonguy.teamsmod.client.gui.screen;

import com.daposeidonguy.teamsmod.client.gui.screen.team.ScreenTeam;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ScreenPages extends Screen {

    protected static final int WIDTH = 250;
    protected static final int HEIGHT = 165;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");
    private static final ResourceLocation BUTTONS = new ResourceLocation("textures/gui/server_selection.png");

    protected int guiTop, guiLeft;
    protected int yoffset;
    private Button goBack;
    private Button prevPage;
    private Button nextPage;
    private int page;

    protected ScreenPages(ITextComponent titleIn) {
        super(titleIn);
        page = 0;
        yoffset = 25;
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - WIDTH) / 2;
        this.guiTop = (this.height - HEIGHT) / 2;

        goBack = new Button(guiLeft + WIDTH / 2 - 60, guiTop + 130, 120, 20, "Go back", (pressable) -> {
            minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
            minecraft.displayGuiScreen(new ScreenTeam(new StringTextComponent("Team")));
        });
        children.add(goBack);

        prevPage = new ImageButton(guiLeft + 10, guiTop + 72, 13, 20, 34, 6, 32, BUTTONS, press -> {
            if (press.visible) {
                minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
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
                minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
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
        renderBackground();
        Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND);
        blit(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);

        int numButtons = this.buttons.size();
        for (int i = 0; i < numButtons; ++i) {
            Widget button = this.buttons.get(i);
            if (button.y < this.guiTop + HEIGHT - 40 && button.y >= this.guiTop + 25) {
                this.buttons.get(i).render(mouseX, mouseY, partialTicks);
            }
        }
        this.goBack.render(mouseX, mouseY, partialTicks);
        this.prevPage.visible = this.page > 0;
        this.nextPage.visible = this.page < Math.ceil(numButtons / 4);
        if (this.prevPage.visible) {
            this.prevPage.render(mouseX, mouseY, partialTicks);
        }
        if (this.nextPage.visible) {
            this.nextPage.render(mouseX, mouseY, partialTicks);
        }
    }
}
