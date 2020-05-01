package com.daposeidonguy.teamsmod.client.gui;

import com.daposeidonguy.teamsmod.client.gui.team.GuiTeamPlayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.List;

public class GuiScrollable extends GuiScrollingList {

    private GuiScreen parent;
    private List<GuiButton> buttonList;

    public GuiScrollable(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight, GuiScreen parent, List<GuiButton> buttonList) {
        super(client, width, height, top, bottom, left, entryHeight);
        this.parent = parent;
        this.buttonList = buttonList;
    }

    @Override
    protected int getSize() {
        return buttonList.size();
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick) {
        GuiButton button = buttonList.get(index);
        if (button.isMouseOver() && button.id == Integer.MIN_VALUE + 8 && button.enabled) {
            FMLClientHandler.instance().getClient().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeamPlayers(button.displayString));
        } else {
            super.actionPerformed(button);
        }
    }

    @Override
    protected boolean isSelected(int index) {
        return false;
    }

    @Override
    protected void drawBackground() {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        for (GuiButton button : buttonList) {
            if (button.y + 5 > this.top && button.y + 15 < this.bottom) {
                button.drawButton(parent.mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
        GuiButton button = buttonList.get(slotIdx);
        button.y = slotTop;
        button.visible = button.y + 5 > this.top && button.y + 15 < this.bottom;
        button.enabled = button.visible;
    }

    @Override
    protected void drawGradientRect(int left, int top, int right, int bottom, int color1, int color2) {
    }
}
