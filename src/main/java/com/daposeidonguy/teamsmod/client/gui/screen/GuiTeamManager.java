package com.daposeidonguy.teamsmod.client.gui.screen;

import com.daposeidonguy.teamsmod.common.storage.SaveData;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class GuiTeamManager {
    private static final int WIDTH = 250;
    private static final int HEIGHT = 165;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");

    public static class GuiTeamEditor extends Screen {

        private int guiTop, guiLeft;
        private TextFieldWidget text;
        private Button button;
        private String name;


        public GuiTeamEditor(ITextComponent name) {
            super(name);
            this.name = name.getString();
        }

        @Override
        public void init() {
            super.init();
            this.guiLeft = (this.width - WIDTH) / 2;
            this.guiTop = (this.height - HEIGHT) / 2;


            this.text = new TextFieldWidget(minecraft.fontRenderer, guiLeft + WIDTH / 2 - 60, guiTop + 45, 120, 20, "");
            this.text.setFocused2(true);

            this.button = new Button(guiLeft + WIDTH / 2 - 60, guiTop + 70, 120, 20, "Invite Player", (pressable) -> {
                minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                minecraft.player.sendChatMessage("/teamsmod invite " + this.text.getText());
                minecraft.displayGuiScreen(null);
            });
            this.addButton(this.button);
            this.addButton(new Button(guiLeft + WIDTH / 2 - 60, guiTop + 100, 120, 20, "Leave Team", (pressable) -> {
                minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                minecraft.player.sendChatMessage("/teamsmod leave");
                minecraft.displayGuiScreen(null);
            }));
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
            minecraft.fontRenderer.drawString("Team Manager: " + name, guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Team Manager: " + name) / 2, guiTop + 10, Color.BLACK.getRGB());
            minecraft.fontRenderer.drawString("Enter Name", guiLeft + WIDTH / 2 - minecraft.fontRenderer.getStringWidth("Enter Name") / 2, guiTop + 35, Color.GRAY.getRGB());
            minecraft.fontRenderer.drawString("Eligible Players:", guiLeft + WIDTH + 42 - minecraft.fontRenderer.getStringWidth("Eligible Players:") / 2, guiTop + 35, Color.WHITE.getRGB());
            Iterator<AbstractClientPlayerEntity> uuidIterator = minecraft.world.getPlayers().iterator();
            int yoffset = 15;
            while (uuidIterator.hasNext()) {
                String clientName = minecraft.player.getGameProfile().getName();
                AbstractClientPlayerEntity p = uuidIterator.next();
                UUID uid = p.getUniqueID();
                String playerName = p.getGameProfile().getName();
                if (playerName != clientName && !SaveData.teamMap.containsKey(uid)) {
                    minecraft.fontRenderer.drawString(playerName, guiLeft + WIDTH + 42 - minecraft.fontRenderer.getStringWidth(playerName) / 2, guiTop + yoffset + 35, Color.GRAY.getRGB());
                    yoffset += 15;
                }
            }
            this.text.render(mouseX, mouseY, partialTicks);
            super.render(mouseX, mouseY, partialTicks);
        }

        @Override
        public boolean charTyped(char typedChar, int keyCode) {
            this.text.charTyped(typedChar, keyCode);
            return super.charTyped(typedChar, keyCode);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            this.text.mouseClicked(mouseX, mouseY, mouseButton);
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public static class GuiTeamCreator extends Screen {

        private int guiTop, guiLeft;
        private TextFieldWidget text;
        private Button button;

        protected GuiTeamCreator(ITextComponent title) {
            super(title);
        }

        @Override
        public void init() {
            super.init();
            this.guiLeft = (this.width - WIDTH) / 2;
            this.guiTop = (this.height - HEIGHT) / 2;

            this.text = new TextFieldWidget(minecraft.fontRenderer, guiLeft + WIDTH / 2 - 60, guiTop + 50, 120, 20, "");
            this.text.setFocused2(true);

            this.button = new Button(guiLeft + WIDTH / 2 - 60, guiTop + 75, 120, 20, "Create Team", (pressable) -> {
                minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F, 1.0F);
                minecraft.player.sendChatMessage("/teamsmod create " + this.text.getText());
                minecraft.displayGuiScreen(null);
            });
            this.addButton(this.button);
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

            super.render(mouseX, mouseY, partialTicks);
        }

        @Override
        public boolean charTyped(char typedChar, int keyCode) {
            this.text.charTyped(typedChar, keyCode);
            return super.charTyped(typedChar, keyCode);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            this.text.mouseClicked(mouseX, mouseY, mouseButton);
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }
}
