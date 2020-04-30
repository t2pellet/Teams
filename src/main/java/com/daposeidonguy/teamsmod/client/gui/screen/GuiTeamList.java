package com.daposeidonguy.teamsmod.client.gui.screen;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

public class GuiTeamList extends Screen {

    private static final int WIDTH = 250;
    private static final int HEIGHT = 165;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");
    private int guiTop, guiLeft;
    private List<Button> scrollList;

    protected GuiTeamList(ITextComponent title) {
        super(title);
    }

    @Override
    public void init() {
        super.init();
        this.guiLeft = (this.width - WIDTH) / 2;
        this.guiTop = (this.height - HEIGHT) / 2;

        this.addButton(new Button(guiLeft + WIDTH / 2 - 60, guiTop + 130, 120, 20, "Go back", (pressable) -> {
            minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
            minecraft.displayGuiScreen(new GuiTeam(new StringTextComponent("Team")));
        }));

        int yoffset = 30;
        Iterator<String> teamIterator = SaveData.teamsMap.keySet().iterator();
        while (teamIterator.hasNext()) {
            String team = teamIterator.next();
            Button button = new Button(guiLeft + WIDTH / 2 - 60, guiTop + yoffset, 120, 20, team, (pressable) -> {
                minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                minecraft.displayGuiScreen(new GuiTeamPlayers(new StringTextComponent(team)));
                TeamsMod.logger.debug("LALALALALA");
            });
            addButton(button);
            yoffset += 25;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND);
        blit(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
        minecraft.fontRenderer.drawString("Teams List", guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Teams List") / 2, guiTop + 10, Color.BLACK.getRGB());
        super.render(mouseX, mouseY, partialTicks);
    }

}
