package com.daposeidonguy.teamsmod.client.gui.team;

import com.daposeidonguy.teamsmod.team.SaveData;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class GuiTransferPlayers extends GuiScreen {

    private static final int WIDTH = 250;
    private static final int HEIGHT = 165;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");
    private int guiTop, guiLeft;


    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - WIDTH) / 2;
        this.guiTop = (this.height - HEIGHT) / 2;

        this.buttonList.add(new GuiButton(Integer.MIN_VALUE + 4, guiLeft + WIDTH / 2 - 60, guiTop + 130, 120, 20, "Go back"));

        List<GuiButton> scrollList = new ArrayList<>();
        int yoffset = 30;
        String name = null;
        if (SaveData.teamMap.containsKey(FMLClientHandler.instance().getClientPlayerEntity().getUniqueID())) {
            name = SaveData.teamMap.get(FMLClientHandler.instance().getClientPlayerEntity().getUniqueID());
        }
        if (name == null) {
            FMLClientHandler.instance().getClient().displayGuiScreen(null);
            FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString("You are not in a team!"));
            return;
        }
        Iterator<UUID> teamIterator = SaveData.teamsMap.get(name).iterator();
        while (teamIterator.hasNext()) {
            UUID uid = teamIterator.next();
            if (uid.equals(mc.player.getUniqueID())) {
                if (teamIterator.hasNext()) {
                    uid = teamIterator.next();
                } else {
                    return;
                }
            }
            NetworkPlayerInfo info = mc.getConnection().getPlayerInfo(uid);
            if (info != null) {
                String otherP = info.getGameProfile().getName();
                GuiButton button = new GuiButton(Integer.MIN_VALUE + 9, guiLeft + WIDTH / 2 - 60, guiTop + yoffset, 120, 20, otherP);
                addButton(button);
                yoffset += 25;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
        mc.fontRenderer.drawString("Player List", guiLeft + WIDTH / 2 - mc.fontRenderer.getStringWidth("Player List") / 2, guiTop + 10, Color.BLACK.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
