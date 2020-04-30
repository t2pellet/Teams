package com.daposeidonguy.teamsmod.client.gui.screen;

import com.daposeidonguy.teamsmod.client.ClientEventHandler;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.UsernameCache;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class GuiTeamPlayers extends Screen {
    private static final int WIDTH = 250;
    private static final int HEIGHT = 165;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");
    private int guiTop, guiLeft;
    private String name;

    public GuiTeamPlayers(ITextComponent name) {
        super(name);
        this.name = name.getString();
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
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        minecraft.getTextureManager().bindTexture(BACKGROUND);
        blit(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
        try {
            minecraft.fontRenderer.drawString("Team Players: " + name, guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Team Players: " + name) / 2, guiTop + 10, Color.BLACK.getRGB());
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
                        playerName = minecraft.getConnection().getPlayerInfo(uid).getGameProfile().getName();
                    } catch (Exception ex) {
                        playerName = "Unmet player";
                    }
                }
            }
            if (!playerName.equals("")) {
                minecraft.fontRenderer.drawString(playerName, guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth(playerName) / 2, guiTop + yoffset, Color.GRAY.getRGB());
                yoffset += 15;
            }
        }

        super.render(mouseX, mouseY, partialTicks);
    }
}
