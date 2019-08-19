package com.daposeidonguy.teamsmod.client;

import com.daposeidonguy.teamsmod.team.SaveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

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
        this.buttonList.add(new GuiButton(Integer.MIN_VALUE+3,guiLeft + WIDTH / 2 - 60, guiTop + 100, 120,20,"Unimplemented (WIP)"));
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

    public static class GuiTeamInfo extends GuiScreen {
        private int guiTop, guiLeft;
        private String name;

        public GuiTeamInfo(String name) {
            this.name = name;
        }

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
            GuiTeamEditor.fontRenderer.drawString("Online Players: " + name,guiLeft+WIDTH/2 - GuiTeamEditor.fontRenderer.getStringWidth("Online Players: " + name) / 2,guiTop+10,Color.BLACK.getRGB());
            int yoffset = 30;
            Iterator<UUID> teamIterator = SaveData.teamsMap.get(name).iterator();
            while(teamIterator.hasNext()) {
                EntityPlayer p = mc.world.getPlayerEntityByUUID(teamIterator.next());
                if(p!=null) {
                    String playerName = p.getDisplayNameString();
                    GuiTeamEditor.fontRenderer.drawString(playerName,guiLeft+WIDTH/2 - GuiTeamEditor.fontRenderer.getStringWidth(playerName) / 2,guiTop+yoffset,Color.GRAY.getRGB());
                    yoffset+=15;
                }
            }

            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    public static class GuiTeamList extends GuiScreen {

        private int guiTop, guiLeft;
        private GuiScrollingList buttonscrollist;


        @Override
        public void initGui() {
            super.initGui();
            this.guiLeft = (this.width - GuiTeamEditor.WIDTH) / 2;
            this.guiTop = (this.height - GuiTeamEditor.HEIGHT) / 2;

            this.buttonList.add(new GuiButton(Integer.MIN_VALUE+4,guiLeft + WIDTH / 2 - 60, guiTop + 130,120,20,"Close menu"));

            List<GuiButton> scrollList = new ArrayList<>();
            int yoffset = 30;
            Iterator<String> teamIterator = SaveData.teamsMap.keySet().iterator();
            while(teamIterator.hasNext()) {
                String team = teamIterator.next();
                GuiButton button = new GuiButton(Integer.MIN_VALUE+8,guiLeft+WIDTH / 2 - 60, guiTop + yoffset,120,20,team);
                scrollList.add(button);
                yoffset+=25;
            }

            buttonscrollist = new GuiTeamScroll(mc,242,100,this.guiTop+25,this.guiTop+125,guiLeft+3,25,this,scrollList);
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(BACKGROUND);
            drawTexturedModalRect(guiLeft,guiTop,0,0,WIDTH,HEIGHT);
            GuiTeamEditor.fontRenderer.drawString("Teams List",guiLeft+WIDTH/2 - GuiTeamEditor.fontRenderer.getStringWidth("Teams List") / 2,guiTop+10,Color.BLACK.getRGB());
            super.drawScreen(mouseX, mouseY, partialTicks);
            buttonscrollist.drawScreen(mouseX,mouseY,partialTicks);
        }

    }

    public static class GuiTeamManager1 extends GuiScreen {

        private int guiTop, guiLeft;
        private GuiTextField text;
        private GuiButton button;
        private String name;


        public GuiTeamManager1(String name) {
            this.name = name;
        }

        @Override
        public void initGui() {
            super.initGui();
            this.guiLeft = (this.width - GuiTeamEditor.WIDTH) / 2;
            this.guiTop = (this.height - GuiTeamEditor.HEIGHT) / 2;


            this.text = new GuiTextField(Integer.MIN_VALUE+6,FMLClientHandler.instance().getClient().fontRenderer,guiLeft + WIDTH / 2 - 60,guiTop+45,120,20);
            this.text.setFocused(true);

            this.button = new GuiButton(Integer.MIN_VALUE+5,guiLeft + WIDTH / 2 - 60,guiTop + 70, 120,20,"Invite Player");
            this.buttonList.add(this.button);
            this.buttonList.add(new GuiButton(Integer.MIN_VALUE+7,guiLeft+WIDTH/2-60,guiTop+100,120,20,"Leave Team"));
            this.buttonList.add(new GuiButton(Integer.MIN_VALUE+4,guiLeft + WIDTH / 2 - 60,guiTop + 130,120,20,"Close menu"));
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawDefaultBackground();
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(BACKGROUND);
            drawTexturedModalRect(guiLeft,guiTop,0,0,WIDTH,HEIGHT);
            GuiTeamEditor.fontRenderer.drawString("Team Manager: " + name,guiLeft+WIDTH/2 - GuiTeamEditor.fontRenderer.getStringWidth("Team Manager: " + name) / 2,guiTop+10,Color.BLACK.getRGB());
            GuiTeamEditor.fontRenderer.drawString("Set Name",guiLeft+WIDTH/2 - GuiTeamEditor.fontRenderer.getStringWidth("Set Name") /2,guiTop+35,Color.GRAY.getRGB());
            GuiTeamEditor.fontRenderer.drawString("Players List:",guiLeft+WIDTH+40 - GuiTeamEditor.fontRenderer.getStringWidth("Players List:") / 2,guiTop+35,Color.WHITE.getRGB());
            Iterator<EntityPlayer> uuidIterator = mc.world.playerEntities.iterator();
            int yoffset=15;
            while(uuidIterator.hasNext()) {
                String clientName = mc.player.getDisplayNameString();
                String playerName = uuidIterator.next().getDisplayNameString();
                if(playerName!=clientName) {
                    GuiTeamEditor.fontRenderer.drawString(playerName, guiLeft + WIDTH + 40 - GuiTeamEditor.fontRenderer.getStringWidth(playerName) / 2, guiTop + yoffset + 35, Color.GRAY.getRGB());
                    yoffset += 15;
                }
            }
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
                FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage("/team invite " + this.text.getText());
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

            GuiTeamEditor.fontRenderer.drawString("Taken Names:",guiLeft+WIDTH+40 - GuiTeamEditor.fontRenderer.getStringWidth("Taken Names:") /2,guiTop+35, Color.WHITE.getRGB());
            Iterator<String> nameIterator = SaveData.teamsMap.keySet().iterator();
            int yoffset=15;
            while(nameIterator.hasNext()) {
                String name = nameIterator.next();
                GuiTeamEditor.fontRenderer.drawString(name,guiLeft+WIDTH+40 - GuiTeamEditor.fontRenderer.getStringWidth(name) / 2,guiTop+yoffset+35,Color.GRAY.getRGB());
                yoffset+=15;
            }

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
                FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage("/team create " + this.text.getText());
                FMLClientHandler.instance().getClient().displayGuiScreen(null);
            }
        }
    }


    public static class GuiTeamScroll extends GuiScrollingList {

        private GuiScreen parent;
        private List<GuiButton> buttonList;

        public GuiTeamScroll(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight, GuiScreen parent, List<GuiButton> buttonList) {
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
            if (button.isMouseOver() && button.id == Integer.MIN_VALUE+8 && button.enabled) {
                FMLClientHandler.instance().getClient().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                FMLClientHandler.instance().getClient().displayGuiScreen(new GuiTeamInfo(button.displayString));
            }else {
                super.actionPerformed(button);
            }
        }

        @Override
        protected boolean isSelected(int index) {
            return false;
        }

        @Override
        protected void drawBackground() {}

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            super.drawScreen(mouseX, mouseY, partialTicks);
            for(GuiButton button : buttonList) {
                if( button.y+5 > this.top && button.y + 15 < this.bottom) {
                    button.drawButton(parent.mc,mouseX,mouseY,partialTicks);
                }
            }
        }

        @Override
        protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
            GuiButton button = buttonList.get(slotIdx);
            button.y = slotTop;
            button.visible = button.y+5 > this.top && button.y + 15 < this.bottom;
            button.enabled = button.visible;
        }

        @Override
        protected void drawGradientRect(int left, int top, int right, int bottom, int color1, int color2) {
        }
    }
}
