package com.daposeidonguy.teamsmod.client.gui.screen.team;

import com.daposeidonguy.teamsmod.client.ClientUtils;
import com.daposeidonguy.teamsmod.client.gui.screen.ScreenBase;
import com.daposeidonguy.teamsmod.client.gui.widget.ClearButton;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class ScreenTeamManager {

    public static class GuiTeamEditor extends ScreenBase {

        private TextFieldWidget text;
        private Button button;
        private String name;


        public GuiTeamEditor(ScreenBase parent, String name) {
            super(new StringTextComponent("teameditor"), new ScreenTeam());
            this.name = name;
        }

        @Override
        public void init() {
            super.init();

            this.text = new TextFieldWidget(minecraft.fontRenderer, guiLeft + WIDTH / 2 - 60, guiTop + 45, 120, 20, "");
            this.setFocused(text);

            this.button = new Button(guiLeft + WIDTH / 2 - 60, guiTop + 70, 120, 20, "Invite Player", (pressable) -> {
                minecraft.player.sendChatMessage("/teamsmod invite " + this.text.getText());
                minecraft.displayGuiScreen(null);
            });
            this.addButton(this.button);
            this.addButton(new Button(guiLeft + WIDTH / 2 - 60, guiTop + 100, 120, 20, "Leave Team", (pressable) -> {
                minecraft.player.sendChatMessage("/teamsmod leave");
                minecraft.displayGuiScreen(null);
            }));

            Iterator<NetworkPlayerInfo> infoIterator = minecraft.getConnection().getPlayerInfoMap().iterator();
            String clientName = minecraft.player.getGameProfile().getName();
            int yoffset = 15;
            while (infoIterator.hasNext()) {
                UUID uid = infoIterator.next().getGameProfile().getId();
                String playerName = ClientUtils.getOnlineUsernameFromUUID(uid);
                if (playerName != clientName && !SaveData.teamMap.containsKey(uid)) {
                    int width = minecraft.fontRenderer.getStringWidth(playerName);
                    addButton(new ClearButton(guiLeft + WIDTH + 42 - width / 2, guiTop + yoffset + 35, width, 10, 0, 0, 0, playerName, btn -> this.text.setText(btn.getMessage())));
                    yoffset += 15;
                }
            }
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTicks) {
            super.render(mouseX, mouseY, partialTicks);
            minecraft.fontRenderer.drawString("Team Manager: " + name, guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Team Manager: " + name) / 2, guiTop + 10, Color.BLACK.getRGB());
            minecraft.fontRenderer.drawString("Enter Name", guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Enter Name") / 2, guiTop + 35, Color.GRAY.getRGB());
            minecraft.fontRenderer.drawString("Eligible Players:", guiLeft + WIDTH + 42 - minecraft.fontRenderer.getStringWidth("Eligible Players:") / 2, guiTop + 35, Color.WHITE.getRGB());
            this.text.render(mouseX, mouseY, partialTicks);
        }

        @Override
        public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
            String entry = this.text.getText();
            super.resize(p_resize_1_, p_resize_2_, p_resize_3_);
            this.text.setText(entry);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            this.text.mouseClicked(mouseX, mouseY, mouseButton);
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public static class GuiTeamCreator extends ScreenBase {

        private TextFieldWidget text;
        private Button button;

        protected GuiTeamCreator(ScreenBase parent) {
            super(new StringTextComponent("teamcreator"), parent);
        }

        @Override
        public void init() {
            super.init();

            this.text = new TextFieldWidget(minecraft.fontRenderer, guiLeft + WIDTH / 2 - 60, guiTop + 50, 120, 20, "");
            this.setFocused(text);

            this.button = new Button(guiLeft + WIDTH / 2 - 60, guiTop + 75, 120, 20, "Create Team", (pressable) -> {
                minecraft.player.sendChatMessage("/teamsmod create " + this.text.getText());
                minecraft.displayGuiScreen(null);
            });
            this.addButton(this.button);
            this.addButton(new Button(guiLeft + WIDTH / 2 - 60, guiTop + 130, 120, 20, "Go back", (pressable) -> {
                minecraft.displayGuiScreen(new ScreenTeam());
            }));
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTicks) {
            super.render(mouseX, mouseY, partialTicks);
            minecraft.fontRenderer.drawString("Team Manager", guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Team Manager") / 2, guiTop + 10, Color.BLACK.getRGB());
            minecraft.fontRenderer.drawString("Set Name", guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Set Name") / 2, guiTop + 40, Color.GRAY.getRGB());
            this.text.render(mouseX, mouseY, partialTicks);

            minecraft.fontRenderer.drawString("Taken Names:", guiLeft + WIDTH + 40 - minecraft.fontRenderer.getStringWidth("Taken Names:") / 2, guiTop + 35, Color.WHITE.getRGB());
            Iterator<String> nameIterator = SaveData.teamsMap.keySet().iterator();
            int yoffset = 15;
            while (nameIterator.hasNext()) {
                String name = nameIterator.next();
                minecraft.fontRenderer.drawString(name, guiLeft + WIDTH + 40 - minecraft.fontRenderer.getStringWidth(name) / 2, guiTop + yoffset + 35, Color.GRAY.getRGB());
                yoffset += 15;
            }

        }

        @Override
        public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
            String entry = this.text.getText();
            super.resize(p_resize_1_, p_resize_2_, p_resize_3_);
            this.text.setText(entry);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            this.text.mouseClicked(mouseX, mouseY, mouseButton);
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }
}
