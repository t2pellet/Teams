package com.daposeidonguy.teamsmod.client.gui.screen;

import com.daposeidonguy.teamsmod.client.gui.screen.inventory.GuiTransfer;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.inventory.ContainerTransfer;
import com.daposeidonguy.teamsmod.common.network.MessageGui;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class GuiTransferPlayers extends Screen {

    private static final int WIDTH = 250;
    private static final int HEIGHT = 165;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");
    private int guiTop, guiLeft;

    protected GuiTransferPlayers(ITextComponent title) {
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
        String name = null;
        if (SaveData.teamMap.containsKey(minecraft.player.getUniqueID())) {
            name = SaveData.teamMap.get(minecraft.player.getUniqueID());
        }
        if (name == null) {
            minecraft.displayGuiScreen(null);
            minecraft.player.sendMessage(new StringTextComponent("You are not in a team!"));
            return;
        }
        Iterator<UUID> teamIterator = SaveData.teamsMap.get(name).iterator();
        while (teamIterator.hasNext()) {
            UUID uid = teamIterator.next();
            if (uid.equals(minecraft.player.getUniqueID())) {
                if (teamIterator.hasNext()) {
                    uid = teamIterator.next();
                } else {
                    return;
                }
            }
            String otherP = minecraft.player.connection.getPlayerInfo(uid).getGameProfile().getName();
            Button button = new Button(guiLeft + WIDTH / 2 - 60, guiTop + yoffset, 120, 20, otherP, (pressable) -> {
                minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                if (!TeamConfig.disableInventoryTransfer) {
                    minecraft.displayGuiScreen(new GuiTransfer(new ContainerTransfer(0, minecraft.player.inventory, otherP), minecraft.player.inventory, new StringTextComponent("Transfer")));
                    if (EffectiveSide.get().isClient()) {
                        PacketHandler.INSTANCE.sendToServer(new MessageGui(minecraft.player.getUniqueID(), otherP));
                    }
                } else {
                    minecraft.player.sendMessage(new StringTextComponent("That feature is disabled"));
                    minecraft.displayGuiScreen(null);
                }
            });
            this.addButton(button);
            yoffset += 25;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        minecraft.getTextureManager().bindTexture(BACKGROUND);
        blit(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
        minecraft.fontRenderer.drawString("Player List", guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Player List") / 2, guiTop + 10, Color.BLACK.getRGB());
        super.render(mouseX, mouseY, partialTicks);
    }

}
