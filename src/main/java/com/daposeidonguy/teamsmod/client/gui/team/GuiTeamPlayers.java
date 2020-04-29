package com.daposeidonguy.teamsmod.client.gui.team;

import com.daposeidonguy.teamsmod.handlers.ClientEventHandler;
import com.daposeidonguy.teamsmod.team.SaveData;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class GuiTeamPlayers extends GuiScreen {
    private static final int WIDTH = 250;
    private static final int HEIGHT = 165;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");
    private int guiTop, guiLeft;
    private String name;

    public GuiTeamPlayers(String name) {
        super();
        this.name = name;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - WIDTH) / 2;
        this.guiTop = (this.height - HEIGHT) / 2;

        this.buttonList.add(new GuiButton(Integer.MIN_VALUE + 4, guiLeft + WIDTH / 2 - 60, guiTop + 130, 120, 20, "Go back"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
        try {
            mc.fontRenderer.drawString("Team Players: " + name, guiLeft + WIDTH / 2 - mc.fontRenderer.getStringWidth("Team Players: " + name) / 2, guiTop + 10, Color.BLACK.getRGB());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int yoffset = 30;
        Iterator<UUID> teamIterator = SaveData.teamsMap.get(name).iterator();
        while (teamIterator.hasNext()) {
            UUID uid = teamIterator.next();
            String playerName = "";
            if (UsernameCache.containsUUID(uid)) {
                playerName = UsernameCache.getLastKnownUsername(uid);
            } else {
                if (ClientEventHandler.idtoNameMap.containsKey(uid)) {
                    playerName = ClientEventHandler.idtoNameMap.get(uid);
                } else {
                    try {
                        playerName = FMLClientHandler.instance().getClient().getConnection().getPlayerInfo(uid).getDisplayName().toString();
                    } catch (Exception ex) {
                        playerName = "Unmet player";
                    }
                }
            }
            if (!playerName.equals("")) {
                mc.fontRenderer.drawString(playerName, guiLeft + WIDTH / 2 - mc.fontRenderer.getStringWidth(playerName) / 2, guiTop + yoffset, Color.GRAY.getRGB());
                yoffset += 15;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
