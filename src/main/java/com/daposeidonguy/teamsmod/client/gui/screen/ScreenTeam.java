package com.daposeidonguy.teamsmod.client.gui.screen;

import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;


public class ScreenTeam extends Screen {

    private static final int WIDTH = 250;
    private static final int HEIGHT = 165;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");
    private static final FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    private int guiTop, guiLeft;

    public ScreenTeam(ITextComponent title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - WIDTH) / 2;
        this.guiTop = (this.height - HEIGHT) / 2;

        this.addButton(new Button(guiLeft + WIDTH / 2 - 60, guiTop + 40, 120, 20, "Create/Manage Team", (button) -> {
            minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
            if (SaveData.teamMap.containsKey(minecraft.player.getUniqueID())) {
                minecraft.displayGuiScreen(new ScreenTeamManager.GuiTeamEditor(new StringTextComponent(SaveData.teamMap.get(minecraft.player.getUniqueID()))));
            } else {
                minecraft.displayGuiScreen(new ScreenTeamManager.GuiTeamCreator(new StringTextComponent("Creator")));
            }
        }));
        this.addButton(new Button(guiLeft + WIDTH / 2 - 60, guiTop + 70, 120, 20, "List Teams", (button) -> {
            minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
            minecraft.displayGuiScreen(new ScreenTeamList(new StringTextComponent("Team List")));
        }));
        this.addButton(new Button(guiLeft + WIDTH / 2 - 60, guiTop + 100, 120, 20, "Transfer Items", (button) -> {
            minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
            minecraft.displayGuiScreen(new ScreenTransferPlayers(new StringTextComponent("Transfer Item")));
        }));
        this.addButton(new Button(guiLeft + WIDTH / 2 - 60, guiTop + 130, 120, 20, "Close menu", (button) -> {
            minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
            minecraft.displayGuiScreen(null);
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND);
        blit(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
        ScreenTeam.fontRenderer.drawString("Teams GUI", guiLeft + WIDTH / 2 - ScreenTeam.fontRenderer.getStringWidth("Teams GUI") / 2, guiTop + 10, Color.BLACK.getRGB());
        super.render(mouseX, mouseY, partialTicks);
    }
}
