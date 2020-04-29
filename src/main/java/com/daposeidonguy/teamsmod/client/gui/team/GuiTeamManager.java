package com.daposeidonguy.teamsmod.client.gui.team;

import com.daposeidonguy.teamsmod.team.SaveData;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.awt.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

public class GuiTeamManager {
    private static final int WIDTH = 250;
    private static final int HEIGHT = 165;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");

    public static class GuiTeamEditor extends GuiScreen {

        private int guiTop, guiLeft;
        private GuiTextField text;
        private GuiButton button;
        private String name;


        public GuiTeamEditor(String name) {
            super();
            this.name = name;
        }

        @Override
        public void initGui() {
            super.initGui();
            this.guiLeft = (this.width - WIDTH) / 2;
            this.guiTop = (this.height - HEIGHT) / 2;


            this.text = new GuiTextField(Integer.MIN_VALUE + 6, FMLClientHandler.instance().getClient().fontRenderer, guiLeft + WIDTH / 2 - 60, guiTop + 45, 120, 20);
            this.text.setFocused(true);

            this.button = new GuiButton(Integer.MIN_VALUE + 5, guiLeft + WIDTH / 2 - 60, guiTop + 70, 120, 20, "Invite Player");
            this.buttonList.add(this.button);
            this.buttonList.add(new GuiButton(Integer.MIN_VALUE + 7, guiLeft + WIDTH / 2 - 60, guiTop + 100, 120, 20, "Leave Team"));
            this.buttonList.add(new GuiButton(Integer.MIN_VALUE + 4, guiLeft + WIDTH / 2 - 60, guiTop + 130, 120, 20, "Go back"));
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawDefaultBackground();
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(BACKGROUND);
            drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
            mc.fontRenderer.drawString("Team Manager: " + name, guiLeft + WIDTH / 2 - mc.fontRenderer.getStringWidth("Team Manager: " + name) / 2, guiTop + 10, Color.BLACK.getRGB());
            mc.fontRenderer.drawString("Enter Name", guiLeft + WIDTH / 2 - mc.fontRenderer.getStringWidth("Enter Name") / 2, guiTop + 35, Color.GRAY.getRGB());
            mc.fontRenderer.drawString("Eligible Players:", guiLeft + WIDTH + 42 - mc.fontRenderer.getStringWidth("Eligible Players:") / 2, guiTop + 35, Color.WHITE.getRGB());
            Iterator<EntityPlayer> uuidIterator = mc.world.playerEntities.iterator();
            int yoffset = 15;
            while (uuidIterator.hasNext()) {
                String clientName = mc.player.getDisplayNameString();
                EntityPlayer p = uuidIterator.next();
                UUID uid = p.getUniqueID();
                String playerName = p.getDisplayNameString();
                if (playerName != clientName && !SaveData.teamMap.containsKey(uid)) {
                    mc.fontRenderer.drawString(playerName, guiLeft + WIDTH + 42 - mc.fontRenderer.getStringWidth(playerName) / 2, guiTop + yoffset + 35, Color.GRAY.getRGB());
                    yoffset += 15;
                }
            }
            this.text.drawTextBox();

            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            super.keyTyped(typedChar, keyCode);
            this.text.textboxKeyTyped(typedChar, keyCode);
        }

        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            this.text.mouseClicked(mouseX, mouseY, mouseButton);
            if (this.button.isMouseOver()) {
                FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage("/teamsmod invite " + this.text.getText());
                FMLClientHandler.instance().getClient().displayGuiScreen(null);
            }
        }
    }

    public static class GuiTeamCreator extends GuiScreen {

        private int guiTop, guiLeft;
        private GuiTextField text;
        private GuiButton button;

        @Override
        public void initGui() {
            super.initGui();
            this.guiLeft = (this.width - WIDTH) / 2;
            this.guiTop = (this.height - HEIGHT) / 2;

            this.text = new GuiTextField(Integer.MIN_VALUE + 6, FMLClientHandler.instance().getClient().fontRenderer, guiLeft + WIDTH / 2 - 60, guiTop + 50, 120, 20);
            this.text.setFocused(true);

            this.button = new GuiButton(Integer.MIN_VALUE + 5, guiLeft + WIDTH / 2 - 60, guiTop + 75, 120, 20, "Create Team");
            this.buttonList.add(this.button);
            this.buttonList.add(new GuiButton(Integer.MIN_VALUE + 4, guiLeft + WIDTH / 2 - 60, guiTop + 130, 120, 20, "Go back"));
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawDefaultBackground();
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(BACKGROUND);
            drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);
            mc.fontRenderer.drawString("Team Manager", guiLeft + WIDTH / 2 - mc.fontRenderer.getStringWidth("Team Manager") / 2, guiTop + 10, Color.BLACK.getRGB());
            mc.fontRenderer.drawString("Set Name", guiLeft + WIDTH / 2 - mc.fontRenderer.getStringWidth("Set Name") / 2, guiTop + 40, Color.GRAY.getRGB());
            this.text.drawTextBox();

            mc.fontRenderer.drawString("Taken Names:", guiLeft + WIDTH + 40 - mc.fontRenderer.getStringWidth("Taken Names:") / 2, guiTop + 35, Color.WHITE.getRGB());
            Iterator<String> nameIterator = SaveData.teamsMap.keySet().iterator();
            int yoffset = 15;
            while (nameIterator.hasNext()) {
                String name = nameIterator.next();
                mc.fontRenderer.drawString(name, guiLeft + WIDTH + 40 - mc.fontRenderer.getStringWidth(name) / 2, guiTop + yoffset + 35, Color.GRAY.getRGB());
                yoffset += 15;
            }

            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            super.keyTyped(typedChar, keyCode);
            this.text.textboxKeyTyped(typedChar, keyCode);
        }

        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            this.text.mouseClicked(mouseX, mouseY, mouseButton);
            if (this.button.isMouseOver()) {
                FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage("/teamsmod create " + this.text.getText());
                FMLClientHandler.instance().getClient().displayGuiScreen(null);
            }
        }
    }
}
