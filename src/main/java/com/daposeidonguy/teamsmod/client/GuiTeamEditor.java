package com.daposeidonguy.teamsmod.client;

import com.daposeidonguy.teamsmod.network.MessageInvite;
import com.daposeidonguy.teamsmod.network.MessageTeam;
import com.daposeidonguy.teamsmod.network.PacketHandler;
import com.daposeidonguy.teamsmod.team.SaveData;
import com.daposeidonguy.teamsmod.team.Team;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.awt.*;
import java.io.IOException;

public class GuiTeamEditor extends GuiScreen {

    private static final int WIDTH = 250;
    private static final int HEIGHT = 165;
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");
    private int guiTop, guiLeft;
    private static final FontRenderer fontRenderer = FMLClientHandler.instance().getClient().fontRenderer;

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.WIDTH) / 2;
        this.guiTop = (this.height - this.HEIGHT) / 2;

        this.buttonList.add(new GuiButton(Integer.MIN_VALUE+1,guiLeft + WIDTH / 2 - 60, guiTop + 40, 120,20,"Create/Manage Team"));
        this.buttonList.add(new GuiButton(Integer.MIN_VALUE+2,guiLeft + WIDTH / 2 - 60, guiTop + 70, 120,20,"List Teams"));
        this.buttonList.add(new GuiButton(Integer.MIN_VALUE+3,guiLeft + WIDTH / 2 - 60, guiTop + 100, 120,20,"Compare Stats (WIP)"));
        this.buttonList.add(new GuiButton(Integer.MIN_VALUE+4,guiLeft + WIDTH / 2 - 60, guiTop + 130,120,20,"Close menu"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(BACKGROUND);
        drawTexturedModalRect(guiLeft,guiTop,0,0,WIDTH,HEIGHT);
        GuiTeamEditor.fontRenderer.drawString("Teams GUI",guiLeft+WIDTH/2 - GuiTeamEditor.fontRenderer.getStringWidth("Teams GUI") / 2,guiTop+10,Color.BLACK.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public static class GuiTeamList extends GuiScreen {

        private int guiTop, guiLeft;

        @Override
        public void initGui() {
            super.initGui();
            this.guiLeft = (this.width - GuiTeamEditor.WIDTH) / 2;
            this.guiTop = (this.height - GuiTeamEditor.HEIGHT) / 2;

            this.buttonList.add(new GuiButton(Integer.MIN_VALUE+4,guiLeft + WIDTH / 2 - 60, guiTop + 130,120,20,"Close menu"));
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawDefaultBackground();
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(BACKGROUND);
            drawTexturedModalRect(guiLeft,guiTop,0,0,WIDTH,HEIGHT);
            GuiTeamEditor.fontRenderer.drawString("Teams List",guiLeft+WIDTH/2 - GuiTeamEditor.fontRenderer.getStringWidth("Teams List") / 2,guiTop+10,Color.BLACK.getRGB());
            int yoffset = 30;
            for(Team team : SaveData.listTeams) {
                GuiTeamEditor.fontRenderer.drawString(team.getName(),guiLeft+WIDTH/2 - GuiTeamEditor.fontRenderer.getStringWidth(team.getName()),guiTop+yoffset,Color.GRAY.getRGB());
                yoffset+=15;
            }

            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    public static class GuiTeamManager1 extends GuiScreen {

        private int guiTop, guiLeft;
        private GuiTextField text;
        private GuiButton button;

        @Override
        public void initGui() {
            super.initGui();
            this.guiLeft = (this.width - GuiTeamEditor.WIDTH) / 2;
            this.guiTop = (this.height - GuiTeamEditor.HEIGHT) / 2;

            this.text = new GuiTextField(Integer.MIN_VALUE+6,FMLClientHandler.instance().getClient().fontRenderer,guiLeft + WIDTH / 2 - 60,guiTop+50,120,20);
            this.text.setFocused(true);

            this.button = new GuiButton(Integer.MIN_VALUE+5,guiLeft + WIDTH / 2 - 60,guiTop + 75, 120,20,"Invite Player");
            this.buttonList.add(this.button);
            this.buttonList.add(new GuiButton(Integer.MIN_VALUE+4,guiLeft + WIDTH / 2 - 60,guiTop + 130,120,20,"Close menu"));
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawDefaultBackground();
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(BACKGROUND);
            drawTexturedModalRect(guiLeft,guiTop,0,0,WIDTH,HEIGHT);
            GuiTeamEditor.fontRenderer.drawString("Team Manager",guiLeft+WIDTH/2 - GuiTeamEditor.fontRenderer.getStringWidth("Team Manager") / 2,guiTop+10,Color.BLACK.getRGB());
            GuiTeamEditor.fontRenderer.drawString("Set Name",guiLeft+WIDTH/2 - GuiTeamEditor.fontRenderer.getStringWidth("Set Name") /2,guiTop+40,Color.GRAY.getRGB());
            this.text.drawTextBox();

            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            super.keyTyped(typedChar, keyCode);
            this.text.textboxKeyTyped(typedChar,keyCode);
        }

        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            this.text.mouseClicked(mouseX,mouseY,mouseButton);
            if(this.button.isMouseOver()) {
                PacketHandler.INSTANCE.sendToServer(new MessageInvite(this.text.getText(),FMLClientHandler.instance().getClientPlayerEntity().getUniqueID()));
                FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString("Invited player " + "\"" + this.text.getText() + "\""));
                FMLClientHandler.instance().getClient().displayGuiScreen(null);
            }
        }
    }


    public static class GuiTeamManager extends GuiScreen {

        private int guiTop, guiLeft;
        private GuiTextField text;
        private GuiButton button;

        @Override
        public void initGui() {
            super.initGui();
            this.guiLeft = (this.width - GuiTeamEditor.WIDTH) / 2;
            this.guiTop = (this.height - GuiTeamEditor.HEIGHT) / 2;

            this.text = new GuiTextField(Integer.MIN_VALUE+6,FMLClientHandler.instance().getClient().fontRenderer,guiLeft + WIDTH / 2 - 60,guiTop+50,120,20);
            this.text.setFocused(true);

            this.button = new GuiButton(Integer.MIN_VALUE+5,guiLeft + WIDTH / 2 - 60,guiTop + 75, 120,20,"Create Team");
            this.buttonList.add(this.button);
            this.buttonList.add(new GuiButton(Integer.MIN_VALUE+4,guiLeft + WIDTH / 2 - 60,guiTop + 130,120,20,"Close menu"));
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawDefaultBackground();
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(BACKGROUND);
            drawTexturedModalRect(guiLeft,guiTop,0,0,WIDTH,HEIGHT);
            GuiTeamEditor.fontRenderer.drawString("Team Manager",guiLeft+WIDTH/2 - GuiTeamEditor.fontRenderer.getStringWidth("Team Manager") / 2,guiTop+10,Color.BLACK.getRGB());
            GuiTeamEditor.fontRenderer.drawString("Set Name",guiLeft+WIDTH/2 - GuiTeamEditor.fontRenderer.getStringWidth("Set Name") /2,guiTop+40,Color.GRAY.getRGB());
            this.text.drawTextBox();

            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            super.keyTyped(typedChar, keyCode);
            this.text.textboxKeyTyped(typedChar,keyCode);
        }

        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            this.text.mouseClicked(mouseX,mouseY,mouseButton);
            if(this.button.isMouseOver()) {
                PacketHandler.INSTANCE.sendToServer(new MessageTeam(this.text.getText(),FMLClientHandler.instance().getClientPlayerEntity().getUniqueID()));
                FMLClientHandler.instance().getClientPlayerEntity().sendMessage(new TextComponentString("Created team " + "\"" + this.text.getText() + "\""));
                FMLClientHandler.instance().getClient().displayGuiScreen(null);
            }
        }
    }


}